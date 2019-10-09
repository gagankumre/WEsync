package com.brocode.wesync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;

public class DownloadActivity extends AppCompatActivity {

    TextView tvJoiningHost;
    Button btnJoin;
    TextView tvDownloadingMediaText;
    ProgressBar pbDownloadProgress;
    TextView tvDownloadProgress;

    String password;

    FileReceiver fileReceiver;
    Host host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        tvJoiningHost = findViewById(R.id.tv_joiningHost);
        btnJoin = findViewById(R.id.btn_join);
        tvDownloadingMediaText = findViewById(R.id.tv_downloadingMediaText);
        pbDownloadProgress = findViewById(R.id.pb_downloadProgess);
        tvDownloadProgress = findViewById(R.id.tv_downloadProgess);

        tvDownloadingMediaText.setVisibility(View.GONE);
        pbDownloadProgress.setVisibility(View.GONE);
        tvDownloadProgress.setVisibility(View.GONE);

        Intent intent = getIntent();
        host = (Host) intent.getSerializableExtra("hostKey");

        GlobalData.deviceRole = GlobalData.DeviceRole.CLIENT;

        tvJoiningHost.setText("Connecting to " + host.hostName);

        btnJoin.setOnClickListener(v -> {

            tvDownloadingMediaText.setVisibility(View.VISIBLE);
            pbDownloadProgress.setVisibility(View.VISIBLE);
            tvDownloadProgress.setVisibility(View.VISIBLE);
            btnJoin.setVisibility(View.GONE);

            fileReceiver = new FileReceiver(host.hostAddress,3078,DownloadActivity.this);
        });
    }
    public void onFailure(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(DownloadActivity.this, msg, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DownloadActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
    public void onFinishDownload(String path, File file) {
        Intent intent = new Intent(DownloadActivity.this, PlayerActivity.class);
        intent.putExtra(getString(R.string.mediaSelectPathExtra),path);
        intent.putExtra(getString(R.string.mediaSelectFileExtra),file);
        intent.putExtra("Data","DataFromHost");
        intent.putExtra("HOST", (Serializable)host.hostAddress);
        startActivity(intent);
        finish();
    }

    public void setProgress(int progessInPercent, long downloaded, long totalSize) {
        runOnUiThread(() -> {
            pbDownloadProgress.setProgress(progessInPercent);
            tvDownloadProgress.setText("" + downloaded + "MB/" + totalSize + "MB");
        });
    }

}
