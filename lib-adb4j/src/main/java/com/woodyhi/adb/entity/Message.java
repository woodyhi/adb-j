package com.woodyhi.adb.entity;

/**
 * Created by June on 2018/6/19.
 */
@Deprecated
public class Message {
    public int command;       /* command identifier constant      */
    public int arg0;          /* first argument                   */
    public int arg1;          /* second argument                  */
    public int data_length;   /* length of payload (0 is allowed) */
    public int data_check;    /* checksum of data payload         */
    public int magic;         /* command ^ 0xffffffff             */
}
