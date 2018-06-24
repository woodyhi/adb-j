package com.woodyhi.adb.entity;

import com.woodyhi.adb.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by June on 2018/6/20.
 */
public class StreamData {
    public int command;       /* command identifier constant      */
    public int arg1;          /* first argument                   */
    public int arg2;          /* second argument                  */
    public int data_length;   /* length of payload (0 is allowed) */
//    public int data_check;    /* checksum of data payload         */
    public int data_crc32;   /*  crc32 of data payload            */
    public int magic;         /* command ^ 0xffffffff             */

    public byte[] data;

    public StreamData(){

    }

    public StreamData(int command, int arg1, int arg2, byte[] data) {
        this.command = command;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.data = data;

        if(data != null){
            data_length = data.length;
            check();
        }
        magic();
    }

    private void check() {
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
        data_crc32 = n2;
    }

    private void magic() {
        magic = command ^ 0xffffffff;
    }


    public byte[] toBytes() {
        ByteBuffer buf = ByteBuffer.allocate(24 + data_length).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(command);
        buf.putInt(arg1);
        buf.putInt(arg2);
        buf.putInt(data_length);
        buf.putInt(data_crc32);
        buf.putInt(magic);
        if (data != null) {
            buf.put(data);
        }
        return buf.array();
    }

    public static StreamData parse(InputStream in) throws IOException {
        StreamData msg = new StreamData();
        ByteBuffer packet = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN);

        /* Read the header first */
        int dataRead = 0;
        do
        {
            int bytesRead = in.read(packet.array(), dataRead, 24 - dataRead);

            if (bytesRead < 0)
                throw new IOException("Stream closed");
            else
                dataRead += bytesRead;
        }
        while (dataRead < 24);

        /* Pull out header fields */
        msg.command = packet.getInt();
        msg.arg1 = packet.getInt();
        msg.arg2 = packet.getInt();
        msg.data_length = packet.getInt();
        msg.data_crc32 = packet.getInt();
        msg.magic = packet.getInt();

        /* If there's a payload supplied, read that too */
        if (msg.data_length != 0)
        {
            msg.data = new byte[msg.data_length];

            dataRead = 0;
            do
            {
                int bytesRead = in.read(msg.data, dataRead, msg.data_length - dataRead);

                if (bytesRead < 0)
                    throw new IOException("Stream closed");
                else
                    dataRead += bytesRead;
            }
            while (dataRead < msg.data_length);
        }
        return msg;
    }

    public static StreamData parseBytes(byte[] bytes){
        ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        int command = buf.getInt();
        int arg1 = buf.getInt();
        int arg2 = buf.getInt();
        int length = buf.getInt();
        int data_crc32 = buf.getInt();
        int magic = buf.getInt();
        System.out.println("command "  + Integer.toHexString(command)
            + " arg1:" + arg1 + " arg2:" + arg2 + " length:" + length
                + " data_crc32:" + data_crc32 + " magic:" + magic
        );

        byte[] b1 = new byte[4];
        System.arraycopy(bytes, 0, b1, 0, 4);
        System.out.println(Util.bytesToIntLittle(b1, 0));
        System.out.println(Integer.toHexString(Util.bytesToIntLittle(b1, 0)));

//        byte[] b2 = new byte[4];
//        System.arraycopy(bytes, 4, b2, 0, 4);
//        System.out.println("b2:");
//        System.out.println(Util.bytesToIntLittle(b2, 0));
//        System.out.println(Integer.toHexString(Util.bytesToIntLittle(b2, 0)));


//        byte[] b3 = new byte[4];
//        System.arraycopy(bytes, 8, b3, 0, 4);
//        System.out.println(new String(b3));
//        System.out.println(Util.bytesToIntLittle(b3, 0));

//        byte[] b4 = new byte[4];
//        System.arraycopy(bytes, 12, b4, 0, 4);
//        System.out.println(new String(b4));
//        System.out.println(Util.bytesToIntLittle(b4, 0));

//        byte[] b5 = new byte[4];
//        System.arraycopy(bytes, 16, b5, 0, 4);
//        System.out.println(new String(b5));
//        System.out.println(Util.bytesToIntLittle(b5, 0));

//        byte[] b6 = new byte[4];
//        System.arraycopy(bytes, 20, b6, 0, 4);
//        System.out.println(new String(b6));
//        System.out.println(Util.bytesToIntLittle(b6, 0));

        byte[] bd = new byte[bytes.length - 24];
        System.arraycopy(bytes, bytes.length - 20, bd, 0, 20);
        StringBuilder sb = new StringBuilder();
        for(byte b: bd){
            sb.append(String.valueOf(b));
        }
        System.out.println(new String(sb.toString()));

        StreamData streamData = new StreamData(command, arg1, arg2, null);
        return streamData;
    }


    public boolean writeLog = true;

    public void write(OutputStream os){
        if(writeLog)
            System.out.println("\nadb send to device : " + Integer.toHexString(command));
        try {
            if (os != null) {
                os.write(toBytes());
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
