package com.volantgoat.bluetoothchat.holder;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.volantgoat.bluetoothchat.R;


/**
 * Create by dong
 * Data:2019/12/11
 */
public class ChatLeftHolder extends RecyclerView.ViewHolder {
    private TextView tvContent;
    private TextView tvName;
    public ChatLeftHolder(View itemView) {
        super(itemView);
        tvContent = (TextView) itemView.findViewById(R.id.tv_left);
        tvName = (TextView) itemView.findViewById(R.id.tv_device);
    }
    public TextView getTvContent() {
        return tvContent;
    }

    public void setTvContent(TextView tvContent) {
        this.tvContent = tvContent;
    }

    public TextView getTvName() {
        return tvName;
    }

    public void setTvName(TextView tvName) {
        this.tvName = tvName;
    }
}
