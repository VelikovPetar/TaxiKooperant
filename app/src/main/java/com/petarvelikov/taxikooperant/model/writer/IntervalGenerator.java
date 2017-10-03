package com.petarvelikov.taxikooperant.model.writer;

import android.content.SharedPreferences;

import com.petarvelikov.taxikooperant.constants.Constants;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class IntervalGenerator {

    private Observable<Long> emitter;
    private SharedPreferences sharedPreferences;

    @Inject
    public IntervalGenerator(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        int interval = this.sharedPreferences.getInt(Constants.INTERVAL, Constants.DEFAULT_INTERVAL);
        emitter = Observable.interval(0, interval, TimeUnit.SECONDS);
    }

    public Observable<Long> asObservable() {
        return emitter;
    }
}
