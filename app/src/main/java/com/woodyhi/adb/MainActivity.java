package com.woodyhi.adb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Button connectBtn;
    private Button disconnectBtn;
    private Button devicesBtn;

    private Adb adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectBtn = findViewById(R.id.connect);
        disconnectBtn = findViewById(R.id.disconnect);
        devicesBtn = findViewById(R.id.getdevices);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        adb.connect();
                    }
                }).start();
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb.disconnect();
            }
        });

        devicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb.getDevices();
            }
        });

        adb = new Adb(this);
    }
}
