package com.woodyhi.adb.action;

import com.cgutman.adblib.AdbConnection;
import com.cgutman.adblib.AdbStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by June on 2018/6/25.
 */
public class PushAction {

    private AdbConnection adb;
    private AdbStream stream;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public PushAction(AdbConnection adb) {
        this.adb = adb;
        createStream();
    }

    private void createStream() {
        try {
            stream = adb.open("sync:");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            onFail();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            onFail();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            onFail();
            return;
        }

        // Start the receiving thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stream.isClosed())
                    try {
                        // Print each thing we read from the shell stream

                        byte[] bytes = stream.read();
                        String result = new String(bytes, "US-ASCII").replace("\0", "");
                        if("OKAY".equals(result)){
                            onSuccess();
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

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public void push(String filepath, String remotepath) throws IOException, InterruptedException {
        File file = new File(filepath);
        FileInputStream inputStream = new FileInputStream(file);
        push(inputStream, remotepath);
    }

    public void push(InputStream inputStream, String remotepath) throws IOException, InterruptedException {
        /* first step */
        //        "{filename,mode}"
        //        String remote = "/sdcard/tmp/test.apk,33206";
        //        if(remotepath != null && remotepath.length() > 0)
        String remote = remotepath;

        ByteBuffer buf = ByteBuffer.allocate(8 + remote.length()).order(ByteOrder.LITTLE_ENDIAN);
        buf.put("SEND".getBytes("UTF-8"));
        buf.putInt(remote.length());
        buf.put(remote.getBytes("UTF-8"));
        stream.write(buf.array());

        int totalSize = inputStream.available();
        int progress = 0;
        System.out.println("file length " + totalSize);
        /* second step */
        int buffer_size = 2048;
        int len;
        byte[] buff = new byte[buffer_size];
        while ((len = inputStream.read(buff)) != -1) {
            ByteBuffer bb = ByteBuffer.allocate(8 + len).order(ByteOrder.LITTLE_ENDIAN);
            bb.put("DATA".getBytes("UTF-8"));
            bb.putInt(len);
            bb.put(buff, 0, len);
            stream.write(bb.array());
            onProgress(totalSize, progress += len);
        }
        inputStream.close();

        /* third step */
        ByteBuffer order = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        order.put(("DONE\0").getBytes("UTF-8"));
        stream.write(order.array());

        /* fourth step */
        ByteBuffer order2 = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        order2.put("QUIT\0".getBytes("UTF-8"));
        stream.write(order2.array());
    }

    private void onSuccess() {
        System.out.println("push success ^0^");
        if(callback != null){
            callback.success();
        }
    }

    private void onProgress(int total, int progress){
        System.out.println(progress + "/" + total);
        if(callback != null){
            callback.progress(total, progress);
        }
    }

    private void onFail() {
        System.out.println("push fail T_T");
        if(callback != null){
            callback.fail();
        }
    }


    public interface Callback {
        void success();
        void progress(int total, int progress);
        void fail();
    }
}
