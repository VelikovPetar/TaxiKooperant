package com.petarvelikov.taxikooperant.model.settings;

import android.content.SharedPreferences;
import android.util.Log;

import com.petarvelikov.taxikooperant.constants.Constants;
import com.petarvelikov.taxikooperant.di.scope.ActivityScope;

import javax.inject.Inject;

@ActivityScope
public class SettingsController {

    private SharedPreferences sharedPreferences;

    @Inject
    public SettingsController(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean hasUserId() {
//        return sharedPreferences.contains(Constants.USER_ID);
        return false;
    }

    public String getUserId() {
        return sharedPreferences.getString(Constants.USER_ID, null);
    }

    public void setUserId(String userId) {
        if (userId == null || userId.equals("")) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.USER_ID, userId);
        editor.apply();
    }

    public void setInterval(int seconds) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.INTERVAL, seconds);
        editor.apply();
    }

    public int getVolume() {
        int volume = (int) (sharedPreferences.getFloat(Constants.VOLUME, Constants.DEFAULT_VOLUME) * 100);
        Log.d("VOLUME", volume + "");
        return (int) (sharedPreferences.getFloat(Constants.VOLUME, Constants.DEFAULT_VOLUME) * 100);
    }

    public void setVolume(int intValue) {
        float floatValue = (float) intValue / 100.0f;
        Log.d("VOLUME", floatValue + "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(Constants.VOLUME, floatValue);
        editor.apply();
    }
}
