package com.petarvelikov.taxikooperant.model.messages;

public class PopupMessage extends AbstractMessage {

    private String textMessage;
    private byte source;

    public PopupMessage(String textMessage, byte source) {
        super();
        this.textMessage = textMessage;
        this.source = source;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public byte getSource() {
        return source;
    }
}
