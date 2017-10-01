package com.petarvelikov.taxikooperant.model.interfaces;


import io.reactivex.Observable;

public interface ServerStatusObservable {
    Observable<Integer> getServerStatusObservable();
}
