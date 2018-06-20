package com.woodyhi.adb;

/**
 * Created by June on 2018/6/19.
 */
public interface ICommand {

    void connect(String ip, int port);

    void pull();

    void push(String path);

    void push(String localpath, String remotepath);

    void install();

    void disconnect();
}
