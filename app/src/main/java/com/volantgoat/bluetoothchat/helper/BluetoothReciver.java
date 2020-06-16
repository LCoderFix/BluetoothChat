package com.volantgoat.bluetoothchat.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Create by dong
 * Data:2019/12/10
 */
public class BluetoothReciver extends BroadcastReceiver {
    public static final String TAG = BluetoothReciver.class.getSimpleName();
    BluetoothInterface bluetoothInterface;

    public BluetoothReciver(BluetoothInterface bluetoothInterface) {
        this.bluetoothInterface = bluetoothInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            //当找到设备时
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
            if (bluetoothInterface != null) {
                bluetoothInterface.getBlueToothDevices(device, rssi);
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            if (bluetoothInterface != null) {
                bluetoothInterface.searchFinish();
            }
            Log.i(TAG, "搜索完成");
        }
    }
}
