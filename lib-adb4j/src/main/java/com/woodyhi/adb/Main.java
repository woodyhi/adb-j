package com.woodyhi.adb;

/**
 * Created by June on 2018/6/19.
 */
public class Main {

    public static void main(String[] args){
        System.out.println("xxx");
        ADB adb = new ADB();
        adb.connect("10.102.20.11", 5555);
    }

}
