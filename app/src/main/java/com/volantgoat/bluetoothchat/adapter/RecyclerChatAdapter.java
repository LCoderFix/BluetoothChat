package com.volantgoat.bluetoothchat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.volantgoat.bluetoothchat.R;
import com.volantgoat.bluetoothchat.bean.ChatInfo;
import com.volantgoat.bluetoothchat.holder.ChatLeftHolder;
import com.volantgoat.bluetoothchat.holder.ChatRightHolder;
import com.volantgoat.bluetoothchat.holder.ImageLeftHolder;
import com.volantgoat.bluetoothchat.holder.ImageRightHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


/**
 * Create by dong
 * Data:2019/12/11
 */
public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = RecyclerChatAdapter.class.getSimpleName();
    private List<ChatInfo> list;
    private Context context;

    public List<ChatInfo> getList() {
        return list;
    }

    public void setList(List<ChatInfo> list) {
        this.list = list;
    }

    public RecyclerChatAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getTag();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ChatInfo.TAG_Text_LEFT:
                View view = LayoutInflater.from(context).inflate(R.layout.recycler_chat_left, null);
                return new ChatLeftHolder(view);
            case ChatInfo.TAG_Text_RIGHT:
                view = LayoutInflater.from(context).inflate(R.layout.recycler_chat_right, null);
                return new ChatRightHolder(view);
            case ChatInfo.TAG_IMG_LEFT:
                view = LayoutInflater.from(context).inflate(R.layout.recycler_chat_left_img, null);
                return new ImageLeftHolder(view);
            case ChatInfo.TAG_IMG_RIGHT:
                view = LayoutInflater.from(context).inflate(R.layout.recycler_chat_right_img, null);
                return new ImageRightHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (list.get(position).getTag()) {
            case ChatInfo.TAG_Text_LEFT:
                ChatLeftHolder chatLeftHolder = (ChatLeftHolder) holder;
                chatLeftHolder.getTvName().setText(list.get(position).getName());
                chatLeftHolder.getTvContent().setText(list.get(position).getContent());
                break;
            case ChatInfo.TAG_Text_RIGHT:
                ChatRightHolder chatRightHolder = (ChatRightHolder) holder;
                chatRightHolder.getTvName().setText(list.get(position).getName());
                chatRightHolder.getTvContent().setText(list.get(position).getContent());
                break;
            case ChatInfo.TAG_IMG_LEFT:
                ImageLeftHolder imageLeftHolder = (ImageLeftHolder) holder;
                imageLeftHolder.getTvName().setText(list.get(position).getName());
                Glide.with(context)
                        .load(R.drawable.headportrait)
                        .transform(new MultiTransformation(new CenterCrop(),new RoundedCorners(12)))
                        .into(imageLeftHolder.getHeadImage());
                Glide.with(context)
                        .load(list.get(position).getImgUrl())
                        .transform(new MultiTransformation(new CenterCrop(),new RoundedCorners(12)))
                        .into(imageLeftHolder.getImgLeft());
                break;
            case ChatInfo.TAG_IMG_RIGHT:
                Log.i(TAG, "onBindViewHolder: ");
                ImageRightHolder imageRightHolder = (ImageRightHolder) holder;
                imageRightHolder.getTvName().setText("我");
                Glide.with(context)
                        .load(R.drawable.headportrait)
                        .transform(new MultiTransformation(new CenterCrop(),new RoundedCorners(12)))
                        .into(imageRightHolder.getHeadImage());
                Glide.with(context)
                        .load(list.get(position).getImgUrl())
                        .transform(new MultiTransformation(new CenterCrop(),new RoundedCorners(12)))
                        .into(imageRightHolder.getImgRight());

                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}