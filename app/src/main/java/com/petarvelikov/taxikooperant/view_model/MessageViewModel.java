package com.petarvelikov.taxikooperant.view_model;

import com.petarvelikov.taxikooperant.model.interfaces.MessageObservable;

public class MessageViewModel {

    private MessageObservable messageObservable;

    public MessageViewModel(MessageObservable messageObservable) {
        this.messageObservable = messageObservable;
    }
}
