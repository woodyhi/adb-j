package com.woodyhi.adb.action;

import com.cgutman.adblib.AdbConnection;
import com.cgutman.adblib.AdbStream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by June on 2018/6/25.
 */
public class InstallWithDeleteAction2 {

    private AdbConnection adb;
    private AdbStream adbStream;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public InstallWithDeleteAction2(AdbConnection adbConnection) {
        this.adb = adbConnection;
    }

    private void openStream(String apkpath) {
        // Open the shell stream of ADB
        try {
            String cmd1 = "pm install -t -r " + apkpath;
            String cmd2 = "rm " + apkpath;
            adbStream = adb.open("shell:" + cmd1 + ";" + cmd2);
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

                        System.err.println("InstallWithDeleteAction2:" + result);

                        if (callback != null) {
                            callback.onReceive(result);
                        }


                        if ("Success\r\n".equals(result)) {
                            System.out.println("install sucess ^0^");

                            if (callback != null) {
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
        if (callback != null) {
            callback.onStart();
        }
        openStream(apkpath);
        if (adbStream == null) {
            return;
        }
    }


    public interface Callback {
        void onStart();

        void onSuccess();

        void onReceive(String msg);

        void onFail();
    }
}
