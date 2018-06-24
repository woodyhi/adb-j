package com.woodyhi.adb;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.cgutman.adblib.AdbConnection;
import com.cgutman.adblib.AdbCrypto;
import com.cgutman.adblib.AdbStream;
import com.woodyhi.adb.ad.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

/**
 * Created by June on 2018/6/19.
 */
public class AdbManager {
    Logger logger = Logger.getLogger(AdbManager.class.getName());
//        static String host = "10.102.17.163";
    static String host = "10.102.20.11";
//    static String host = "10.102.17.150";
    static int port = 5555;
    /* local */
    static String tmpdir = "/sdcard/tmp/";
    /* remote */
    static String pushpath = "/data/local/tmp/com.woodyhi.demo.adb";


    AdbConnection adb;
    Socket sock;
    AdbCrypto crypto;

    private HandlerThread subThread;
    private Handler handler;

    public AdbManager(Context context) {
        subThread = new HandlerThread("adb");
        subThread.start();
        handler = new Handler(subThread.getLooper());
    }


    private void init(){
        // Setup the crypto object required for the AdbConnection
        try {
            crypto = Util.setupCrypto(tmpdir + "pub.key", tmpdir + "priv.key");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Connect the socket to the remote host
        System.out.println("Socket connecting...");
        try {
            sock = new Socket(host, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Socket connected");

        // Construct the AdbConnection object
        try {
            adb = AdbConnection.create(sock, crypto);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Start the application layer connection process
        System.out.println("ADB connecting...");
        try {
            adb.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("ADB connected");
    }

    public void connect(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    AdbStream stream;

    public void stream() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Open the shell stream of ADB
                //        final AdbStream stream;
                try {
                    stream = adb.open("sync:");
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
                        while (!stream.isClosed())
                            try {
                                // Print each thing we read from the shell stream
                                System.out.print("receivingthread===" + new String(stream.read(), "US-ASCII"));
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
        });
    }


    private void push(String localfilepath, String remotepath) throws IOException, InterruptedException {
        System.out.println("---push start---");
        //        "{filename,mode}"
        String remote = "/sdcard/tmp/test.apk,33206";
        if(remotepath != null && remotepath.length() > 0)
            remote = remotepath;

        ByteBuffer buf = ByteBuffer.allocate(8 + remote.length()).order(ByteOrder.LITTLE_ENDIAN);
        buf.put("SEND".getBytes("UTF-8"));
        buf.putInt(remote.length());
        buf.put(remote.getBytes("UTF-8"));
        stream.write(buf.array());

        /* second */
        File file = new File(localfilepath);
        FileInputStream inputStream = new FileInputStream(file);

        int buffer_size = 2048;
        int len;
        byte[] buff = new byte[buffer_size];
        while ((len = inputStream.read(buff)) != -1) {
            ByteBuffer bb = ByteBuffer.allocate(8 + len).order(ByteOrder.LITTLE_ENDIAN);
            bb.put("DATA".getBytes("UTF-8"));
            bb.putInt(len);
            bb.put(buff, 0, len);


            stream.write(bb.array());
        }
        inputStream.close();

        /* third */
        ByteBuffer order = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        order.put(("DONE\0").getBytes("UTF-8"));
        stream.write(order.array());

        /* fourth */
        ByteBuffer order2 = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        order2.put("QUIT\0".getBytes("UTF-8"));
        stream.write(order2.array());

        System.out.println("---push end---");
    }

    public void getDevices() {
//        try {
//            stream.write("ls -al \n");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        try {
            push(Environment.getExternalStorageDirectory().getAbsolutePath() + "/app.apk", pushpath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    AdbStream shellStream;
    public void install(){
        try {
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Open the shell stream of ADB
                //        final AdbStream stream;
                try {
                    shellStream = adb.open("shell:pm install -t -r " + pushpath);
//                    shellStream = adb.open("shell:am start -a android.intent.action.VIEW -t application/vnd.android.package-archive -d  file://" + tmpdir + "test.apk");
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
                        while (!shellStream.isClosed())
                            try {
                                // Print each thing we read from the shell stream
                                System.out.print(new String(shellStream.read(), "US-ASCII"));
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
        });
    }
}
