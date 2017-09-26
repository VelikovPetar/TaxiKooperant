package com.petarvelikov.taxikooperant.di.module;

import com.petarvelikov.taxikooperant.model.reader.TcpMessageReader;
import com.petarvelikov.taxikooperant.model.status.StatusUpdater;
import com.petarvelikov.taxikooperant.view_model.MessageViewModel;
import com.petarvelikov.taxikooperant.view_model.StatusViewModel;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelInterfaceModule {

    @Binds
    public abstract MessageViewModel.ObservableMessageModel provideObservableMessageModel(TcpMessageReader reader);

    @Binds
    public abstract StatusViewModel.ObservableStatusModel provideObservableStatusModel(StatusUpdater updater);
}
