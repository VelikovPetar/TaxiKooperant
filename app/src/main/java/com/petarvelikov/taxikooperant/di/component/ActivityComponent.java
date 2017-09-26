package com.petarvelikov.taxikooperant.di.component;


import com.petarvelikov.taxikooperant.di.module.ActivityModule;
import com.petarvelikov.taxikooperant.di.scope.ActivityScope;
import com.petarvelikov.taxikooperant.view.ConfigActivity;
import com.petarvelikov.taxikooperant.view.MainActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class})
public interface ActivityComponent {
    void inject(MainActivity activity);

    void inject(ConfigActivity activity);
}
