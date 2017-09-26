package com.petarvelikov.taxikooperant.model.interfaces;

import com.petarvelikov.taxikooperant.model.status.StatusModel;

import io.reactivex.Observable;

public interface NetworkStatusObservable {
    Observable<Integer> getNetworkStatusObservable();
}
