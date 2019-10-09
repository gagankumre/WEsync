package com.brocode.wesync.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.brocode.wesync.GlobalData;
import com.brocode.wesync.R;
import com.brocode.wesync.PlayerActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class VideoRvAdapter extends RecyclerView.Adapter<VideoRvAdapter.MyVideoHolder> {

    Context mContext;
    ArrayList<File> videoList;

//    String last

    public VideoRvAdapter(Context mContext, ArrayList<File> videoList) {
        this.mContext = mContext;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public MyVideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false);
        MyVideoHolder viewHolder = new MyVideoHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyVideoHolder holder, int position) {
        holder.fileName.setText(videoList.get(position).getName());
        Bitmap bmpThumbnail = ThumbnailUtils.createVideoThumbnail(videoList.get(position).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        holder.ivThumbnail.setImageBitmap(bmpThumbnail);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.deviceRole=GlobalData.DeviceRole.HOST;
                Intent intent = new Intent(mContext, PlayerActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable)videoList);
                intent.putExtra("BUNDLE",args);
                intent.putExtra("position", holder.getAdapterPosition());
                intent.putExtra("Data", "DataToBeHosted");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class MyVideoHolder extends RecyclerView.ViewHolder {
        private TextView fileName;
        private ImageView ivThumbnail;
        private CardView mCardView;

        public MyVideoHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.tvVideoName);
            ivThumbnail = itemView.findViewById(R.id.ivThumbNail);
            mCardView = itemView.findViewById(R.id.cvVideo);
        }
    }

}
