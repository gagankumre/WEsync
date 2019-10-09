package com.brocode.wesync.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brocode.wesync.GlobalData;
import com.brocode.wesync.R;
import com.brocode.wesync.PlayerActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

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

        holder.mSong.setOnClickListener(v -> {
            GlobalData.deviceRole=GlobalData.DeviceRole.HOST;
            Intent intent = new Intent(mContext, PlayerActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable)songList);
            intent.putExtra("BUNDLE",args);
            intent.putExtra("position", holder.getAdapterPosition());
            intent.putExtra("Data", "DataToBeHosted");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
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
