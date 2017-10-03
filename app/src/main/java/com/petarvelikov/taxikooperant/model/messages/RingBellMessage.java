package com.petarvelikov.taxikooperant.model.messages;

public class RingBellMessage extends AbstractMessage {

    private int seconds;

    public RingBellMessage(int seconds) {
        super();
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }
}
