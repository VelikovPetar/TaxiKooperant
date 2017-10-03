package com.petarvelikov.taxikooperant.constants;

public class Constants {

    public static final String USER_ID = "user_id";
    public static final String INTERVAL = "interval";
    public static final String VOLUME = "volume";
    public static final String SHOULD_STOP_FOREGROUND = "should_stop_foreground";
    public static final float DEFAULT_VOLUME = 0.5f;
    public static final int DEFAULT_INTERVAL = 25;

    public interface ACTION {
        String START_FOREGROUND = "com.petarvelikov.taxikooperant.action.startforeground";
        String STOP_FOREGROUND = "com.petarvelikov.taxikooperant.action.stopforeground";
        String START_SERVICE = "com.petarvelikov.taxikooperant.action.start";
        String STOP_SERVICE = "com.petarvelikov.taxikooperant.action.stop";
        String STOP_RINGING = "com.petarvelikov.taxikooperant.action.stopringing";
        String START_RINGING = "com.petarvelikov.taxikooperant.action.startringing";
    }
}

