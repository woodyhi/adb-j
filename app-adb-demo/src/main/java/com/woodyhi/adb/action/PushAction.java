package com.woodyhi.adb.action;

import com.cgutman.adblib.AdbStream;
import com.woodyhi.adb.AdbAction;

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
public class PushAction extends AdbAction {
    private AdbStream stream;

    private PushCallback callback;

    private String mFilePath;
    private InputStream mInputStream;

    private String mRemotePath;

    public void setCallback(PushCallback callback) {
        this.callback = callback;
    }

    public PushAction() {
    }

    /**
     * @param source 本地文件路径 eg. /sdcard/x.apk
     * @param dest   目标设备文件保存路径. eg. /sdcard/tmp/t.apk
     */
    public void setIO(String source, String dest) {
        this.mFilePath = source;
        this.mRemotePath = dest;
    }

    /**
     * @param source   本地文件输入流
     * @param dest 目标设备文件保存路径. eg. /sdcard/tmp/t.apk
     */
    public void setIO(InputStream source, String dest) {
        this.mInputStream = source;
        this.mRemotePath = dest;
    }

    private void openStream() {
        if (adbConnection == null) {
            throw new NullPointerException("adb未连接");
        }
        try {
            stream = adbConnection.open("sync:");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            onFail(e.getMessage());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            onFail(e.getMessage());
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            onFail(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            onFail(e.getMessage());
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
                        System.out.println(PushAction.class.getSimpleName() + ":" + result);

                        if ("OKAY".equals(result)) {
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

    public void push(String filepath, String remotepath) throws IOException, InterruptedException {
        if (stream == null) {
            throw new NullPointerException("adb stream is null");
        }
        File file = new File(filepath);
        FileInputStream inputStream = new FileInputStream(file);
        push(inputStream, remotepath);
    }

    public void push(InputStream inputStream, String remotepath) throws IOException, InterruptedException {
        if (stream == null) {
            throw new NullPointerException("adb stream is null");
        }

        if (callback != null) {
            callback.onStart();
        }

        //        "{filepath,mode}"
        //        String remote = "/sdcard/tmp/test.apk,33206";
        String remote = remotepath + ",33188";

        ByteBuffer buf = ByteBuffer.allocate(8 + remote.length()).order(ByteOrder.LITTLE_ENDIAN);
        buf.put("SEND".getBytes("UTF-8"));
        buf.putInt(remote.length());
        buf.put(remote.getBytes("UTF-8"));
        stream.write(buf.array());

        int totalSize = inputStream.available();
        int progress = 0;

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

        ByteBuffer order = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        order.put("DONE".getBytes("UTF-8"));
        stream.write(order.array());

        ByteBuffer order2 = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        order2.put("QUIT".getBytes("UTF-8"));
        stream.write(order2.array());
    }

    private void onSuccess() {
        System.out.println("push onSuccess ^0^");
        if (callback != null) {
            callback.onSuccess();
        }
    }

    private void onProgress(int total, int progress) {
        //        System.out.println("total:" + total + ", progress:" + progress);
        if (callback != null) {
            callback.onProgress(total, progress);
        }
    }

    private void onFail(String msg) {
        if (callback != null) {
            callback.onFail(msg);
        }
    }

    @Override
    public void run() {
        try {
            openStream();
        } catch (Exception e) {
            e.printStackTrace();
            onFail(e.getMessage());
            return;
        }

        try {
            if (mFilePath != null) {
                push(mFilePath, mRemotePath);
            } else {
                push(mInputStream, mRemotePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
            onFail(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            onFail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            onFail(e.getMessage());
        }
    }

    public interface PushCallback {
        void onStart();

        void onProgress(int total, int progress);

        void onSuccess();

        void onFail(String msg);
    }

}
