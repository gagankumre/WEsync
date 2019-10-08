package com.brocode.wesync.ui.main;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brocode.wesync.R;
import com.brocode.wesync.VideoPlayerActivity;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SongRvAdapter extends RecyclerView.Adapter<SongRvAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<File> songList;

    public SongRvAdapter(Context mContext, ArrayList<File> songList) {
        this.mContext = mContext;
        this.songList = songList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_song, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.songName.setText(songList.get(position).getName());
        holder.artistName.setText("Artist");
        holder.location.setText(songList.get(position).getPath());

        holder.mSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable)songList);
                intent.putExtra("BUNDLE",args);
                intent.putExtra("position", holder.getAdapterPosition());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mSong;
        private TextView songName;
        private TextView artistName;
        private TextView location;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mSong = itemView.findViewById(R.id.mSong);
            songName = itemView.findViewById(R.id.tvSongName);
            artistName = itemView.findViewById(R.id.tvArtistName);
            location = itemView.findViewById(R.id.tvSongLocation);
        }
    }

}
