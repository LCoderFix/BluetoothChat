package com.volantgoat.bluetoothchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.volantgoat.bluetoothchat.R;

/**
 * Create by dong
 * Date on 2020/6/13  16:31
 */
public class ImageLeftHolder extends RecyclerView.ViewHolder {
    private ImageView imgLeft;
    private ImageView headImage;
    private TextView tvName;
    public ImageLeftHolder(View view){
        super(view);
        imgLeft=view.findViewById(R.id.img_left);
        headImage=view.findViewById(R.id.img_head);
        tvName=view.findViewById(R.id.tv_device);
    }

    public ImageView getImgLeft() {
        return imgLeft;
    }

    public void setImgLeft(ImageView imgLeft) {
        this.imgLeft = imgLeft;
    }

    public ImageView getHeadImage() {
        return headImage;
    }

    public void setHeadImage(ImageView headImage) {
        this.headImage = headImage;
    }

    public TextView getTvName() {
        return tvName;
    }

    public void setTvName(TextView tvName) {
        this.tvName = tvName;
    }
}
