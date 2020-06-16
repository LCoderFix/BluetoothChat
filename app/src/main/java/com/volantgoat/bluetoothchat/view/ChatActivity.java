package com.volantgoat.bluetoothchat.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TabHost;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.volantgoat.bluetoothchat.R;
import com.volantgoat.bluetoothchat.adapter.RecyclerChatAdapter;
import com.volantgoat.bluetoothchat.base.BaseActivity;
import com.volantgoat.bluetoothchat.bean.ChatInfo;
import com.volantgoat.bluetoothchat.contract.ChatContract;
import com.volantgoat.bluetoothchat.emojicon.EmojiconGridFragment;
import com.volantgoat.bluetoothchat.emojicon.EmojiconsFragment;
import com.volantgoat.bluetoothchat.emojicon.emoji.Emojicon;
import com.volantgoat.bluetoothchat.helper.AppContext;
import com.volantgoat.bluetoothchat.presenter.ChatPresenter;
import com.volantgoat.bluetoothchat.util.SoftHideKeyBoardUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Create by dong
 * Date on 2020/6/11  19:53
 */
public class ChatActivity extends BaseActivity<ChatPresenter> implements ChatContract.View, EmojiconsFragment.OnEmojiconBackspaceClickedListener, EmojiconGridFragment.OnEmojiconClickedListener {
    public static final String TAG = ChatActivity.class.getSimpleName();
    public static final String DEVICE_NAME_INTENT = "device_name";
    public static final int TAKE_PHOTO = 0x06;
    public static String DEVICE_NAME;
    private static final int UPDATE_DATA = 0x666;
    public static final int READ_DATA = 0;
    public static final int WRITE_DATA = 1;
    public static final int READ_DATA_FINISH = 2;
    private RecyclerView mRecyclerView;
    private EditText etWrite;
    private Button btnSend;
    private RecyclerChatAdapter mAdapter;
    private List<ChatInfo> list = new ArrayList<>();
    private Button btnEmoji;
    private FrameLayout mEmojicon;
    private Button btnTakePhoto;
    private File outputImage;
    private String imgUrl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DEVICE_NAME = getIntent().getStringExtra(DEVICE_NAME_INTENT);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);

        }
        mPresenter.onReadChatInfo();
    }


    @Override
    public void initViews() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setFocusable(true);
        mRecyclerView.setFocusableInTouchMode(true);
        mRecyclerView.requestFocus();
        etWrite = findViewById(R.id.et_write);
        btnSend = findViewById(R.id.bt_send);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnEmoji = findViewById(R.id.btn_show_emoji);
        mEmojicon = findViewById(R.id.chat_emojicons);
        mAdapter = new RecyclerChatAdapter(this);
        mAdapter.setList(list);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter = new ChatPresenter();
        mPresenter.attachView(this);
        ImmersionBar.with(this)
                .transparentBar()
                .statusBarDarkFont(true)
                .init();

    }

    @Override
    public void initListener() {
        setOnClick(btnSend);
        setOnClick(btnEmoji);
        setOnClick(etWrite);
        setOnClick(btnTakePhoto);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        DEVICE_NAME = intent.getStringExtra(DEVICE_NAME_INTENT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
        SoftHideKeyBoardUtil.assistActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppContext.setActivityClass(this);
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send:
                mPresenter.sendData(getEtWrite().getBytes());
                onChatInfoSuccess(new ChatInfo(ChatInfo.TAG_Text_RIGHT, "我", getEtWrite()));
                etWrite.setText("");
                break;
            case R.id.btn_show_emoji:
                if (mEmojicon.getVisibility() == View.GONE) {
                    hideSoftInput();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEmojicon.setVisibility(View.VISIBLE);
                            setEmojiconFragment(false);
                        }
                    }, 200);
                } else {
                    mEmojicon.setVisibility(View.GONE);
                }
                break;
            case R.id.et_write:
                mEmojicon.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    }
                }, 100);
                break;
            case R.id.btn_take_photo:
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                imgUrl = Environment.getExternalStorageDirectory() + "/DCIM/" + new Date().getTime() + ".jpg";
                outputImage = new File(imgUrl);
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.cameraalbum.fileprovider", outputImage);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                mPresenter.savaFile(imgUrl);
            }
        }
    }

    /**
     * 保存图片到sdcard
     * @param bitmap
     */
    public static void savePicToSdcard(String path, Bitmap bitmap) {
        if (bitmap != null) {
            try {
                FileOutputStream out = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(String errMessage) {

    }

    public String getEtWrite() {
        return etWrite.getText().toString();
    }

    @Override
    public void clear() {
        list.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChatInfoSuccess(List<ChatInfo> list) {
        this.list.addAll(list);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onChatInfoSuccess(ChatInfo chatInfo) {
        list.add(chatInfo);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    public List<ChatInfo> getChatInfoList() {
        return list;
    }

    /**
     * 隐藏键盘
     */
    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.chat_emojicons, (Fragment) EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(etWrite, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(etWrite);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.stop();
    }
}
