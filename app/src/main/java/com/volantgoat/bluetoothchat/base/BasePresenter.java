package com.volantgoat.bluetoothchat.base;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * Create by dong
 * Date on 2020/6/10  21:47
 */
public class BasePresenter<V extends BaseView> {
    public static final String TAG=BasePresenter.class.getSimpleName();
    protected V mView;

    public void start(){
        //注册EventBus
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        Log.i(TAG, "start: "+"EventBus注册");
    }
    /**
     * 绑定view,初始化调用
     * @param mView
     */
    public void attachView(V mView){
        this.mView=mView;
    }

    /**
     * 解除绑定view
     */
    public void detachView(){
        this.mView=null;
    }

    /**
     * 判定view是否绑定
     * @return
     */
    public boolean isViewAttached(){
        return mView!=null;
    }

    public void stop(){
        //注销EventBus
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
