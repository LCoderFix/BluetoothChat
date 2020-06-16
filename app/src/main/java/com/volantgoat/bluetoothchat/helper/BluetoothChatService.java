package com.volantgoat.bluetoothchat.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.volantgoat.bluetoothchat.bean.MessageEvent;
import com.volantgoat.bluetoothchat.view.ChatActivity;
import com.volantgoat.bluetoothchat.view.MainActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.volantgoat.bluetoothchat.view.MainActivity.BLUE_TOOTH_IMAGE_READ_SUCCESS;
import static com.volantgoat.bluetoothchat.view.MainActivity.BLUE_TOOTH_READ;

/**
 * 每端均充作服务端与客户端
 * Create by dong
 * Data:2019/12/11
 */
public class BluetoothChatService {
    // 调试
    private static final String TAG = "BluetoothChatService";

    // 建立连接名称
    private static final String NAME_SECURE = "BluetoothChatSecure";
    //UUID
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    // 显示当前连接状态
    private int mState;
    // 什么都不做
    public static final int STATE_NONE = 0;
    // 监听连接
    public static final int STATE_LISTEN = 1;
    // 正在建立连接
    public static final int STATE_CONNECTING = 2;
    // 现在连接到一个远程的设备，可以进行传输
    public static final int STATE_TRANSFER = 3;

    //等待连接
    private AcceptThread mAcceptThread;
    //连接线程
    private ConnectThread mConnectThread;
    //传输线程
    private TransferThread mTransferThread;

    private BluetoothAdapter bluetoothAdapter;
    private boolean isTransferError = false;

    //图片开始标识
    private String IMAGE_START = "image:";

    //图片消息结束标识
    private String IMAGE_END = "over";

    private String FILE_NAME_END = "?";
    private String DEFAULT_ENCODE = "UTF-8";

    //获取单例
    public static volatile BluetoothChatService instance = null;
    private String imgUrl;
    private Context mContext;
    private String ISO_ENCODE="ISO-8859-1";

    public static BluetoothChatService getInstance() {

        if (instance == null) {
            synchronized (BluetoothChatService.class) {
                if (instance == null) {
                    instance = new BluetoothChatService();
                }
            }
        }
        return instance;
    }

    public BluetoothChatService() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }

    /**
     * 开启服务监听
     */
    public synchronized void start() {
        if (mTransferThread != null) {
            mTransferThread.cancel();
            mTransferThread = null;
        }

        setState(STATE_LISTEN);

        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.e(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        if (mTransferThread != null) {
            mTransferThread.cancel();
            mTransferThread = null;
        }

        setState(STATE_NONE);
    }

    public void setState(int state) {
        this.mState = state;
    }

    /**
     * 连接访问
     * @param device
     */
    public synchronized void connectDevice(BluetoothDevice device) {
        // 如果有正在传输的则先关闭
        if (mState == STATE_CONNECTING) {
            if (mTransferThread != null) {
                mTransferThread.cancel();
                mTransferThread = null;
            }
        }

        //如果有正在连接的则先关闭
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        EventBus.getDefault().post(new MessageEvent(MainActivity.class, MainActivity.BLUE_TOOTH_DIALOG, "正在与" + device.getName() + "连接"));
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        //标志为正在连接
        setState(STATE_CONNECTING);
    }

    //连接等待线程
    class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            //获取服务器监听端口
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket = tmp;
        }

        @Override
        public void run() {
            super.run();
            //监听端口
            BluetoothSocket socket = null;
            while (mState != STATE_TRANSFER) {
                try {
                    Log.e(TAG, "run: AcceptThread 阻塞调用，等待连接");
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                //获取到连接Socket后则开始通信
                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                //传输数据，服务器端调用
                                Log.e(TAG, "run: 服务器AcceptThread传输");
                                EventBus.getDefault().post(new MessageEvent(MainActivity.class, MainActivity.BLUE_TOOTH_DIALOG, "正在与" + socket.getRemoteDevice().getName() + "连接"));
                                //    sendMessageToUi(MainActivity.BLUE_TOOTH_DIALOG , "正在与" + socket.getRemoteDevice().getName() + "连接");
                                dataTransfer(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_TRANSFER:
                                // 没有准备好或者终止连接
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "无法关闭连接" + e);
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 开始连接通讯
     *
     * @param socket
     * @param remoteDevice 远程设备
     */
    private void dataTransfer(BluetoothSocket socket, final BluetoothDevice remoteDevice) {
        //关闭连接线程，这里只能连接一个远程设备
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // 启动管理连接线程和开启传输
        mTransferThread = new TransferThread(socket);
        mTransferThread.start();
        //标志状态为连接
        setState(STATE_TRANSFER);
        EventBus.getDefault().post(new MessageEvent(MainActivity.class, MainActivity.BLUE_TOOTH_SUCCESS, remoteDevice.getName()));

    }

    /**
     * 传输数据
     *
     * @param out
     */
    public void sendData(byte[] out) {
        //TODO 到到UI显示
        //    EventBus.getDefault().post(new MessageEvent(ChatActivity.class,MainActivity.BLUE_TOOTH_WRAITE,new String(out)));
        TransferThread r;
        synchronized (this) {
            if (mState != STATE_TRANSFER) return;
            r = mTransferThread;
        }
        r.write(out);
    }

    public void sendData(byte[] out, int start, int end) {
        //TODO 到到UI显示
        // EventBus.getDefault().post(new MessageEvent(ChatActivity.class,MainActivity.BLUE_TOOTH_WRAITE,new String(out)));
        TransferThread r;
        synchronized (this) {
            if (mState != STATE_TRANSFER) return;
            r = mTransferThread;
        }
        r.write(out, start, end);
    }

    /**
     * 用来传输数据的线程
     */
    class TransferThread extends Thread {
        private final BluetoothSocket socket;
        private final OutputStream out;
        private final InputStream in;

        public TransferThread(BluetoothSocket mBluetoothSocket) {
            socket = mBluetoothSocket;
            OutputStream mOutputStream = null;
            InputStream mInputStream = null;
            try {
                if (socket != null) {
                    //获取连接的输入输出流
                    mOutputStream = socket.getOutputStream();
                    mInputStream = socket.getInputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            out = mOutputStream;
            in = mInputStream;
            isTransferError = false;
        }

        @Override
        public void run() {
            boolean isReciveImg=false;
            super.run();
            //读取数据
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    int imageStart;
                    StringBuffer sb = new StringBuffer();
                    bytes = in.read(buffer);
                    String s = new String(buffer, 0, bytes);
                    sb.append(s);
                    if ((imageStart = sb.indexOf(IMAGE_START)) < 0)
                        EventBus.getDefault().post(new MessageEvent(ChatActivity.class, BLUE_TOOTH_READ, sb.toString()));
                        //图片消息开始标识
                    else {
                        Log.i(TAG, "run: "+sb.substring(imageStart,imageStart+IMAGE_START.length()));
                        sb.delete(0, imageStart + IMAGE_START.length());
                        Log.i(TAG, "run: " + "开始读取文件名称");
                        int file_name_end;
                        while ((file_name_end = sb.indexOf(FILE_NAME_END)) < 0)
                            readToBuffer(in, sb);
                        String file_name = new String(sb.substring(0, file_name_end).getBytes(ISO_ENCODE), DEFAULT_ENCODE);
                        Log.i(TAG, "run:文件名称 " + file_name);


                        sb.delete(0, file_name_end + FILE_NAME_END.length());
                        Log.i(TAG, "run: "+"开始读取文件长度");
                        while (sb.length() < 8)
                            readToBuffer(in, sb);
                        String imageLengthString = sb.substring(0, 8);
                        byte[] imageLengthByteArray = imageLengthString.getBytes(ISO_ENCODE);
                        long imageLength = bytesToLong(imageLengthByteArray);
                        Log.i(TAG, "run: 文件长度 " + imageLength);
                        sb.delete(0, 8);


                        Log.i(TAG, "run: "+"开始读取文件");
                        byte[] image = sb.toString().getBytes(ISO_ENCODE);
                        String imgPath ="/storage/emulated/0/Android/data/com.volantgoat.bluetoothchat/cache" ;
                        imgUrl ="/storage/emulated/0/Android/data/com.volantgoat.bluetoothchat/"+"cache/"+file_name ;
                        File file=new File(imgPath);
                        File imgFile=new File(imgUrl);
                        if(!file.exists())
                            file.mkdir();
                        if(!imgFile.exists())
                            imgFile.createNewFile();
                        FileOutputStream fos = new FileOutputStream(imgFile);
                        if (imageLength > image.length) {
                            Log.i(TAG, "run: " + "文件只有部分在数组中");
                            fos.write(image);
                            writeImage(in, fos, imageLength - image.length);
                            sb.delete(0, sb.length());
                        } else {
                            Log.i(TAG, "run: "+"文件已经在数组中");
                            fos.write(image, 0, (int) imageLength);
                            sb.delete(0, (int) imageLength);
                        }
                        fos.close();
                        Log.i(TAG, "run: " + "文件已经保存");
                        int end;
                        while ((end = sb.indexOf(IMAGE_END)) < 0) {
                            readToBuffer(in, sb);
                            Log.i(TAG, "run: 未读到结束标志");
                        }
                        Log.i(TAG, "run: "+sb.substring(end,end+IMAGE_END.length()));
                        sb.delete(0, end + IMAGE_END.length());
                        EventBus.getDefault().post(new MessageEvent(ChatActivity.class, BLUE_TOOTH_IMAGE_READ_SUCCESS,imgUrl));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "传输错误" + e.toString());
                    BluetoothChatService.this.start();
                    //TODO 连接丢失显示并重新开始连接
                    EventBus.getDefault().post(new MessageEvent(MainActivity.class, MainActivity.BLUE_TOOTH_TOAST, "设备连接失败/传输关闭"));
                    isTransferError = true;
                    break;
                }
            }
        }

        /**
         * 192      * 从输入流中读取图片信息到图片文件输出流中
         * 193      *
         * 194      * @param is
         * 195      *            输入流
         * 196      * @param fos
         * 197      *            图片文件输出流
         * 198      * @param length
         * 199      *            需要读取的数据长度
         * 200      * @throws Exception
         * 201
         */
        private void writeImage(InputStream is, FileOutputStream fos, long length) throws Exception {
            byte[] imageByte = new byte[1024];
            int oneTimeReadLength;

            for (long readLength = 0; readLength < length; ) {
                if (readLength + imageByte.length <= length) {
                    System.out.println("剩余的字节数大于1024，将尽可能多的读取内容");
                    oneTimeReadLength = is.read(imageByte);
                } else {
                    System.out.println("剩余的字节数小于1024，将只读取" + (length - readLength) + "字节");
                    oneTimeReadLength = is.read(imageByte, 0, (int) (length - readLength));
                }

                if (oneTimeReadLength == -1)
                    throw new RuntimeException("读取文件时，读取到了-1，说明Socket已经结束");
                System.out.println("实际读取长度" + oneTimeReadLength + "字节");

                readLength += oneTimeReadLength;

                fos.write(imageByte, 0, oneTimeReadLength);
                System.out.println("继续追加" + readLength + "字节长度");
            }
        }

        /**
         * 将byte数组转化为Long类型
         *
         * @param array
         * @return
         */
        public long bytesToLong(byte[] array) {
            return ((((long) array[0] & 0xff) << 56) | (((long) array[1] & 0xff) << 48) | (((long) array[2] & 0xff) << 40)
                    | (((long) array[3] & 0xff) << 32) | (((long) array[4] & 0xff) << 24)
                    | (((long) array[5] & 0xff) << 16) | (((long) array[6] & 0xff) << 8) | (((long) array[7] & 0xff) << 0));
        }

        /**
         * @param is
         * @param sb
         * @throws Exception
         */
        private void readToBuffer(InputStream is, StringBuffer sb) throws Exception {
            int readLength;
            byte[] b = new byte[1024];
            readLength = is.read(b);
            if (readLength == -1)
                throw new RuntimeException("读取到了-1，说明Socket已经关闭");
                String s = new String(b, 0, readLength,ISO_ENCODE);
            sb.append(s);
        }

        /**
         * 写入数据传输
         *
         * @param buffer
         */
        public void write(byte[] buffer, int start, int end) {
            try {
                out.write(buffer, start, end);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 写入数据传输
         * @param buffer
         */
        public void write(byte[] buffer) {
            try {
                out.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接线程
     */
    class ConnectThread extends Thread {
        private  BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            BluetoothSocket mSocket = null;
            try {
                //建立通道
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                socket = mSocket;
            } catch (IOException e) {
                Log.e(TAG, "连接失败"+e.toString());
                EventBus.getDefault().post(new MessageEvent(MainActivity.class, MainActivity.BLUE_TOOTH_TOAST, "连接失败，请重新连接"));
            }

        }

        @Override
        public void run() {
            super.run();
            //建立后取消扫描
            bluetoothAdapter.cancelDiscovery();

            try {
                Log.e(TAG, "run: connectThread 等待");
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Log.e(TAG, "run: 无法关闭");
                }
                //TODO 连接失败显示
                EventBus.getDefault().post(new MessageEvent(MainActivity.class, MainActivity.BLUE_TOOTH_TOAST, "连接失败，请重新连接"));
                //   sendMessageToUi(MainActivity.BLUE_TOOTH_TOAST , "连接失败，请重新连接");
                BluetoothChatService.this.start();
            }


            // 重置
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }
            //Socket已经连接上了，默认安全,客户端才会调用
            Log.e(TAG, "run: connectThread 连接上了,准备传输");
            dataTransfer(socket, device);
        }

        public void cancel() {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
