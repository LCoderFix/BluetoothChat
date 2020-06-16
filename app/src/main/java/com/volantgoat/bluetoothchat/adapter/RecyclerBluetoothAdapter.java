package com.volantgoat.bluetoothchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.volantgoat.bluetoothchat.R;
import com.volantgoat.bluetoothchat.bean.BlueTooth;
import com.volantgoat.bluetoothchat.holder.BluetoothHolder;
import com.volantgoat.bluetoothchat.holder.ToastHolder;

import java.util.List;

/**
 * Create by dong
 * Data:2019/12/10
 */
public class RecyclerBluetoothAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BlueTooth> list;
    private Context context;
    private OnItemClickListener onItemClickListener;



    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public RecyclerBluetoothAdapter(Context context) {
        this.context = context;
    }

    public void setBluetoothData(List<BlueTooth> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == BlueTooth.TAG_NORMAL) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_blue_tooth, viewGroup, false);
            return new BluetoothHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_toast, viewGroup, false);
            return new ToastHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (list.get(i).getTag() == BlueTooth.TAG_NORMAL) {
            BluetoothHolder holder = (BluetoothHolder) viewHolder;
            BlueTooth blueTooth = list.get(i);
            holder.getTvName().setText(blueTooth.getName());
            holder.getTvMac().setText(blueTooth.getMac());
            holder.getTvLevel().setText(blueTooth.getRssi());
            holder.getRlClick().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClick(i);
                    }
                }
            });
        } else {
            ToastHolder holder = ((ToastHolder) viewHolder);
            BlueTooth blueTooth = list.get(i);
            holder.getTvToast().setText(blueTooth.getName());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getTag();
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();

    }
}
