package com.volantgoat.bluetoothchat.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.volantgoat.bluetoothchat.R;
import com.volantgoat.bluetoothchat.adapter.RecyclerBluetoothAdapter;
import com.volantgoat.bluetoothchat.base.BaseActivity;
import com.volantgoat.bluetoothchat.bean.BlueTooth;
import com.volantgoat.bluetoothchat.bean.MessageEvent;
import com.volantgoat.bluetoothchat.contract.MainContract;
import com.volantgoat.bluetoothchat.helper.AppContext;
import com.volantgoat.bluetoothchat.presenter.MainPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by dong
 * Date on 2020/6/11  11:03
 */
public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View, RecyclerBluetoothAdapter.OnItemClickListener {
    //加载
    public static final int BLUE_TOOTH_DIALOG = 0x111;
    //Toast提示
    public static final int BLUE_TOOTH_TOAST = 0x123;
    //发送消息
    public static final int BLUE_TOOTH_WRAITE = 0X222;
    //接收消息
    public static final int BLUE_TOOTH_READ = 0X333;
    //连接成功，跳转
    public static final int BLUE_TOOTH_SUCCESS = 0x444;
    //接收图片消息成功
    public static final int BLUE_TOOTH_IMAGE_READ_SUCCESS =0x555;
    //发送图片消息成功
    public static final int BLUE_TOOTH_IMAGE_WRITE_SUCCESS=0x666;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private List<BlueTooth> list;
    private RecyclerBluetoothAdapter mAdapter;


    /**
     * 布局资源ID
     * @return ID(int)
     */
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
        mPresenter.prepareAccept();
    }

    /**
     * 初始化控件
     */
    @Override
    public void initViews() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
        mAdapter = new RecyclerBluetoothAdapter(this);
        ImmersionBar.with(this)
                .transparentBar()
                .fitsSystemWindows(true)
                .statusBarDarkFont(true)
                .init();
    }

    /**
     * 注册监听
     */
    @Override
    public void initListener() {
        mAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        list = new ArrayList<>();
        mAdapter.setBluetoothData(list);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.setBluetoothEnable(true);
        mPresenter.findBluetoothDevice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppContext.setActivityClass(this);
    }

    /**
     * 点击事件
     *
     * @param view View
     */
    @Override
    public void processClick(View view) {

    }

    /**
     * loading
     */
    @Override
    public void showLoading(String msg) {
        showProgressDialog(msg);
    }

    /**
     * loading closed
     */
    @Override
    public void hideLoading() {
        mProgressDialog.dismiss();
    }

    /**
     * 失败信息
     * @param errMessage 失败信息
     */
    @Override
    public void onError(String errMessage) {

    }

    /**
     * 设备扫描成功数据
     *
     * @param blueTooth bean
     */
    @Override
    public void onDeviceSuccess(BlueTooth blueTooth) {
        list.add(blueTooth);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 列表清空
     */
    @Override
    public void onClear() {
        list.clear();
    }

    /**
     * 进度对话框
     *
     * @param msg 提示消息
     */
    public void showProgressDialog(String msg) {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.stop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    /**
     * 单项点击连接
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        BlueTooth mBluetooth=list.get(position);
        mPresenter.connectDevice(mBluetooth);
    }
}
