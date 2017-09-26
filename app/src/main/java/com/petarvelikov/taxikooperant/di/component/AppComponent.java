package com.petarvelikov.taxikooperant.di.component;

import android.content.SharedPreferences;

import com.petarvelikov.taxikooperant.di.module.AppModule;
import com.petarvelikov.taxikooperant.di.module.InterfaceModule;
import com.petarvelikov.taxikooperant.di.module.SystemComponentsModule;
import com.petarvelikov.taxikooperant.model.interfaces.MessageObservable;
import com.petarvelikov.taxikooperant.model.interfaces.StatusUpdateObservable;
import com.petarvelikov.taxikooperant.model.tcp.TcpService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, InterfaceModule.class, SystemComponentsModule.class})
public interface AppComponent {

    void inject(TcpService service);

    MessageObservable messageObservable();

    StatusUpdateObservable statusUpdateObservable();

    SharedPreferences sharedPreferences();
}
