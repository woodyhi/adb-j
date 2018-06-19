package com.woodyhi.adb;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by June on 2018/6/19.
 */
public class ADB implements ICommand{

    private final

    SocketConnector socketConnector;

    public ADB(){
        socketConnector = new SocketConnector();
    }

    @Override
    public void connect(String ip, int port){
        socketConnector.connect("10.102.20.11", 5555);
        int version = 0x01000000;
        int maxLength = 256 * 1024;
        OutputStream outputStream = socketConnector.o();
        outputStream.write();
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
