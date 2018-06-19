package com.woodyhi.adb;

/**
 * Created by June on 2018/6/19.
 */
public interface ICommand {

    void connect(String ip, int port);

    void pull();

    void push();

    void install();

    void disconnect();
}
