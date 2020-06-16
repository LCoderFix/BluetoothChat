package com.volantgoat.bluetoothchat.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.volantgoat.bluetoothchat.bean.ChatInfo;
import com.volantgoat.bluetoothchat.bean.MessageEvent;
import com.volantgoat.bluetoothchat.util.MD5Util;
import com.volantgoat.bluetoothchat.view.ChatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.volantgoat.bluetoothchat.helper.SQLHelper.COLUMN_CONTENT;
import static com.volantgoat.bluetoothchat.helper.SQLHelper.COLUMN_ID;
import static com.volantgoat.bluetoothchat.helper.SQLHelper.COLUMN_NAME;
import static com.volantgoat.bluetoothchat.helper.SQLHelper.COLUMN_TAG;
import static com.volantgoat.bluetoothchat.helper.SQLHelper.TABLE_NAME;
import static com.volantgoat.bluetoothchat.view.ChatActivity.READ_DATA;

/**
 * Create by dong
 * Date on 2020/6/11  21:21
 */
public class ModelThread extends Thread {
    //读、写标志
    private int tag;

    private SQLHelper sqlHelper;
    //信息列表
    private List<ChatInfo> list;

    private SQLiteDatabase db;

    public ModelThread(int tag, Context context, List<ChatInfo> list) {
        this.tag = tag;
        sqlHelper = new SQLHelper(context);
        this.list = list;
    }

    @Override
    public void run() {
        super.run();


        if (tag == READ_DATA) {
            db = sqlHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_ID + " = ?", new String[]{MD5Util.stringToMD5(ChatActivity.DEVICE_NAME + "我")});
            if (cursor.moveToFirst()) {
                do {
                    ChatInfo chatInfo = new ChatInfo(
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_TAG))),
                            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                    list.add(chatInfo);
                } while (cursor.moveToNext());
            }
            EventBus.getDefault().post(new MessageEvent(ChatActivity.class, ChatActivity.READ_DATA_FINISH));
            cursor.close();



        } else {
            db = sqlHelper.getWritableDatabase();
            //清除之前数据
            db.execSQL("delete from " + TABLE_NAME + " where " + COLUMN_ID + " = ?", new String[]{MD5Util.stringToMD5(ChatActivity.DEVICE_NAME + "我")});
            for (ChatInfo chatInfo : list) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_ID, MD5Util.stringToMD5(ChatActivity.DEVICE_NAME + "我"));
                contentValues.put(COLUMN_TAG, chatInfo.getTag());
                contentValues.put(COLUMN_NAME, chatInfo.getName());
                contentValues.put(COLUMN_CONTENT, chatInfo.getContent());
                db.insert(TABLE_NAME, null, contentValues);
            }
        }
        db.close();
    }
}
