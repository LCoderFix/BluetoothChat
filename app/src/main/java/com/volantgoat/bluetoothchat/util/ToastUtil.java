package com.volantgoat.bluetoothchat.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Create by dong
 * Data:2019/12/11
 */
public class ToastUtil {
    private static Toast toast;
    public static void showText(Context context , String s){
        if(toast == null)
            toast = Toast.makeText(context , s , Toast.LENGTH_SHORT);
        else {
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(s);
        }
        toast.show();
    }
}
