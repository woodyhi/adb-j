package com.woodyhi.adb;

/**
 * Created by June on 2018/6/19.
 */
public class Util {

    public static int crc32(byte[] data) {
        int data_length = data.length;
        int n2 = 0, i = 0;
        if (data != null) {
            while (i < data_length) {
                final byte b = data[i];
                if (b >= 0) {
                    n2 += b;
                } else {
                    n2 += b + 256;
                }
                ++i;
            }
        }
        return n2;
    }
}
