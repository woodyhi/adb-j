package com.woodyhi.adb;

import com.woodyhi.adb.entity.StreamData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by June on 2018/6/19.
 */
public class ADB implements ICommand {
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
            //
            int maxLength = 256 * 1024;
            StreamData streamData = new StreamData(Constants.A_CNXN, Constants.A_VERSION, maxLength, "host::features=shell_2".getBytes());
            socketWrapper.send(streamData.toBytes());
        }

        @Override
        public void handleResult(byte[] bytes) {
            ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            int command = buf.getInt();
            int arg1 = buf.getInt();
            int arg2 = buf.getInt();
            int length = buf.getInt();

            String str = String.format("%s command:%s, arg1:%s, arg2:%s, length:%s", getLabel(command), Integer.toHexString(command), arg1, arg2, length);
//            logger.info(str);
            byte[] data = new byte[length];
            System.arraycopy(bytes, bytes.length - length, data, 0, length);
            logger.info(str + "\n" + new String(data));

            String c = null;
            switch (command) {
                case Constants.A_SYNC:
                    c = "SYNC";
                    break;
                case Constants.A_CNXN:
                    c = "CNXN";
                    try {
                        push2("lib-adb4j/test.apk", "/sdcard/tmp/test.apk");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    StreamData sd = new StreamData(Constants.A_SYNC, LOCAL_ID, REMOTE_ID, "sync:\u0000".getBytes());
//                    sd.write(socketWrapper.getOutputStream());

//                    String cmd = "shell: pm install /sdcard/launcher-debug.apk";
//                    StreamData streamData = new StreamData(Constants.A_OPEN, LOCAL_ID, REMOTE_ID, cmd.getBytes());
//                    streamData.write(socketWrapper.getOutputStream());

//                    String cmd = "shell:ls";
//                    int maxLength = 256 * 1024;
//                    StreamData streamData = new StreamData(Constants.A_OPEN, Constants.A_VERSION, 0, cmd.getBytes());
//                    socketWrapper.send(streamData.toBytes());

                    break;

                case Constants.A_OPEN:
                    c ="OPEN";
                    break;
                case Constants.A_OKAY:
                    c = "OKAY";
                    //                    REMOTE_ID = arg1;
                    break;
                case Constants.A_CLSE:
                    c = "CLSE";
                    break;
                case Constants.A_WRTE:
                    c = "WRTE";
                    break;
                case Constants.A_AUTH:
                    c = "AUTH";
                    StreamData streamData1 = new StreamData(Constants.A_AUTH, 3, 0, PUBLIC_KEY.getBytes());
                    streamData1.write(socketWrapper.getOutputStream());
                    break;
            }



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

    @Override
    public void connect(String ip, int port) {
        socketWrapper = new SocketWrapper("10.102.20.11", 5555);
        socketWrapper.setSocketCallback(socketCallback);
        socketWrapper.connect();
    }

    private void send() {

    }

    private void read(InputStream inputStream) {
        try {
            InputStream is = inputStream;
            byte[] buffer = new byte[1024];
            int length = 0;
            //            boolean mRunning = true;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //            while (mRunning) {
            length = is.read(buffer, 0, buffer.length);
            if (length == -1) {
                //                    disconnct();
                //                    mRunning = false;
                //                    break;
                System.out.println("no data");
                return;
            }

            bos.write(buffer, 0, length);
            if (length < 1024) {
                //                    if (mCallback != null) {
                //                        mCallback.receive(bos.toByteArray());
                System.out.println(new String(bos.toByteArray(), Charset.forName("UTF-8")));
                StreamData streamData = StreamData.parseBytes(bos.toByteArray());
                if (streamData.command == Constants.A_AUTH) {

                    StreamData streamData1 = new StreamData(Constants.A_AUTH, 3, 0, PUBLIC_KEY.getBytes());
                    streamData1.write(socketWrapper.getOutputStream());
                }
                bos.reset();
                //                    }
            }
            //            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pull() {

    }

    @Override
    public void push(String path) {

    }

    @Override
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
        logger.info("---push start---");
        /* first */
        String remote = "/data/local/tmp/adbserver.apk,33206";
//        String remote = "/sdcard/";
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
            int count = len + 8;
            ByteBuffer bb = ByteBuffer.allocate(count).order(ByteOrder.LITTLE_ENDIAN);
            bb.put("DATA".getBytes("UTF-8"));
            bb.putInt(len);
            bb.put(buff, 0, len);

            StreamData data = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, bb.array());
            data.write(socketWrapper.getOutputStream());
        }

        /* third */
        ByteBuffer order = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        order.put("DONE\u0000".getBytes("UTF-8"));
        StreamData streamData1 = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, order.array());
        streamData1.write(socketWrapper.getOutputStream());

        /* fourth */
        ByteBuffer order2 = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        order2.put("QUIT\u0000".getBytes("UTF-8"));
        StreamData streamData2 = new StreamData(Constants.A_WRTE, LOCAL_ID, REMOTE_ID, order2.array());
        streamData2.write(socketWrapper.getOutputStream());

        logger.info("---push end---");
    }

    @Override
    public void install() {
        //        String path = Environment.getExternalStorageDirectory() + File.separator + "YuMeng/download/apks/net.myvst.v2_3120.apk.";
        String path = "lib-adb4j/test.apk";
        String command = "shell: pm install -r " + path;
        StreamData streamData = new StreamData(Constants.A_OPEN, LOCAL_ID, REMOTE_ID, command.getBytes());
        streamData.write(socketWrapper.getOutputStream());
    }


    public void disconnect() {
    }


}
