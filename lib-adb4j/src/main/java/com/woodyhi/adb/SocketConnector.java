package com.woodyhi.adb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by June on 2018/6/19.
 */
public class SocketConnector {
    private Socket socket;

    public void connect(String ip, int port){
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OutputStream o(){
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public InputStream i(){
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
