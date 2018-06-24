package com.woodyhi.adb;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by June on 2018/6/19.
 */
public class Main {

    public static void main(String[] args){
//        Scanner in = new Scanner(System.in);
        String host = "10.102.20.11";
//        String host = "192.168.1.13";
        ADB adb = new ADB();
        adb.connect(host, 5555);

        // We become the sending thread
//        while (true){
//            try {
//                adb.write(in.nextLine()+'\n');
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//        }
    }

}
