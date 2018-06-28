package com.woodyhi.demo.adb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
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
    private List<String> logList = new ArrayList<>();

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
        logTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        logTextView.setHorizontallyScrolling(true); // 不让超出屏幕的文本自动换行，使用滚动条
        logTextView.setFocusable(true);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editText.getText().toString();
                if (!TextUtils.isEmpty(ip)) {
                    adbHelper.connect(ip);
                }
            }
        });

        pushBtn.setOnClickListener(new View.OnClickListener() {
            private int index = -1;
            private long time;

            @Override
            public void onClick(View v) {
                PushAction pushAction = new PushAction();
                pushAction.setInputStream(getApplicationContext().getResources().openRawResource(R.raw.tvportal));
                pushAction.setRemotePath(AdbHelper.remote_dir + AdbHelper.remote_filename);
                pushAction.setCallback(new PushAction.PushCallback() {
                    @Override
                    public void onStart() {
                        index = -1;
                        time = System.currentTimeMillis();
                        log("\npush file");
                    }

                    @Override
                    public void onSuccess() {
                        log("上传成功了！");
                    }

                    @Override
                    public void onProgress(final int total, final int progress) {
                        String sp = "";
                        long d = System.currentTimeMillis() - time;
                        float s = (float) progress / d;
                        sp = String.format("%.2fKB/s", s);

                        final String spd = sp;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String fm = "size:%s,transfer:%s,avgSpeed: %s";
                                String m = String.format(fm, total, progress, spd);
                                if (index == -1) {
                                    logList.add(m);
                                    index = logList.indexOf(m);
                                } else {
                                    logList.remove(index);
                                    logList.add(index, m);
                                }
                                log();
                            }
                        });
                    }

                    @Override
                    public void onFail(String msg) {
                        log(msg);
                    }
                });
                adbHelper.execute(pushAction);
            }
        });

        installBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstallAction installAction = new InstallAction();
                installAction.setApkpath(AdbHelper.remote_dir + AdbHelper.remote_filename);
                installAction.setCallback(new InstallAction.InstallCallback() {
                    @Override
                    public void onStart() {
                        log("\nstart install");
                        log("目标设备如显示安装提示框，请点击‘安装’按钮");
                    }

                    @Override
                    public void onSuccess() {
                        log("安装成功了！");
                    }

                    @Override
                    public void onReceive(String msg) {
                        log(msg);
                    }

                    @Override
                    public void onFail(String msg) {
                        log(msg);
                    }
                });
                adbHelper.execute(installAction);
            }
        });

        adbHelper = new AdbHelper(this);
        adbHelper.setAdbConnectListener(new AdbHelper.AdbConnectListener() {
            @Override
            public void socketConnectStart() {
                log("\nsocket connecting");
            }

            @Override
            public void socketConnected() {
                log("socket connected");
            }

            @Override
            public void adbConnectStart() {
                log("adb connecting");
            }

            @Override
            public void adbConnected() {
                log("adb connected");
            }

            @Override
            public void error(String msg) {
                log(msg);
            }
        });
    }


    private void log(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logList.add(msg);
                log();
            }
        });
    }

    private void log() {
        logTextView.setText("");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < logList.size(); i++) {
            if (i == 0) {
                sb.append(logList.get(i));
            } else {
                sb.append("\n" + logList.get(i));
            }
        }
        logTextView.setText(sb.toString());
        int offset = (logTextView.getLineCount()) * logTextView.getLineHeight();
        ScrollView scrollView = (ScrollView) logTextView.getParent();
        scrollView.scrollTo(0, offset);
    }
}
