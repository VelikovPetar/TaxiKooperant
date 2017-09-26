package com.petarvelikov.taxikooperant.di.component;

import android.content.SharedPreferences;

import com.petarvelikov.taxikooperant.di.module.AppModule;
import com.petarvelikov.taxikooperant.di.module.ModelInterfaceModule;
import com.petarvelikov.taxikooperant.di.module.SystemComponentsModule;
import com.petarvelikov.taxikooperant.model.interfaces.MessageObservable;
import com.petarvelikov.taxikooperant.model.interfaces.NetworkStatusObservable;
import com.petarvelikov.taxikooperant.model.reader.TcpMessageReader;
import com.petarvelikov.taxikooperant.model.status.StatusUpdater;
import com.petarvelikov.taxikooperant.model.tcp.TcpService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ModelInterfaceModule.class, SystemComponentsModule.class})
public interface AppComponent {

    void inject(TcpService service);

    TcpMessageReader tcpMessageReader();

    StatusUpdater statusUpdater();

    SharedPreferences sharedPreferences();
}
