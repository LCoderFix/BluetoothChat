package com.volantgoat.bluetoothchat.contract;

import android.content.Context;

import com.volantgoat.bluetoothchat.base.BaseView;
import com.volantgoat.bluetoothchat.bean.BlueTooth;
import com.volantgoat.bluetoothchat.bean.ChatInfo;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 *
 * Create by dong
 * Date on 2020/6/11  19:41
 */
public interface ChatContract {
    interface Model{
        /**
         * 发送消息
         * @param out
         */
        void sendData(byte[] out);
        void sendData(File file);
    }
    interface View extends BaseView{
        void clear();
        void onChatInfoSuccess(List<ChatInfo> list);
        void onChatInfoSuccess(ChatInfo chatInfo);
    }
    interface Presenter{
        void onReadChatInfo();
        void onSaveChatInfo();
        void sendData(byte[] out);
        void sendData(File file);
        void savaFile(String imgUrl);
    }
}
