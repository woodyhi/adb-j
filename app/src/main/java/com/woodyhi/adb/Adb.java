package com.woodyhi.adb;

import android.content.Context;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import se.vidstige.jadb.ConnectionToRemoteDeviceException;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

/**
 * Created by June on 2018/6/19.
 */
public class Adb {
    Logger logger = Logger.getLogger(Adb.class.getName());
    //    static String host = "10.102.17.163";
    static String host = "10.102.20.11";
    static int port = 5555;
    JadbConnection jadb;

    public Adb(Context context) {
        jadb = new JadbConnection();
        try {
            jadb = new JadbConnection(NetworkUtil.getLocalIpAddress(context).getHostAddress(), 5037);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
        try {
            logger.info("正在连接无线设备");

            jadb.connectToTcpDevice(new InetSocketAddress(host, port));
            logger.info("连接成功");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JadbException e) {
            e.printStackTrace();
        } catch (ConnectionToRemoteDeviceException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            jadb.disconnectFromTcpDevice(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JadbException e) {
            e.printStackTrace();
        } catch (ConnectionToRemoteDeviceException e) {
            e.printStackTrace();
        }
    }

    public void getDevices() {
        try {
            logger.info("当前已连接设备:");
            List<JadbDevice> devices = jadb.getDevices();
            for (JadbDevice device : devices) {
                //                System.out.println(device.getSerial());
                logger.info(device.getSerial() + "  " + device.getState());
                //                if (device.getSerial().equals(host + ":" + port)) {
                //                    connected = true;
                //                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JadbException e) {
            e.printStackTrace();
        }
    }
}
