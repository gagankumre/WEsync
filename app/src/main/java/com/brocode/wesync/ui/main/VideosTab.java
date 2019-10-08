package com.brocode.wesync.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brocode.wesync.R;

import java.io.File;
import java.util.ArrayList;

public class VideosTab extends Fragment {

    RecyclerView mRecyclerView;
    VideoRvAdapter mAdapter;
    File directory;

    ArrayList<File> videoList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View video_view = inflater.inflate(R.layout.tab_videos, container, false);
        mRecyclerView = video_view.findViewById(R.id.rvVideo);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new VideoRvAdapter(getContext(), videoList);
        mRecyclerView.setAdapter(mAdapter);
        return video_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        directory = new File("/mnt/");
        videoList = new ArrayList<>();
        getVideoList(directory);
    }

    private void getVideoList(File directory) {

        File listFile[] = directory.listFiles();

        if(listFile!=null && listFile.length>0){

            for(int i=0; i<listFile.length; i++){

                if(listFile[i].isDirectory()) {
                    getVideoList(listFile[i]);
                }
                else{
                    boolean flag = false;
                    if(listFile[i].getName().endsWith(".mp4"))
                    {
                        for(int j=0; j<videoList.size(); j++)
                        {
                            if(videoList.get(j).getName().equals(listFile[i].getName()))
                                flag = true;
                        }

                        if(!flag)
                            videoList.add(listFile[i]);
                    }
                }

            }

        }

    }
}
