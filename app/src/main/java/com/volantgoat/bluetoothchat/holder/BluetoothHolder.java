package com.volantgoat.bluetoothchat.holder;


import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.volantgoat.bluetoothchat.R;


/**
 *
 *
 * Create by dong
 * Data:2019/12/10
 */
public class BluetoothHolder extends RecyclerView.ViewHolder {
    private TextView tvName,tvMac,tvLevel;
    private RelativeLayout rlClick;

    public BluetoothHolder(View itemView) {
        super(itemView);
        tvName=itemView.findViewById(R.id.tv_name);
        tvMac=itemView.findViewById(R.id.tv_mac);
        tvLevel=itemView.findViewById(R.id.tv_level);
        rlClick=itemView.findViewById(R.id.rl_click);
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvMac() {
        return tvMac;
    }

    public TextView getTvLevel() {
        return tvLevel;
    }

    public RelativeLayout getRlClick() {
        return rlClick;
    }

    public void setTvName(TextView tvName) {
        this.tvName = tvName;
    }

    public void setTvMac(TextView tvMac) {
        this.tvMac = tvMac;
    }

    public void setTvLevel(TextView tvLevel) {
        this.tvLevel = tvLevel;
    }

    public void setRlClick(RelativeLayout rlClick) {
        this.rlClick = rlClick;
    }
}
