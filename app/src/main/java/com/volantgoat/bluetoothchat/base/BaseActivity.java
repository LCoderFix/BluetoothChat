package com.volantgoat.bluetoothchat.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Create by dong
 * Date on 2020/6/10  20:40
 */
public abstract class BaseActivity<T extends BasePresenter> extends FragmentActivity implements View.OnClickListener,BaseView {
    public abstract int getLayoutId();
    public abstract void initViews();
    public abstract void initListener();
    public abstract void initData();
    public abstract void processClick(View view);

    protected T mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initViews();
        initData();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 封装setOnClickListener
     * @param view
     * @param <T>
     */
    public <T extends View> void setOnClick(T view){
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        processClick(v);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().unregister(this);
    }


}
