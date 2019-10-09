package com.brocode.wesync;

import java.util.Random;

public class GlobalData {

    static String nick = "NoNick";
    public static DeviceRole deviceRole;
    static String password;

    public enum DeviceRole {
        HOST,
        CLIENT
    }
}