package com.woodyhi.adb;

import com.woodyhi.adb.entity.StreamData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by June on 2018/6/19.
 */
public class ADB {
    Logger logger = Logger.getLogger(ADB.class.getSimpleName());

    private int REMOTE_ID = 0;
    private int LOCAL_ID = 0x00000000;

    private final String PUBLIC_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA7HCUPjl3s2n5xw3CH0cj\n" +
            "UTG1rWHm4idbMG1siRV8neiYZGeyFL1dIhTZ+Fp8FTazdiKVnEYBjc0Mzh0IJXLa\n" +
            "P8ZgYcKE4UuTAPWPZCN/Wkw4l6Z8wXa/hqJ61BAiSontmgKkLuXPq5jnAFjYP+CV\n" +
            "H8wapl6GBbYJ56ckUDAql3odC8lwzuo6fuB5Vz5duZXhzyPmiUbpkaYK9XIKEUvy\n" +
            "p3JC13wOT3R7EvIMypX6ne1PDtcNm98WWYBFaXOIEvZDh6hN6BblZOnc8ZOdx5PU\n" +
            "xUH41A7XOE+a84P3GDj9BfXyc+JcgIc1oHfAsXPJG+6mStHATMIHF4Si5qmfurMy\n" +
            "pftkk685RLrZhhswHH4Fus+5uWHpBX9qfYe9dCYIOi/5E56fk4NtEzRlVkv+sKEF\n" +
            "QmCiNaPHSbgn/zys1ejePFnIxtbWFzaZfuTgsxuReFI2K/5r4Bea4sIq5znev3/U\n" +
            "ziq4Vzs6DQMFjKbX/LpSCt7xn/V1KlGRMVcE0QJcKgU1OLQwKWwJKzrR77wLopkJ\n" +
            "9yP30+jWpAoYG84YVgfGyWQ0xLzKGJYyXocpL5KuJ05dwkVk5VrCuNCQNJQs0J+5\n" +
            "QfzALZQYr9QXQYO5n6YUHbtUCulWws8WyRMbpRZd5lcRpHuCtioCOv7gf5845UJ7\n" +
            "SZNLil2XRJy2CMkgg170ca8CAwEAAQ==\u0000";


    private SocketWrapper socketWrapper;

    private SocketCallback socketCallback = new SocketCallback() {

        @Override
        public void connected() {
            System.out.println("socket connected");
            int MAX_ADB_DATA = 4096;
            StreamData streamData = new StreamData(Constants.A_CNXN, Constants.A_VERSION, MAX_ADB_DATA, "host::\0".getBytes(Charset.forName("UTF-8")));
            streamData.write(socketWrapper.getOutputStream());
        }


        @Override
        public void handleResult(StreamData msg) {
            String str = String.format("%s command:%s, arg1:%s, arg2:%s, length:%s", getLabel(msg.command), Integer.toHexString(msg.command), msg.arg1, msg.arg2, msg.data_length);
            System.out.println("RECV " + str + "           " + (msg.data == null? "null": new String(msg.data)));

            switch (msg.command) {
                case Constants.A_SYNC:
                    break;
                case Constants.A_CNXN:
                    String ddd = new String(msg.data);
                    String[] da = ddd.split(":");
                    System.out.println("split  " + da[0] + " " + da[1] + " " + da[2]);

                    //                    StreamData open = new StreamData(Constants.A_OPEN, LOCAL_ID, 0, "upload".getBytes());
                    //                    open.write(socketWrapper.getOutputStream());

                    //                    try {
                    //                        push2("lib-adb4j/app-toggle-adbtcp-debug.apk", "/sdcard/tmp/test.apk");
                    //                    } catch (IOException e) {
                    //                        e.printStackTrace();
                    //                    }


//                                        String cmd = "shell:ls -al";
//                                        int maxLength = 256 * 1024;
//                                        StreamData streamData = new StreamData(Constants.A_OPEN, LOCAL_ID, 0, cmd.getBytes());
//                                        socketWrapper.send(streamData.toBytes());
//
                    if(flag) {
                        String dest = "sync:";
                        ByteBuffer bbuf = ByteBuffer.allocate(dest.length() + 1);
                        try {
                            bbuf.put(dest.getBytes("UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        bbuf.put((byte) 0);
                        StreamData streamData = new StreamData(Constants.A_OPEN, ++LOCAL_ID, 0, bbuf.array());
                        streamData.write(socketWrapper.getOutputStream());

                        flag = false;
                    }

                    //                    try {
                    //                        StreamData w1 = new StreamData(Constants.A_WRTE, 0, REMOTE_ID, "ls -l".getBytes("UTF-8"));
                    //                        w1.write(socketWrapper.getOutputStream());
                    //                        StreamData w2 = new StreamData(Constants.A_WRTE, 0, REMOTE_ID, new byte[]{0});
                    //                        w2.write(socketWrapper.getOutputStream());
                    //                    } catch (UnsupportedEncodingException e) {
                    //                        e.printStackTrace();
                    //                    }

                    break;

                case Constants.A_OPEN:
                    break;
                case Constants.A_OKAY:
                    REMOTE_ID = msg.arg2;
                    StreamData o = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, "STAT".getBytes());
                    o.write(socketWrapper.getOutputStream());

                    if(push){
                        try {
                            push2("lib-adb4j/app-toggle-adbtcp-debug.apk", "/sdcard/tmp/test.apk");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        push = false;
                    }

                    break;
                case Constants.A_CLSE:
                    break;
                case Constants.A_WRTE:

                    if(wr) {
                        String cmd = "ls -l\n\0";
                        StreamData streamData = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, cmd.getBytes());
                        streamData.write(socketWrapper.getOutputStream());
                        wr = false;
                    }

                    StreamData sd = new StreamData(Constants.A_OKAY, LOCAL_ID, REMOTE_ID, null);
                    sd.write(socketWrapper.getOutputStream());
                    break;
                case Constants.A_AUTH:
                    System.out.println(str + "          " + Util.bytesToHexFun2(msg.data));
                    StreamData streamData1 = new StreamData(Constants.A_AUTH, 3, 0, PUBLIC_KEY.getBytes());
                    streamData1.write(socketWrapper.getOutputStream());
                    break;
            }
        }

        boolean flag = true;
        boolean wr = false;
        boolean push = true;

        @Override
        public void handleResult(byte[] bytes) {
        }
    };

    private String getLabel(int command){
        String c = null;
        switch (command) {
            case Constants.A_SYNC:
                c = "SYNC";
                break;
            case Constants.A_CNXN:
                c = "CNXN";
                break;
            case Constants.A_OPEN:
                c ="OPEN";
                break;
            case Constants.A_OKAY:
                c = "OKAY";
                break;
            case Constants.A_CLSE:
                c = "CLSE";
                break;
            case Constants.A_WRTE:
                c = "WRTE";
                break;
            case Constants.A_AUTH:
                c = "AUTH";
                break;
        }
        return c;
    }

    public ADB() {
    }

    public void connect(String ip, int port) {
        socketWrapper = new SocketWrapper(ip, 5555);
        socketWrapper.setSocketCallback(socketCallback);
        socketWrapper.connect();
    }

    public void write(String s) throws UnsupportedEncodingException {
        StreamData w1 = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, s.getBytes("UTF-8"));
        w1.write(socketWrapper.getOutputStream());
        StreamData w2 = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, new byte[]{0});
        w2.write(socketWrapper.getOutputStream());
    }

    public void push(String localpath, String remotepath) {
        try {
            //            String location = "/data/local/tmp/adbserver.apk,33206";
            //            String location = "/storage/emulated/0/test.apk";
            String location = "/sdcard/";
            ByteBuffer buf = ByteBuffer.allocate(8 + location.length()).order(ByteOrder.LITTLE_ENDIAN);
            buf.put("SEND".getBytes("UTF-8"));
            buf.putInt(location.length());
            buf.put(location.getBytes("UTF-8"));
            StreamData streamData = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, buf.array());
            streamData.write(socketWrapper.getOutputStream());

            File file = new File(localpath);
            FileInputStream inputStream = new FileInputStream(file);
            byte[] data = getFileByte(inputStream);
            int dataLength = data.length;
            int index = 0;
            int destPosition;
            while (true) {
                destPosition = index + 4096;
                ByteBuffer order = ByteBuffer.allocate(destPosition >= dataLength ? dataLength - index : 4096).order(ByteOrder.LITTLE_ENDIAN);
                order.put(data, index, destPosition >= dataLength ? dataLength - index : 4096);
                StreamData data1 = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, order.array());
                data1.write(socketWrapper.getOutputStream());

                index = destPosition;
                if (index >= dataLength) {
                    break;
                }
            }

            ByteBuffer order = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
            order.put("DONE\u0000".getBytes("UTF-8"));
            StreamData streamData1 = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, order.array());
            streamData1.write(socketWrapper.getOutputStream());

            ByteBuffer order2 = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
            order2.put("QUIT\u0000".getBytes("UTF-8"));
            StreamData streamData2 = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, order2.array());
            streamData2.write(socketWrapper.getOutputStream());

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }

    private byte[] getFileByte(final InputStream inputStream) {
        int n = 0;
        try {
            ArrayList<ByteBuffer> list = new ArrayList<>();
            byte[] array = new byte[65536];
            while (true) {
                final int read = inputStream.read(array);
                if (read == -1) {
                    break;
                }
                final ByteBuffer order = ByteBuffer.allocate(read + 8).order(ByteOrder.LITTLE_ENDIAN);
                order.put("DATA".getBytes("UTF-8"));
                order.putInt(read);
                order.put(array, 0, read);
                n = n + read + 8;
                list.add(order);
            }
            inputStream.close();
            ByteBuffer order2 = ByteBuffer.allocate(n).order(ByteOrder.LITTLE_ENDIAN);
            Iterator<ByteBuffer> iterator = list.iterator();
            while (iterator.hasNext()) {
                order2.put(iterator.next().array());
            }
            return order2.array();
        } catch (Exception ex) {
            return null;
        }
    }

    private void push2(String localfilepath, String remotepath) throws IOException {
        System.out.println("---push start---");
        /* first */
//        String remote = "/data/local/tmp/adbserver.apk,33206";
//        "{filename,mode}"
        String remote = "/sdcard/tmp/test.apk,33206";
        if(remotepath != null && remotepath.length() > 0)
            remote = remotepath;


        ByteBuffer buf = ByteBuffer.allocate(8 + remote.length()).order(ByteOrder.LITTLE_ENDIAN);
        buf.put("SEND".getBytes("UTF-8"));
        buf.putInt(remote.length());
        buf.put(remote.getBytes("UTF-8"));
        StreamData streamData = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, buf.array());
        streamData.write(socketWrapper.getOutputStream());

        /* second */
        File file = new File(localfilepath);
        FileInputStream inputStream = new FileInputStream(file);

        int buffer_size = 2048;
        int len;
        byte[] buff = new byte[buffer_size];
        while ((len = inputStream.read(buff)) != -1) {
            ByteBuffer bb = ByteBuffer.allocate(8 + len).order(ByteOrder.LITTLE_ENDIAN);
            bb.put("DATA".getBytes("UTF-8"));
            bb.putInt(len);
            bb.put(buff, 0, len);

            StreamData data = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, bb.array());
            data.writeLog = false;
            data.write(socketWrapper.getOutputStream());
        }
        inputStream.close();

        /* third */
        ByteBuffer order = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        order.put(("DONE\0").getBytes("UTF-8"));
        StreamData streamData1 = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, order.array());
        streamData1.write(socketWrapper.getOutputStream());

        /* fourth */
        ByteBuffer order2 = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        order2.put("QUIT\0".getBytes("UTF-8"));
        StreamData streamData2 = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, order2.array());
        streamData2.write(socketWrapper.getOutputStream());

        System.out.println("---push end---");
    }

    public void install() {
        String cmd = "shell: pm install /sdcard/launcher-debug.apk";
        StreamData streamData = new StreamData(Constants.A_OPEN, LOCAL_ID, REMOTE_ID, cmd.getBytes());
        streamData.write(socketWrapper.getOutputStream());
    }


    public void disconnect() {
    }


}
