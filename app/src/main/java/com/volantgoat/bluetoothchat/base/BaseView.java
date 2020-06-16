package com.volantgoat.bluetoothchat.base;

/**
 * Create by dong
 * Date on 2020/6/10  21:36
 */
public interface BaseView {
    /**
     * 显示加载中
     */
    void showLoading(String msg);
    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 数据获取失败
     * @param errMessage 失败信息
     */
    void onError(String errMessage);


}
