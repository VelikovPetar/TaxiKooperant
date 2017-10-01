package com.petarvelikov.taxikooperant.constants;

public class Constants {

    public static final String USER_ID = "user_id";
    public static final String INTERVAL = "interval";
    public static final String SHOULD_STOP_FOREGROUND = "should_stop_foreground";

    public interface ACTION {
        public static String START_FOREGROUND = "com.petarvelikov.taxikooperant.action.startforeground";
        public static String STOP_FOREGROUND = "com.petarvelikov.taxikooperant.action.stopforeground";
        public static String START_SERVICE = "com.petarvelikov.taxikooperant.action.start";
        public static String STOP_SERVICE = "com.petarvelikov.taxikooperant.action.stop";
    }
}

