package com.petarvelikov.taxikooperant.di.module;

import com.petarvelikov.taxikooperant.di.scope.ActivityScope;
import com.petarvelikov.taxikooperant.model.interfaces.MessageObservable;
import com.petarvelikov.taxikooperant.model.interfaces.StatusUpdateObservable;
import com.petarvelikov.taxikooperant.view_model.MessageViewModel;
import com.petarvelikov.taxikooperant.view_model.StatusViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    @Provides
    @ActivityScope
    public MessageViewModel provideMessageViewModel(MessageObservable messageObservable) {
        return new MessageViewModel(messageObservable);
    }

    @Provides
    @ActivityScope
    public StatusViewModel provideStatusViewModel(StatusUpdateObservable statusUpdateObservable) {
        return new StatusViewModel(statusUpdateObservable);
    }
}
