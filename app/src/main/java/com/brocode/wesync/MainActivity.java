package com.brocode.wesync;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button refresh;
    NsdClient nsdClient;
    RecyclerView.LayoutManager layoutManager;
    HostListAdapter adapter;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout =  findViewById(R.id.linearLayout);

        SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.sharedPrefsKey), MODE_PRIVATE);
        boolean isFirstRun = sharedPrefs.getBoolean(getString(R.string.isFirstRunSharedPrefsKey),true);
        if(isFirstRun) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            final EditText edittext = new EditText(this);
            alert.setTitle("Set Your Name");

            alert.setView(edittext);

            alert.setPositiveButton("OK", (dialog, whichButton) -> {
                String name = edittext.getText().toString();
                GlobalData.nick = name;
                SharedPreferences.Editor sharedPrefsEditor= sharedPrefs.edit();

                sharedPrefsEditor.putString(getString(R.string.nickSharedPrefsKey), name);
                sharedPrefsEditor.putBoolean(getString(R.string.isFirstRunSharedPrefsKey), false);
                sharedPrefsEditor.commit();
            });
//
//            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    // what ever you want to do with No option.
//                }
//            });

            alert.show();
        }
        GlobalData.nick = sharedPrefs.getString(getString(R.string.nickSharedPrefsKey), "NoNick");
        String name = sharedPrefs.getString(getString(R.string.nickSharedPrefsKey),"NoNick");
        Snackbar snackbar = Snackbar
                .make(linearLayout, name, Snackbar.LENGTH_LONG);
        snackbar.show();


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
                nsdClient.stopDiscovery();
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
