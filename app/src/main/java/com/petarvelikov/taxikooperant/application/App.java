package com.petarvelikov.taxikooperant.application;

import android.app.Application;

import com.petarvelikov.taxikooperant.di.component.AppComponent;
import com.petarvelikov.taxikooperant.di.component.DaggerAppComponent;
import com.petarvelikov.taxikooperant.di.module.AppModule;

public class App extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent component() {
        return component;
    }
}
