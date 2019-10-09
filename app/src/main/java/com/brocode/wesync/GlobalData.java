package com.brocode.wesync;

import java.util.Random;

public class GlobalData {

    static String nick = "Verma";
    public static DeviceRole deviceRole;
    static String password;

    public enum DeviceRole {
        HOST,
        CLIENT
    }
}