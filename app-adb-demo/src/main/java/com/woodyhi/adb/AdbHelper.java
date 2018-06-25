package com.woodyhi.adb;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.cgutman.adblib.AdbConnection;
import com.cgutman.adblib.AdbCrypto;
import com.woodyhi.adb.action.InstallAction;
import com.woodyhi.adb.action.PushAction;
import com.woodyhi.demo.adb.R;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

/**
 * Created by June on 2018/6/19.
 */
public class AdbHelper {
    Logger logger = Logger.getLogger(AdbHelper.class.getName());
    //        static String host = "10.102.17.163";
//    static String host = "10.102.20.11";
    static String host = "192.168.1.104";
    //    static String host = "10.102.17.150";
    static int port = 5555;
    /* local */
    static String tmpdir;

    /* remote path */
    static String remote_dir = "/data/local/tmp/";
    static String remote_filename = "tvportal.apk";

    private Context context;

    private AdbConnection adb;
    private Socket sock;
    private AdbCrypto crypto;

    private HandlerThread handleThread;
    private Handler threadHandler;


    private AdbConnectListener adbConnectListener;

    public void setAdbConnectListener(AdbConnectListener adbConnectListener) {
        this.adbConnectListener = adbConnectListener;
    }

    public interface AdbConnectListener{
        void socketConnectStart();
        void socketConnected();
        void adbConnectStart();
        void adbConnected();
    }

    public AdbHelper(Context context) {
        this.context = context;
        tmpdir = context.getFilesDir().getAbsolutePath();
        handleThread = new HandlerThread("adb");
        handleThread.start();
        threadHandler = new Handler(handleThread.getLooper());
    }


    private void init() {
        // Setup the crypto object required for the AdbConnection
        try {
            crypto = AdbCryptoUtil.setupCrypto(tmpdir + "/pub.key", tmpdir + "/priv.key");
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
        if (adbConnectListener != null)
            adbConnectListener.socketConnectStart();
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
        if (adbConnectListener != null)
            adbConnectListener.socketConnected();

        // Construct the AdbConnection object
        try {
            adb = AdbConnection.create(sock, crypto);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Start the application layer connection process
        System.out.println("ADB connecting...");
        if (adbConnectListener != null)
            adbConnectListener.adbConnectStart();

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
        if(adbConnectListener != null)
            adbConnectListener.adbConnected();
    }

    boolean connect;

    public void connect(String ip) {
        host = ip;
        if (connect)
            return;
        connect = true;
        threadHandler.post(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    public void push(final PushAction.Callback callback) {
        threadHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    PushAction pushAction = new PushAction(adb);
                    pushAction.setCallback(callback);
                    pushAction.push(context.getResources().openRawResource(R.raw.tvportal), remote_dir + remote_filename);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void install(final InstallAction.Callback callback) {
        threadHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    InstallAction installAction = new InstallAction(adb);
                    installAction.setCallback(callback);
                    installAction.install(remote_dir + remote_filename);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void disconnect() {
        try {
            adb.close();
            connect = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
