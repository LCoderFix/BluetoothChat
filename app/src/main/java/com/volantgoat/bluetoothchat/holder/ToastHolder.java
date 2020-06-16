package com.volantgoat.bluetoothchat.holder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.volantgoat.bluetoothchat.R;


/**
 * Create by dong
 * Data:2019/12/10
 */
public class ToastHolder extends RecyclerView.ViewHolder {
    private TextView tvToast;
    public ToastHolder(View itemView) {
        super(itemView);
        tvToast=itemView.findViewById(R.id.tv_toast);
    }

    public TextView getTvToast() {
        return tvToast;
    }

    public void setTvToast(TextView tvToast) {
        this.tvToast = tvToast;
    }
}
