package com.volantgoat.bluetoothchat.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Create by dong
 * Data:2019/12/11
 */
public class SQLHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "blue_tooth_chat_db";
    public static final String TABLE_NAME = "chat_table";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "device_name";
    public static final String COLUMN_TAG = "tag";
    public static final String COLUMN_CONTENT = "content";
    private static final String CREATE_TABLE = "create table " + TABLE_NAME +
            "(" + COLUMN_ID + " varchar(40) , " + COLUMN_NAME + " varchar(20) ," + COLUMN_TAG  + " int , " + COLUMN_CONTENT + " varchar(100)) ";
    public SQLHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
