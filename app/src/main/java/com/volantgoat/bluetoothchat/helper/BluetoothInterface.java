package com.volantgoat.bluetoothchat.helper;

import android.bluetooth.BluetoothDevice;

/**
 * Create by dong
 * Data:2019/12/10
 */
public interface BluetoothInterface {
    void getBlueToothDevices(BluetoothDevice device, int rssi);
    void searchFinish();
}
