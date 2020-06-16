package com.volantgoat.bluetoothchat.helper;

import android.app.Activity;

/**
 * @Description:获取当前活动
 * @author:dong
 * @Date: 2020/6/14  21:24
 */
public class AppContext {
    private static AppContext context;
    private static Activity activityClass;

    public static AppContext getInstance(){
        if(context==null){
            synchronized (AppContext.class){
                if(context==null)
                    context=new AppContext();
            }
        }
        return context;
    }

    public static Activity getActivityClass() {
        return activityClass;
    }

    public static void setActivityClass(Activity activityClass) {
        AppContext.activityClass = activityClass;
    }
}
