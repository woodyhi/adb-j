package com.woodyhi.adb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by June on 2018/6/19.
 */
public class ADB implements ICommand{

    private final SocketConnector socketConnector;

    public ADB(){
        socketConnector = new SocketConnector();
    }

    @Override
    public void connect(String ip, int port){
        socketConnector.connect("10.102.20.11", 5555);
        int version = 0x01000000;
        int maxLength = 256 * 1024;

        read(socketConnector.i());
    }


    private void read(InputStream inputStream){
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
                    System.out.println(bos.toByteArray());
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
    public void push() {

    }

    @Override
    public void install() {

    }


    public void disconnect() {
//        socketConnector.
    }

    private void send(int command, int arg1, int arg2, byte[] data) {
        int length = (data == null ? 0 : data.length);
        ByteBuffer buf = ByteBuffer.allocate(24 + length).order(ByteOrder.LITTLE_ENDIAN);
        int data_length = length;
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
        int data_crc32 = n2;
        int magic = command ^ 0xffffffff;

        buf.putInt(command);
        buf.putInt(arg1);
        buf.putInt(arg2);
        buf.putInt(data_length);
        buf.putInt(data_crc32);
        buf.putInt(magic);
        if (data != null) {
            buf.put(data);
        }
//        Log.i(LOG_TAG, "send length : " + data_length);
//        mConnect.send(buf.array());
    }

}
