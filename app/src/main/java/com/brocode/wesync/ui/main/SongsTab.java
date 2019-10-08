package com.brocode.wesync.ui.main;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brocode.wesync.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SongsTab extends Fragment {

    RecyclerView songrv;
    private ArrayList<File> songList;
    File directory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View songs_view = inflater.inflate(R.layout.tab_songs, container, false);
        songrv = songs_view.findViewById(R.id.rvsongs);
        SongRvAdapter myAdapter = new SongRvAdapter(getContext(), songList);
        songrv.setLayoutManager(new LinearLayoutManager(getActivity()));
        songrv.setAdapter(myAdapter);
        return songs_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        directory = new File("/mnt/");
        songList = new ArrayList<>();
        directory = new File("/mnt/sdcard/Music/");
        File listFiles[] = directory.listFiles();
        songList.addAll(Arrays.asList(listFiles));
//        getSongList(directory);
    }

//    public void getSongList() {
//        //retrieve song info
//        ContentResolver musicResolver = getActivity().getContentResolver();
//        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
//        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);
//
//        if (musicCursor != null && musicCursor.moveToFirst()) {
//            //get columns
//            int titleColumn = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media.TITLE);
//            int idColumn = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media._ID);
//            int artistColumn = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media.ARTIST);
//            int albumColumn = musicCursor.getColumnIndex
//                    (MediaStore.Audio.Media.ALBUM_ID);
//
//            //add songs to list
//            do {
//                long thisId = musicCursor.getLong(idColumn);
//                long albumId = musicCursor.getLong(albumColumn);
//                String thisTitle = musicCursor.getString(titleColumn);
//                String thisArtist = musicCursor.getString(artistColumn);
//                final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
//                Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, albumId);
//                songList.add(new Song(thisTitle, thisArtist, albumArtUri.toString()));
//            }
//            while (musicCursor.moveToNext());
//
//            musicCursor.close();
//        }
//
//    }

    private void getSongList(File directory) {

        File listFile[] = directory.listFiles();

        if(listFile!=null && listFile.length>0){

            for(int i=0; i<listFile.length; i++){

                if(listFile[i].isDirectory()) {
                    getSongList(listFile[i]);
                }
                else{
                    boolean flag = false;
                    if(listFile[i].getName().endsWith(".mp3"))
                    {
                        for(int j=0; j<songList.size(); j++)
                        {
                            if(songList.get(j).getName().equals(listFile[i].getName()))
                                flag = true;
                        }

                        if(!flag)
                            songList.add(listFile[i]);
                    }
                }

            }

        }

    }
}
