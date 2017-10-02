package com.petarvelikov.taxikooperant.model.interfaces;

import io.reactivex.Observable;

public interface ConnectionStatusObservable {
    Observable<Integer> getServerStatusObservable();

    Observable<Integer> getNetworkStatusObservable();
}
