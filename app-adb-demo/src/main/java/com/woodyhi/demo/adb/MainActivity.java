package com.woodyhi.demo.adb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.woodyhi.adb.AdbHelper;
import com.woodyhi.adb.action.InstallAction;
import com.woodyhi.adb.action.PushAction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button connectBtn;
    private Button pushBtn;
    private Button installBtn;

    private TextView logTextView;
    private List<String> logList;

    private AdbHelper adbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit_text);
        connectBtn = findViewById(R.id.connect);
        pushBtn = findViewById(R.id.push);
        installBtn = findViewById(R.id.install);
        logTextView = findViewById(R.id.log_text);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editText.getText().toString();
                if (!TextUtils.isEmpty(ip)) {
                    logList = new ArrayList<>();
                    adbHelper.connect(ip);
                }
            }
        });

        pushBtn.setOnClickListener(new View.OnClickListener() {
            long tag;
            @Override
            public void onClick(View v) {
                tag = System.currentTimeMillis();
                adbHelper.push(new PushAction.Callback() {
                    @Override
                    public void success() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logList.add("push success ^0^");
                                log();
                            }
                        });
                    }

                    @Override
                    public void progress(final int total, final int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int tagIdx = logList.indexOf("" + tag);
                                if(tagIdx < 0) {
                                    logList.add("" + tag);
                                    logList.add("totalSize:" + total + ", transfered: " + progress);
                                }else {
                                    logList.remove(tagIdx + 1);
                                    logList.add(tagIdx + 1, "totalSize:" + total + ", transfered: " + progress);
                                }
                                log();
                            }
                        });
                    }

                    @Override
                    public void fail() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logList.add("push fail T_T");
                                log();
                            }
                        });
                    }
                });
            }
        });

        installBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logList.add("start install");
                logList.add("如有安装提示框，请点击安装...");
                log();
                adbHelper.install(new InstallAction.Callback() {
                    @Override
                    public void receive(final String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logList.add(msg);
                                log();
                            }
                        });
                    }
                });
            }
        });

        adbHelper = new AdbHelper(this);
        adbHelper.setAdbConnectListener(new AdbHelper.AdbConnectListener() {
            @Override
            public void socketConnectStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logList.add("socket connecting");
                        log();
                    }
                });
            }

            @Override
            public void socketConnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logList.add("socket connected");
                        log();
                    }
                });
            }

            @Override
            public void adbConnectStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logList.add("adb connecting");
                        log();
                    }
                });
            }

            @Override
            public void adbConnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logList.add("adb connected");
                        log();
                    }
                });
            }
        });
    }


    private void log() {
        logTextView.setText("");
        for (int i = 0; i < logList.size(); i++) {
            if (i == 0) {
                logTextView.append(logList.get(i));
            } else {
                logTextView.append("\n" + logList.get(i));
            }
        }
    }
}
