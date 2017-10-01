package com.petarvelikov.taxikooperant.model.interfaces;

import android.location.Location;

import io.reactivex.Observable;

public interface LocationObservable {
    Observable<Location> getLocationObservable();
}
