package com.woodyhi.adb;

import com.woodyhi.adb.entity.StreamData; /**
 * Created by June on 2018/6/20.
 */
public abstract class SocketCallback {

    public void connected(){

    }

    public void handleResult(byte[] bytes){

    }

    public void handleResult(StreamData msg) {

    }
}
