package com.petarvelikov.taxikooperant.model.messages;

public class ErrorMessage extends AbstractMessage {

    private String errorMessage;

    public ErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return errorMessage;
    }
}
