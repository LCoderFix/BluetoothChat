package com.volantgoat.bluetoothchat.model;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import com.volantgoat.bluetoothchat.bean.BlueTooth;
import com.volantgoat.bluetoothchat.bean.MessageEvent;
import com.volantgoat.bluetoothchat.contract.MainContract;
import com.volantgoat.bluetoothchat.helper.BluetoothChatService;
import com.volantgoat.bluetoothchat.view.MainActivity;
import org.greenrobot.eventbus.EventBus;


/**
 * Create by dong
 * Date on 2020/6/10  22:22
 */
public class MainModel implements MainContract.Model{
    public static final String TAG=MainModel.class.getSimpleName();
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothChatService mService;

    /**
     * 设置蓝牙状态
     * @param enable
     * @return
     */
    @Override
    public String setBluetoothEnable(Boolean enable) {
        if (enable) {
            mBluetoothAdapter.enable();
            return "正在开启蓝牙";
        } else {
            mBluetoothAdapter.disable();
            return "正在关闭蓝牙";
        }
    }

    /**
     * 启动服务端，等待连接
     */
    @Override
    public void prepareAccept() {
        if(mBluetoothAdapter.isEnabled()){
            mService = BluetoothChatService.getInstance();
            mService.start();
        }
        EventBus.getDefault().post(new MessageEvent(MainActivity.class,2));
        Log.i(TAG, "prepareAccept: "+"发送消息成功");
    }

    /**
     * 连接设备
     * @param blueTooth
     */
    @Override
    public void connectDevice(BlueTooth blueTooth) {
        BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(blueTooth.getMac());
        mService.connectDevice(device);
    }

}
