package com.woodyhi.adb.ad;

import android.util.Base64;

import com.cgutman.adblib.AdbBase64;


/**
 * Created by June on 2018/6/21.
 */
public class Base64Impl implements AdbBase64 {
    @Override
    public String encodeToString(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
//        return Base64.encodeBase64String(data);
    }
}
