package com.petarvelikov.taxikooperant.di.component;

import com.petarvelikov.taxikooperant.di.module.AppModule;
import com.petarvelikov.taxikooperant.di.module.InterfaceModule;
import com.petarvelikov.taxikooperant.di.module.SystemComponentsModule;
import com.petarvelikov.taxikooperant.model.tcp.TcpService;
import com.petarvelikov.taxikooperant.view.ConfigActivity;
import com.petarvelikov.taxikooperant.view.MainActivity;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, InterfaceModule.class, SystemComponentsModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(ConfigActivity activity);
    void inject(TcpService service);
}
