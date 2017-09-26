package com.petarvelikov.taxikooperant.model.interfaces;

import io.reactivex.Observable;

public interface LocationStatusObservable {
    Observable<Integer> getLocationStatusObservable();
}
