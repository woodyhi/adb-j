package com.woodyhi.adb;

/**
 * Created by June on 2018/6/19.
 */
public class Constants {
    public static final int MAX_PAYLOAD = 4096;

    public enum ACTION {
        A_SYNC(0x434e5953),
        A_CNXN(0x4e584e43),
        A_OPEN(0x4e45504f),
        A_OKAY(0x59414b4f),
        A_CLSE(0x45534c43),
        A_WRTE(0x45545257),
        A_AUTH(0x48545541);        ;

        private int value;

        ACTION(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    public static final int A_SYNC = 0x434e5953;
    public static final int A_CNXN = 0x4e584e43;
    public static final int A_OPEN = 0x4e45504f;
    public static final int A_OKAY = 0x59414b4f;
    public static final int A_CLSE = 0x45534c43;
    public static final int A_WRTE = 0x45545257;
    public static final int A_AUTH = 0x48545541;

    /* AUTH constants for arg0.*/
    public static final int AUTH_TOKEN = 1;
    public static final int AUTH_SIGNATURE = 2;
    public static final int AUTH_RSAPUBLICKEY = 3;

    /* ADB protocol version. */
    public static final int A_VERSION = 0x01000000;        // ADB protocol version

    public static final int ADB_VERSION_MAJOR = 1;         // Used for help/version information
    public static final int ADB_VERSION_MINOR = 0;         // Used for help/version information

    public static final int ADB_SERVER_VERSION = 26;    // Increment this when we want to force users to start a new adb server

    /* Maximum size of a filesync DATA packet. */
    public static final int MAX_PUSH_DATA = 2 * 1024;


}
