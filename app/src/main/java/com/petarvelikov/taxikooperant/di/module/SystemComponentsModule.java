package com.petarvelikov.taxikooperant.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SystemComponentsModule {

    @Provides
    @Singleton
    public Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    public LocationManager provideLocationManager(Application application) {
        return (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    public SoundPool provideSoundPool() {
        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= 21) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        return soundPool;
    }

    @Provides
    @Singleton
    public TelephonyManager provideTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }
}
