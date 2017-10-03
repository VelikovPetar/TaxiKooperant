package com.petarvelikov.taxikooperant.model.messages;

public abstract class AbstractMessage {

    private long timestamp;

    public AbstractMessage() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
