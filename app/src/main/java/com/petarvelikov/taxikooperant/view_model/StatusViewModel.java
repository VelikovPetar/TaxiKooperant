package com.petarvelikov.taxikooperant.view_model;

import com.petarvelikov.taxikooperant.model.status.StatusModel;

import io.reactivex.Observable;

public class StatusViewModel {

    private ObservableStatusModel observableStatusModel;

    public StatusViewModel(ObservableStatusModel observableStatusModel) {
        this.observableStatusModel = observableStatusModel;
    }

    public interface ObservableStatusModel {
        Observable<StatusModel> getObservableStatusModel();
    }
}
