package com.woodyhi.adb.action;

import com.cgutman.adblib.AdbConnection;
import com.cgutman.adblib.AdbStream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by June on 2018/6/25.
 */
public class InstallAction {

    private AdbConnection adb;
    private AdbStream adbStream;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public InstallAction(AdbConnection adbConnection) {
        this.adb = adbConnection;
    }

    private void openStream(String apkpath) {
        // Open the shell stream of ADB
        try {
            adbStream = adb.open("shell:pm install -t -r " + apkpath);
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
                            callback.receive(result);
                        }

                        if("Success\r\n".equals(result)){
                            System.out.println("install sucess ^0^");
                            adbStream.close();
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
        openStream(apkpath);
    }



    public interface Callback{
        void receive(String msg);
    }
}
