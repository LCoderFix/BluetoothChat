package com.volantgoat.bluetoothchat.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.volantgoat.bluetoothchat.base.BasePresenter;
import com.volantgoat.bluetoothchat.bean.ChatInfo;
import com.volantgoat.bluetoothchat.bean.MessageEvent;
import com.volantgoat.bluetoothchat.contract.ChatContract;
import com.volantgoat.bluetoothchat.helper.ModelThread;
import com.volantgoat.bluetoothchat.model.ChatModel;
import com.volantgoat.bluetoothchat.view.ChatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.volantgoat.bluetoothchat.view.ChatActivity.READ_DATA;
import static com.volantgoat.bluetoothchat.view.ChatActivity.READ_DATA_FINISH;
import static com.volantgoat.bluetoothchat.view.ChatActivity.WRITE_DATA;
import static com.volantgoat.bluetoothchat.view.MainActivity.BLUE_TOOTH_IMAGE_READ_SUCCESS;
import static com.volantgoat.bluetoothchat.view.MainActivity.BLUE_TOOTH_IMAGE_WRITE_SUCCESS;
import static com.volantgoat.bluetoothchat.view.MainActivity.BLUE_TOOTH_READ;
import static com.volantgoat.bluetoothchat.view.MainActivity.BLUE_TOOTH_WRAITE;

/**
 * Create by dong
 * Date on 2020/6/11  19:45
 */
public class ChatPresenter extends BasePresenter<ChatContract.View> implements ChatContract.Presenter {

    public static final String TAG = ChatPresenter.class.getSimpleName();
    List<ChatInfo> list = new ArrayList<>();
    private ChatContract.Model model;
    private ChatInfo chatInfo;

    public ChatPresenter() {
        model = new ChatModel();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        onSaveChatInfo();
    }


    /**
     * 读历史消息
     */
    @Override
    public void onReadChatInfo() {
        list.clear();
        mView.clear();
        new ModelThread(READ_DATA, ((ChatActivity) mView), list).start();
    }

    /**
     * 保存消息
     */
    @Override
    public void onSaveChatInfo() {
        List<ChatInfo> list = ((ChatActivity) mView).getChatInfoList();
        new ModelThread(WRITE_DATA, ((ChatActivity) mView), list).start();
        Log.i(TAG, "onSaveChatInfo: ");
    }

    @Override
    public void sendData(byte[] out) {
        Log.i(TAG, "sendData: ");
        model.sendData(out);
    }

    @Override
    public void sendData(final File file) {
        Log.i(TAG, "sendData: " + "图片消息");
        model.sendData(file);
        EventBus.getDefault().post(new MessageEvent(ChatActivity.class, BLUE_TOOTH_IMAGE_WRITE_SUCCESS, file.getPath()));
    }

    @Override
    public void savaFile(final String imgUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = Glide.with(((ChatActivity) mView))
                            .asBitmap()
                            .load(imgUrl)
                            .submit(200, 200)
                            .get();
                    savePicToSdcard(imgUrl, bitmap);
                    sendData(new File(imgUrl));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 保存图片到sdcard
     *
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessageEvent message) {
        if (message != null && message.getaClass() == ChatActivity.class) {
            switch (message.getMSG_TYPE()) {
                case READ_DATA_FINISH:
                    mView.onChatInfoSuccess(list);
                    break;
                case BLUE_TOOTH_WRAITE:
                    break;
                case BLUE_TOOTH_READ:
                    mView.onChatInfoSuccess(new ChatInfo(ChatInfo.TAG_Text_LEFT, ChatActivity.DEVICE_NAME, message.getMSG_CONTENT()));
                    break;
                case BLUE_TOOTH_IMAGE_READ_SUCCESS:
                    chatInfo = new ChatInfo(ChatInfo.TAG_IMG_LEFT, ChatActivity.DEVICE_NAME, message.getMSG_CONTENT());
                    chatInfo.setImgUrl(message.getMSG_CONTENT());
                    mView.onChatInfoSuccess(chatInfo);
                    break;
                case BLUE_TOOTH_IMAGE_WRITE_SUCCESS:
                    chatInfo = new ChatInfo(ChatInfo.TAG_IMG_RIGHT, "我", message.getMSG_CONTENT());
                    chatInfo.setImgUrl(message.getMSG_CONTENT());
                    mView.onChatInfoSuccess(chatInfo);
                    break;
            }
        }
    }
}
