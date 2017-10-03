package com.petarvelikov.taxikooperant.model.sound_manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.petarvelikov.taxikooperant.R;
import com.petarvelikov.taxikooperant.constants.Constants;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

@Singleton
public class SoundManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SoundPool soundPool;
    private int ringId;
    private boolean canPlay;
    private volatile int streamId = -1;
    private Disposable disposable;

    @Inject
    public SoundManager(Context context, SharedPreferences sharedPreferences, SoundPool soundPool) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.soundPool = soundPool;
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                canPlay = true;
            }
        });
        ringId = soundPool.load(this.context, R.raw.beep_trimmed, 1);
    }

    public void playSound(int seconds) {
        if (canPlay && streamId == -1) {
            setVolume();
            streamId = soundPool.play(ringId, 1, 1, 1, -1, 1);
            disposable = Observable.timer(seconds, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            stopSound();
                        }
                    });
        }
    }

    public void stopSound() {
        if (canPlay && streamId != -1) {
            soundPool.stop(streamId);
            streamId = -1;
        }
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void setVolume() {
        float volumePercentage = sharedPreferences.getFloat(Constants.VOLUME, Constants.DEFAULT_VOLUME);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = volumePercentage * maxVolume;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) volume, 0);
    }
}
