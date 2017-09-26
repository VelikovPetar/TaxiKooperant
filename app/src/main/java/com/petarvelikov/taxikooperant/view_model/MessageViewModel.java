package com.petarvelikov.taxikooperant.view_model;

import com.petarvelikov.taxikooperant.model.messages.AbstractMessage;

import io.reactivex.Observable;

public class MessageViewModel {

    private ObservableMessageModel observableMessageModel;

    public MessageViewModel(ObservableMessageModel observableMessageModel) {
        this.observableMessageModel = observableMessageModel;
    }

    public interface ObservableMessageModel {
        Observable<AbstractMessage> getObservableModel();
    }
}
