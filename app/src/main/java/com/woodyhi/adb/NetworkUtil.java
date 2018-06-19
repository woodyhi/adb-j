package com.woodyhi.adb;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by June on 2018/6/19.
 */
public class NetworkUtil {


    private static final String TAG = NetworkUtil.class.getSimpleName();

    private static InetAddress getLocalIpAdressFromIntf(String intfName) {
        try {
            NetworkInterface intf = NetworkInterface.getByName(intfName);
            if (intf.isUp()) {
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                        return inetAddress;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Unable to get ip adress for interface " + intfName);
        }
        return null;
    }
    public static InetAddress getLocalIpAddress(Context ctx) throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress != 0)
            return InetAddress.getByName(String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));

        Log.d(TAG, "No ip adress available throught wifi manager, try to get it manually");

        InetAddress inetAddress;

        inetAddress = getLocalIpAdressFromIntf("wlan0");
        if (inetAddress != null) {
            Log.d(TAG, "Got an ip for interfarce wlan0");
            return inetAddress;
        }

        inetAddress = getLocalIpAdressFromIntf("usb0");
        if (inetAddress != null) {
            Log.d(TAG, "Got an ip for interfarce usb0");
            return inetAddress;
        }

        return InetAddress.getByName("0.0.0.0");
    }

}
