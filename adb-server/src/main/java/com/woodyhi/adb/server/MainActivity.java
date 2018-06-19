package com.woodyhi.adb.server;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.woodyhi.adb.ADB;

public class MainActivity extends AppCompatActivity {
    private Button connectBtn;
    private Button sendBtn;
    private Button installBtn;
    private Button openAppBtn;
    private Button disconnectBtn;

    ADB adb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        adb = new ADB();
    }

    private void initViews(){
        connectBtn = findViewById(R.id.connect_btn);
        sendBtn = findViewById(R.id.send_btn);
        installBtn = findViewById(R.id.install_btn);
        openAppBtn = findViewById(R.id.open_app);
        disconnectBtn = findViewById(R.id.disconnect);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb.connect("10.102.20.11", 5555);
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb.disconnect();
            }
        });
    }
}
