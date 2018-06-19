package com.woodyhi.adb;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by June on 2018/6/19.
 */
public class SocketConnector {
    private Socket socket;

    public void connect(String ip, int port){
        try {
            socket = new Socket(ip, port);
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

    public void i(){
        try {
            socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
