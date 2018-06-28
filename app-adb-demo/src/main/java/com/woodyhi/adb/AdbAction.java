package com.woodyhi.adb;

import com.cgutman.adblib.AdbConnection;

/**
 * Created by June on 2018/6/28.
 */
public abstract class AdbAction implements Runnable{
    protected AdbConnection adbConnection;

    public void setAdb(AdbConnection adb){
        this.adbConnection = adb;
    }
}
