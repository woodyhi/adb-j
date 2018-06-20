package com.woodyhi.adb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by June on 2018/6/19.
 */
public class SocketWrapper {

    private String host;
    private int port;
    private Socket socket;
    private SocketCallback socketCallback;

    public SocketWrapper(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(host, port), 3000);

                    if(socketCallback != null)
                        socketCallback.connected();

                    startReadThread();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startReadThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = getInputStream();
                byte[] buff = new byte[1024];

                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                int length;
                while (true) {
                    try {
                        length = inputStream.read(buff);

                        if (length == -1) {
                            System.exit(1);
                        }

                        bos.write(buff, 0, length);
                        if (length < 1024) {
                            onReceive(bos.toByteArray());
                            bos.reset();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void onReceive(byte[] bytes) {
//        System.out.println("received : " + new String(bytes, Charset.forName("UTF-8")));
        if(socketCallback != null){
            socketCallback.handleResult(bytes);
        }
    }

    public OutputStream getOutputStream() {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public InputStream getInputStream() {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void send(byte[] buffer) {
        OutputStream os = getOutputStream();
        try {
            if (os != null) {
                os.write(buffer);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSocketCallback(SocketCallback socketCallback) {
        this.socketCallback = socketCallback;
    }
}
