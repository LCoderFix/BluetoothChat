package com.volantgoat.bluetoothchat.presenter;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuView;

import com.volantgoat.bluetoothchat.base.BasePresenter;
import com.volantgoat.bluetoothchat.bean.BlueTooth;
import com.volantgoat.bluetoothchat.bean.MessageEvent;
import com.volantgoat.bluetoothchat.contract.MainContract;
import com.volantgoat.bluetoothchat.helper.AppContext;
import com.volantgoat.bluetoothchat.helper.BluetoothInterface;
import com.volantgoat.bluetoothchat.helper.BluetoothReciver;
import com.volantgoat.bluetoothchat.model.MainModel;
import com.volantgoat.bluetoothchat.util.ToastUtil;
import com.volantgoat.bluetoothchat.view.ChatActivity;
import com.volantgoat.bluetoothchat.view.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Set;

import static android.content.Context.CAMERA_SERVICE;
import static com.volantgoat.bluetoothchat.view.ChatActivity.DEVICE_NAME_INTENT;
import static com.volantgoat.bluetoothchat.view.MainActivity.BLUE_TOOTH_DIALOG;
import static com.volantgoat.bluetoothchat.view.MainActivity.BLUE_TOOTH_READ;
import static com.volantgoat.bluetoothchat.view.MainActivity.BLUE_TOOTH_SUCCESS;
import static com.volantgoat.bluetoothchat.view.MainActivity.BLUE_TOOTH_TOAST;

/**
 * Create by dong
 * Date on 2020/6/10  22:39
 */
public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter, BluetoothInterface {
    public static final String TAG = MainPresenter.class.getSimpleName();
    private MainContract.Model model;
    private BroadcastReceiver mReciver;
    private BluetoothAdapter mBluetoothAdapter;
    private BlueTooth blueTooth;


    public MainPresenter() {
        model = new MainModel();
        mReciver = new BluetoothReciver(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }


    @Override
    public void setBluetoothEnable(Boolean enable) {
        model.setBluetoothEnable(enable);
    }

    /**
     * 开始查找设备
     */
    @Override
    public void findBluetoothDevice() {
        mView.showLoading("加载中");
        mView.onClear();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            mView.onDeviceSuccess(new BlueTooth("我的好友", BlueTooth.TAG_TOAST));
            for (BluetoothDevice device : pairedDevices)
                mView.onDeviceSuccess(new BlueTooth(device.getName(), device.getAddress(), ""));
        }
        mView.onDeviceSuccess(new BlueTooth("周围设备", BlueTooth.TAG_TOAST));
        mBluetoothAdapter.startDiscovery();
    }


    /**
     * 开启服务线程
     */
    @Override
    public void prepareAccept() {
        model.prepareAccept();
    }

    @Override
    public void connectDevice(BlueTooth blueTooth) {
        this.blueTooth = blueTooth;
        model.connectDevice(blueTooth);
    }

    @Override
    public void stop() {
        ((MainActivity) mView).unregisterReceiver(mReciver);
        mBluetoothAdapter.cancelDiscovery();

    }


    /**
     * 广播回调获取扫描到的设备
     *
     * @param device
     * @param rssi
     */
    @Override
    public void getBlueToothDevices(BluetoothDevice device, int rssi) {

        mView.onDeviceSuccess(new BlueTooth(device.getName(), device.getAddress(), rssi + ""));
    }

    /**
     * 扫描完成
     */
    @Override
    public void searchFinish() {
        Log.i(TAG, "searchFinish: ");
        mView.hideLoading();
    }

    @Override
    public void attachView(MainContract.View mView) {
        super.attachView(mView);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        ((Activity) mView).registerReceiver(mReciver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ((Activity) mView).registerReceiver(mReciver, filter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessageEvent message) {
        if (message != null && message.getaClass() == MainActivity.class)
            switch (message.getMSG_TYPE()) {
                case BLUE_TOOTH_DIALOG:
                    mView.showLoading(message.getMSG_CONTENT());
                    break;
                case BLUE_TOOTH_TOAST:
                    mView.hideLoading();
                    ToastUtil.showText(((MainActivity) mView), message.getMSG_CONTENT());
                    break;
                case BLUE_TOOTH_SUCCESS:
                    mView.hideLoading();
                    if (AppContext.getActivityClass() instanceof MainActivity) {
                        Intent intent = new Intent(((MainActivity) mView), ChatActivity.class);
                        Log.i(TAG, "onGetMessage: " + message.getMSG_CONTENT());
                        intent.putExtra(DEVICE_NAME_INTENT, message.getMSG_CONTENT());
                        ((MainActivity) mView).startActivity(intent);

                    }
                    ToastUtil.showText(((MainActivity) mView), "连接设备" + message.getMSG_CONTENT() + "成功");
                    break;
            }
    }
}
