package com.volantgoat.bluetoothchat.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * 解决键盘挡住输入框
 * Create by dong
 * Date on 2020/6/12  21:58
 */
public class SoftHideKeyBoardUtil {

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    public static void assistActivity(Activity activity){
        new SoftHideKeyBoardUtil(activity);
    }


    public SoftHideKeyBoardUtil(Activity activity){
        //找到Activity最外层 DecorView
        FrameLayout content=activity.findViewById(android.R.id.content);
        //拿到setContentView 中的view
        mChildOfContent = content.getChildAt(0);
        //
        mChildOfContent.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        possiblyResizeChildOfContent();
                    }
                });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }
    
    private void possiblyResizeChildOfContent(){
        int usableHeightNow=computeUsableHeight();
        if(usableHeightNow!=usableHeightPrevious){
            int usableHeightSansKeyboard=mChildOfContent.getRootView().getHeight();

            int heightDifference=usableHeightSansKeyboard-usableHeightNow;
            if(heightDifference>(usableHeightSansKeyboard/4)){
                frameLayoutParams.height=usableHeightSansKeyboard-heightDifference;
            }else {
                frameLayoutParams.height=usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious=usableHeightNow;
        }
    }

    /**
     * 获取可用高度
     * @return
     */
    private int computeUsableHeight(){
        Rect r=new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom-r.top);
    }
}
