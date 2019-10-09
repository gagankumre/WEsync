package com.brocode.wesync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button refresh;
    NsdClient nsdClient;
    RecyclerView.LayoutManager layoutManager;
    HostListAdapter adapter;
    ArrayList<Host> hostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // Get Read/Write permissions
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE},12);
//        }

        SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.sharedPrefsKey), MODE_PRIVATE);
        boolean isFirstRun = sharedPrefs.getBoolean(getString(R.string.isFirstRunSharedPrefsKey),true);
        if(isFirstRun) {
            Random random = new Random();
            int x = random.nextInt(100000);

            String s = GlobalData.nick;
            s+=Integer.toString(x);

            GlobalData.nick = s;
            SharedPreferences.Editor sharedPrefsEditor= sharedPrefs.edit();

            sharedPrefsEditor.putString(getString(R.string.nickSharedPrefsKey), GlobalData.nick);
            sharedPrefsEditor.putBoolean(getString(R.string.isFirstRunSharedPrefsKey), false);
            sharedPrefsEditor.commit();
        }
        GlobalData.nick = sharedPrefs.getString("nickSyncPlayerUser", "NoNick");


        refresh =findViewById(R.id.refresh);
        recyclerView=findViewById(R.id.rv_hostList);
        recyclerView.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);
        LinearLayout lib = findViewById(R.id.library);
        LinearLayout connections=findViewById(R.id.connections);


        layoutManager=new LinearLayoutManager(this);
        adapter=new HostListAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        nsdClient =new NsdClient(getApplicationContext(),adapter);
        GlobalData.deviceRole = GlobalData.DeviceRole.CLIENT;


        lib.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChooseFileActivity.class);
            startActivity(intent);
        });
        connections.setOnClickListener(v -> {

            if(recyclerView.getVisibility()==View.VISIBLE){
                recyclerView.setVisibility(View.GONE);
                refresh.setVisibility(View.GONE);
                adapter.clear();
            }else{

                recyclerView.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.VISIBLE);
                nsdClient.discoverServices();
            }


        });

        refresh.setOnClickListener(v -> {
            nsdClient.stopDiscovery();
            adapter.clear();

            nsdClient=new NsdClient(getApplicationContext(),adapter);
            nsdClient.discoverServices();
        });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        nsdClient.stopDiscovery();
//    }
}
