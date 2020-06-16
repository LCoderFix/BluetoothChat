package com.volantgoat.bluetoothchat.contract;

import com.volantgoat.bluetoothchat.base.BaseView;
import com.volantgoat.bluetoothchat.bean.BlueTooth;

import java.util.List;

/**
 * 用于统一管理接口
 * Create by dong
 * Date on 2020/6/10  21:55
 */
public interface MainContract {
    interface Model{
        /**
         * 控制蓝牙状态
         * @param enable
         * @return
         */
        String setBluetoothEnable(Boolean enable);

        /**
         * 开启连接线程，回传数据
         */
        void prepareAccept();

        /**
         * 连接对应设备
         * @param blueTooth
         */
        void connectDevice(BlueTooth blueTooth);
    }
    interface View extends BaseView{

        @Override
        void showLoading(String msg);

        @Override
        void hideLoading();

        @Override
        void onError(String errMessage);

        /**
         * Found Device Success
         * @param blueTooth
         */
        void onDeviceSuccess(BlueTooth blueTooth);

        void onClear();
    }
    interface Presenter{
        /**
         * 初始化，用于注册EventBus及其它初始化操作
         */
        void start();


        void setBluetoothEnable(Boolean enable);

        void findBluetoothDevice();

        void prepareAccept();

        void connectDevice(BlueTooth blueTooth);

        /**
         * 活动结束时，进行相关释放操作
         */
        void stop();

    }
}
