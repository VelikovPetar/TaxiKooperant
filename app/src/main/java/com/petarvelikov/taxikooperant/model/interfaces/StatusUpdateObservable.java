package com.petarvelikov.taxikooperant.model.interfaces;

import com.petarvelikov.taxikooperant.model.StatusModel;

import io.reactivex.Observable;

public interface StatusUpdateObservable {
    Observable<StatusModel> getStatusUpdatesObservable();
}
