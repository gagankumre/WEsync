package com.brocode.wesync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    int position = -1;

    ArrayList<File> videoList;
    SimpleExoPlayer player;
    PlayerView pvExoplayer;

    private RecyclerView rvClientList;
    private RecyclerView.LayoutManager layoutManager;

    private String path;
    private File file;
    public SyncServer syncServer;
    private TextView tvCurTime,tvTotalTime;
    private SeekBar seekBar;
    long playbackPosition;
    private Thread seekBarSyncThread;
    SyncClient syncClient;
    public ClientListAdapter adapter;
    private NsdHost nsdHost;
    private FileSender fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

//        videoView = findViewById(R.id.myPlayer);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        Intent intent = getIntent();

        String s=intent.getStringExtra("Data");
        if(s.equals("DataFromHost")){
            file= (File) intent.getSerializableExtra(getString(R.string.mediaSelectFileExtra));
            path=intent.getStringExtra(getString(R.string.mediaSelectPathExtra));
        }
        else if(s.equals("DataToBeHosted"))
        {
            Bundle args = intent.getBundleExtra("BUNDLE");
            videoList = (ArrayList<File>) args.getSerializable("ARRAYLIST");
            position = intent.getIntExtra("position", -1);
            file=videoList.get(position);
            path=file.getPath();
        }

        Log.e("Path", path);

        ImageButton b=findViewById(R.id.btn_playPause);
        b.setOnClickListener(v -> {
            syncServer.togglePlayState();
        });
        playerVideo();
        initRvClientList();

        if (GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            nsdHost = new NsdHost(getApplicationContext());    // why not accepting context parameter
            nsdHost.registerService();

            fs = new FileSender(path, 3078);

            syncServer = new SyncServer(this);
            try {
                InetAddress addr = InetAddress.getByName("127.0.0.1");
                syncClient = new SyncClient(this, addr);
            } catch (UnknownHostException e) {
                Log.e("PlayerActivity", e.toString());
            }
        } else if (GlobalData.deviceRole == GlobalData.DeviceRole.CLIENT) {
            InetAddress address = (InetAddress) getIntent().getSerializableExtra("HOST");
            Log.e("address", address.toString());
            syncClient = new SyncClient(this, address);
        }

    }

    private void playerVideo() {



        //ritwik

//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(videoView);
//
//        videoView.setMediaController(mediaController);
//        videoView.setVideoPath(String.valueOf(videoList.get(position)));
//        videoView.requestFocus();
//
//        videoView.setOnPreparedListener(mp -> videoView.start());
//
//        videoView.setOnCompletionListener(mp -> {
//            videoView.setVideoPath(String.valueOf(videoList.get(position = position+1)));
//            videoView.start();
//        });





        // Remove action bar and status bar for proper fullscreen
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        player = ExoPlayerFactory.newSimpleInstance(this);
        pvExoplayer = findViewById(R.id.pv_exoplayer);
        pvExoplayer.setPlayer(player);
        pvExoplayer.setUseController(false);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)));

        // This is the MediaSource representing the media to be played.
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(file));

        // Prepare the player with the source.
        player.prepare(mediaSource);

        tvCurTime = findViewById(R.id.tv_curTime);
        tvTotalTime = findViewById(R.id.tv_totalTime);
        seekBar = findViewById(R.id.sb_seekbar);

        if(GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    long duration = player.getDuration();
                    playbackPosition = i * duration / 100;

                    if (b) {
                        syncServer.sync();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        seekBarSyncThread = new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e("PLAYER", e.toString());
            }
            while (syncClient.isRunning()) {
                seekBar.setProgress((int) player.getCurrentPosition() * 100 / (int) player.getDuration());
                runOnUiThread(() -> {
                    long duration = player.getDuration();
                    long pbp = getExactPlaybackPosition();
                    String totalTime = String.format("%02d:%02d:%02d", (duration/3600000) % 24, (duration/60000) % 60, (duration/1000) % 60);
                    String curTime = String.format("%02d:%02d:%02d", (pbp/3600000) % 24, (pbp/60000) % 60, (pbp/1000) % 60);
                    tvTotalTime.setText(totalTime);
                    tvCurTime.setText(curTime);
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Log.e("PLAYER", e.toString());
                }
            }
        });
        seekBarSyncThread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show();
        Log.d("PLAYER_ACTIVITY", "Destroy");

        if (GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            nsdHost.unRegisterService();
            fs.harakiri();
            fs = null;
        }

        if (syncClient != null) syncClient.close();
        if (syncServer != null) syncServer.close();

        player.release();
    }


    public void initRvClientList() {
        if(GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            // Setup the entire recycler view for client list
            rvClientList = findViewById(R.id.rv_clientList);
            layoutManager = new LinearLayoutManager(this);
            adapter = new ClientListAdapter(this);

            rvClientList.setHasFixedSize(true);
            rvClientList.setLayoutManager(layoutManager);
            rvClientList.setAdapter(adapter);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.e("PlayerActivity", "Config Changed");

        int currentOrientation = getResources().getConfiguration().orientation;
        ViewGroup.LayoutParams params = pvExoplayer.getLayoutParams();
        if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvClientList.setVisibility(View.GONE);
            params.height = params.MATCH_PARENT;
            pvExoplayer.setLayoutParams(params);
        } else if(currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            rvClientList.setVisibility(View.VISIBLE);
            params.height = 270 * getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT;
            pvExoplayer.setLayoutParams(params);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public long getPlaybackPosition() {
        return playbackPosition;
    }

    public long getExactPlaybackPosition() {
        return player.getCurrentPosition();
    }

    public void seekTo(long l) {
        player.seekTo(l);
    }

    public void setPlay(boolean b) {
        player.setPlayWhenReady(b);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
