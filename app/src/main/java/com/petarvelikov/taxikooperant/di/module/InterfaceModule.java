package com.petarvelikov.taxikooperant.di.module;

import com.petarvelikov.taxikooperant.model.interfaces.MessageObservable;
import com.petarvelikov.taxikooperant.model.interfaces.MessageWriter;
import com.petarvelikov.taxikooperant.model.interfaces.StatusUpdateObservable;
import com.petarvelikov.taxikooperant.model.tcp.TcpClient;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class InterfaceModule {

    @Binds
    public abstract StatusUpdateObservable provideStatusUpdatesObservable(TcpClient tcpClient);

    @Binds
    public abstract MessageObservable provideMessageObservable(TcpClient tcpClient);

    @Binds
    public abstract MessageWriter provideMessageWriter(TcpClient tcpClient);
}
