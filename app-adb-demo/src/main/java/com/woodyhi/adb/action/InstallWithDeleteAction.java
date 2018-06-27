package com.woodyhi.adb.action;

import com.cgutman.adblib.AdbConnection;
import com.cgutman.adblib.AdbStream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by June on 2018/6/25.
 */
public class InstallWithDeleteAction {

    private AdbConnection adb;
    private AdbStream adbStream;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public InstallWithDeleteAction(AdbConnection adbConnection) {
        this.adb = adbConnection;
    }

    private void openStream(String apkpath) {
        // Open the shell stream of ADB
        try {
            adbStream = adb.open("shell:");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        } catch (IllegalStateException e) {
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

                        System.err.println("received:" + result);

                        if(callback != null){
                            callback.onReceive(result);
                        }

                        if("Success\r\n".equals(result)){
                            System.out.println("install sucess ^0^");
//                            adbStream.close();

                            synchronized (InstallWithDeleteAction.this) {
                                InstallWithDeleteAction.this.notify();
                            }

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

    public void install(String apkpath) throws IOException, InterruptedException {
        if(callback != null){
            callback.onStart();
        }
        openStream(apkpath);
        if(adbStream == null){
            return;
        }

        adbStream.write("pm install -t -r " + apkpath + "\n");

        synchronized (InstallWithDeleteAction.this){
            System.out.println("xxxxxxxxxxxxxxx");
            this.wait();
            System.out.println("yyyyyyyyyyyyyyy");
        }
        // 安装成功后删除
        adbStream.write("rm " + apkpath + "\n");
    }



    public interface Callback{
        void onStart();
        void onSuccess();
        void onReceive(String msg);
        void onFail();
    }
}
