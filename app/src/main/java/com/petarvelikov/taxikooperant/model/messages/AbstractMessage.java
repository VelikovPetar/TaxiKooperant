package com.petarvelikov.taxikooperant.model.messages;

public abstract class AbstractMessage {

    protected long timestamp;

    public AbstractMessage() {
        this.timestamp = System.currentTimeMillis();
    }

    protected long getTimestamp() {
        return timestamp;
    }
}
