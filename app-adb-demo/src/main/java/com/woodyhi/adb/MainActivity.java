package com.woodyhi.adb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.woodyhi.demo.adb.R;

public class MainActivity extends AppCompatActivity {

    private Button connectBtn;
    private Button streamBtn;
    private Button testBtn;
    private Button installBtn;

    private AdbManager adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectBtn = findViewById(R.id.connect);
        streamBtn = findViewById(R.id.stream);
        testBtn = findViewById(R.id.test);
        installBtn = findViewById(R.id.install);

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

        streamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb.stream();
            }
        });

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb.getDevices();
            }
        });

        installBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb.install();
            }
        });
        adb = new AdbManager(this);
    }
}
