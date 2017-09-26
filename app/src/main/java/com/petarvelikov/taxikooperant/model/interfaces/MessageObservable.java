package com.petarvelikov.taxikooperant.model.interfaces;

import io.reactivex.Observable;

public interface MessageObservable {
    Observable<byte[]> getMessageObservable();
}
