package com.volantgoat.bluetoothchat.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.volantgoat.bluetoothchat.bean.ChatInfo;
import com.volantgoat.bluetoothchat.contract.ChatContract;
import com.volantgoat.bluetoothchat.helper.BluetoothChatService;
import com.volantgoat.bluetoothchat.helper.SQLHelper;
import com.volantgoat.bluetoothchat.util.MD5Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.volantgoat.bluetoothchat.helper.SQLHelper.COLUMN_CONTENT;
import static com.volantgoat.bluetoothchat.helper.SQLHelper.COLUMN_ID;
import static com.volantgoat.bluetoothchat.helper.SQLHelper.COLUMN_NAME;
import static com.volantgoat.bluetoothchat.helper.SQLHelper.COLUMN_TAG;
import static com.volantgoat.bluetoothchat.helper.SQLHelper.TABLE_NAME;
import static com.volantgoat.bluetoothchat.view.ChatActivity.READ_DATA;
import static java.nio.file.attribute.AclEntryPermission.WRITE_DATA;

/**
 * Create by dong
 * Date on 2020/6/11  19:44
 */
public class ChatModel implements ChatContract.Model {
    private String IMAGE_START = "image:";

    private String IMAGE_END = "over";

    private String FILE_NAME_END = "?";

    public static final String TAG = ChatModel.class.getSimpleName();
    private BluetoothChatService mService;

    public ChatModel() {
        mService = BluetoothChatService.getInstance();
    }


    @Override
    public void sendData(byte[] out) {
        Log.i(TAG, "sendData: ");
        mService.sendData(out);
    }

    /**
     * 图片传输协议
     * @param file 图片文件
     */
    @Override
    public void sendData(File file) {
        try {
            Log.i(TAG, "sendData: "+"开始发送图片消息");
            FileInputStream in=new FileInputStream(file);
            sendData(IMAGE_START.getBytes());
            Log.i(TAG, "sendData: "+"图片消息开头发送成功");
            sendData(file.getName().getBytes());
            Log.i(TAG, "sendData: "+"文件名称发送开始");
            sendData(FILE_NAME_END.getBytes());
            Log.i(TAG, "sendData: "+"文件名称发送结束");
            byte[] bs=longToBytes(file.length());
            Log.i(TAG, "sendData:发送文件长度 "+file.length());
            sendData(bs);
            Log.i(TAG, "sendData: "+"文件长度发送结束:");
            int length;
            byte[] b=new byte[1024];
            while((length=in.read(b))>0){
                mService.sendData(b,0,length);
            }
            sendData(IMAGE_END.getBytes());
            Log.i(TAG, "sendData: "+"消息结束标志发送成功");
        }  catch (IOException e) {
            e.printStackTrace();
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
     * 将长整型转为byte数组
     * @param n
     * @return
     */
    public static byte[] longToBytes(long n) {
        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8 & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);
        return b;
    }
}
