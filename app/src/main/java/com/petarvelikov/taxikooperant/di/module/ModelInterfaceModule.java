package com.petarvelikov.taxikooperant.di.module;

import com.petarvelikov.taxikooperant.model.interfaces.ConnectionStatusObservable;
import com.petarvelikov.taxikooperant.model.interfaces.LocationStatusObservable;
import com.petarvelikov.taxikooperant.model.interfaces.MessageObservable;
import com.petarvelikov.taxikooperant.model.interfaces.MessageWriter;
import com.petarvelikov.taxikooperant.model.location.LocationUpdater;
import com.petarvelikov.taxikooperant.model.tcp.TcpClient;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ModelInterfaceModule {

    @Binds
    public abstract ConnectionStatusObservable provideConnectionStatusObservable(TcpClient tcpClient);

    @Binds
    public abstract MessageObservable provideMessageObservable(TcpClient tcpClient);

    @Binds
    public abstract MessageWriter provideMessageWriter(TcpClient tcpClient);

    @Binds
    public abstract LocationStatusObservable provideLocationStatusObservable(LocationUpdater updater);
}
