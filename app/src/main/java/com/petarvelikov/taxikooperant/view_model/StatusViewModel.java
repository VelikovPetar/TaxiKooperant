package com.petarvelikov.taxikooperant.view_model;

import com.petarvelikov.taxikooperant.model.interfaces.StatusUpdateObservable;

public class StatusViewModel {

    private StatusUpdateObservable statusUpdateObservable;

    public StatusViewModel(StatusUpdateObservable statusUpdateObservable) {
        this.statusUpdateObservable = statusUpdateObservable;
    }
}
