package com.woodyhi.adb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.woodyhi.shell.Commands;
import com.woodyhi.shell.ShellCommander;

/**
 * 需root权限 设置tcp adb
 */
public class MainActivity extends AppCompatActivity {

    private TextView tv2;

    private Button enableBtn;
    private Button disableBtn;
    private Button statusBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv2 = findViewById(R.id.text_view2);
        enableBtn = findViewById(R.id.btn_enable);
        disableBtn = findViewById(R.id.btn_diable);
        statusBtn = findViewById(R.id.status);

        enableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enable();
            }
        });

        disableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disable();
            }
        });

        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "" + getADBStatus(), Toast.LENGTH_SHORT).show();
            }
        });

        tv2.setText("" + getADBStatus());

        setIpAddress();


    }

    // wifi ip地址
    private void setIpAddress() {
        String ip;
        try {
            ip = Commands.execSingleCommand(getString(R.string.get_ipaddress_command)).replace("\n", "");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }
        TextView tv = (TextView) findViewById(R.id.textview1);
        tv.setText(ip);
    }

    private boolean getADBStatus() {
        String prop = null;
        try {
            prop = Commands.execSingleCommand(getString(R.string.get_tcp_port_command)).replace("\n", "");
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), "Could not get adb.tcp.port state", Toast.LENGTH_SHORT);
        }
        return prop != null && !prop.equals("") && Integer.parseInt(prop) != -1;
    }

    private void enable() {
//        Commands.execCommandsAsSU(getResources().getStringArray(R.array.enable_tcp_commands));
        enable2(true);
    }

    private void enable2(boolean b) {
        int port = -1;
        if (b) {
            port = 5555;
        }
        StringBuffer sb = new StringBuffer();
        String[] commands = new String[]{
                "setprop service.adb.tcp.port " + port,
                "stop adbd",
                "start adbd"
        };
        ShellCommander.CommandResult result = ShellCommander.execCommand(commands, false, true);
        if (result.result == 0) {
            sb.append("success\n");
        } else {
            sb.append("fail:" + result.result).append("\n");
        }
        if (!TextUtils.isEmpty(result.successMsg)) {
            sb.append(result.successMsg).append("\n");
        }
        if (!TextUtils.isEmpty(result.errorMsg)) {
            sb.append(result.errorMsg).append("\n");
        }
        Log.d("result", sb.toString());
    }

    private void disable() {
//        Commands.execCommandsAsSU(getResources().getStringArray(R.array.disable_tcp_commands));
        enable2(false);
    }
}
