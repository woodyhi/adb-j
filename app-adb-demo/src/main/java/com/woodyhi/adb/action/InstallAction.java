package com.woodyhi.adb.action;

import com.cgutman.adblib.AdbStream;
import com.woodyhi.adb.AdbAction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by June on 2018/6/25.
 */
public class InstallAction extends AdbAction{

    private AdbStream adbStream;

    private String mApkpath;

    private InstallCallback callback;

    public void setCallback(InstallCallback callback) {
        this.callback = callback;
    }

    public InstallAction() {
    }

    public void setApkpath(String apkpath){
        this.mApkpath = apkpath;
    }

    private void openStream(String apkpath) {
        if (adbConnection == null) {
            throw new NullPointerException("adb未连接");
        }
        // Open the shell stream of ADB
        try {
            String cmd1 = "pm install -t -r " + apkpath;
            String cmd2 = "rm " + apkpath;
            String cmd = cmd1 + ";" + cmd2; // 安装后无论结果都删除文件
            adbStream = adbConnection.open("shell:" + cmd);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        // Start the receiving thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!adbStream.isClosed())
                    try {
                        // Print each thing we read from the shell stream
                        byte[] bytes = adbStream.read();
                        String result = new String(bytes, "US-ASCII");
                        System.out.println(result);
                        if(callback != null){
                            callback.onReceive(result);
                        }

                        if("Success\r\n".equals(result)){
                            System.out.println("install sucess ^0^");
                            adbStream.close();
                            if(callback != null){
                                callback.onSuccess();
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
            }
        }).start();
    }

    public void install(String apkpath) {
        if(callback != null){
            callback.onStart();
        }
        openStream(apkpath);
    }

    @Override
    public void run() {
        try {
            install(mApkpath);
        } catch (Exception e){
            e.printStackTrace();
            onFail(e.getMessage());
        }
    }

    private void onFail(String msg){
        if(callback != null)
            callback.onFail(msg);
    }

    public interface InstallCallback {
        void onStart();
        void onSuccess();
        void onReceive(String msg);
        void onFail(String msg);
    }
}
