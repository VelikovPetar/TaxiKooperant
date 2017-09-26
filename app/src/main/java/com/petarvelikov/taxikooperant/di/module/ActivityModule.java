package com.petarvelikov.taxikooperant.di.module;

import com.petarvelikov.taxikooperant.di.scope.ActivityScope;
import com.petarvelikov.taxikooperant.view_model.MessageViewModel;
import com.petarvelikov.taxikooperant.view_model.StatusViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    @Provides
    @ActivityScope
    public MessageViewModel provideMessageViewModel(MessageViewModel.ObservableMessageModel observable) {
        return new MessageViewModel(observable);
    }

    @Provides
    @ActivityScope
    public StatusViewModel provideStatusViewModel(StatusViewModel.ObservableStatusModel observable) {
        return new StatusViewModel(observable);
    }
}
