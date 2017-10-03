package com.petarvelikov.taxikooperant.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.petarvelikov.taxikooperant.R;
import com.petarvelikov.taxikooperant.application.App;
import com.petarvelikov.taxikooperant.constants.Constants;
import com.petarvelikov.taxikooperant.di.component.DaggerActivityComponent;
import com.petarvelikov.taxikooperant.model.settings.SettingsController;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSIONS_CODE = 10001;

    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    SettingsController settingsController;

    private Button confirmButton;
    private EditText deviceIdEditText, intervalEditText;
    private SeekBar volumeSeekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        App app = (App) getApplication();
        DaggerActivityComponent.builder()
                .appComponent(app.component())
                .build()
                .inject(this);
        bindUi();
        requestPermissions();
        if (settingsController.hasUserId()) {
            goToMainActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSIONS_CODE:
                if (grantResults.length > 1) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Please give permission to use fine location!", Toast.LENGTH_LONG).show();
                        confirmButton.setEnabled(false);
                    } else {
                        confirmButton.setEnabled(true);
                    }
                }
        }
    }

    private void bindUi() {
        setupDeviceIdEditText();
        setupIntervalEditText();
        setupVolumeSeekBar();
        confirmButton = (Button) findViewById(R.id.btnConfirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }

    private void setupDeviceIdEditText() {
        deviceIdEditText = (EditText) findViewById(R.id.editDeviceId);
    }

    private void setupIntervalEditText() {
        intervalEditText = (EditText) findViewById(R.id.editInterval);
        intervalEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    confirm();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupVolumeSeekBar() {
        volumeSeekBar = (SeekBar) findViewById(R.id.seekBarVolume);
        volumeSeekBar.setProgress(settingsController.getVolume());
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int value = seekBar.getProgress();
                settingsController.setVolume(value);
            }
        });
    }

    private boolean validateFields() {
        String text = deviceIdEditText.getText().toString().trim();
        if (text.equals("") && !settingsController.hasUserId()) {
            deviceIdEditText.setError(getString(R.string.error_empty));
            return false;
        }
        return true;
    }

    private void confirm() {
        if (validateFields()) {
            deviceIdEditText.setError(null);
            String text = deviceIdEditText.getText().toString().trim();
            settingsController.setUserId(text);
            try {
                text = intervalEditText.getText().toString().trim();
                if (!text.equals("")) {
                    int seconds = Integer.parseInt(text);
                    settingsController.setInterval(seconds);
                }
                intervalEditText.setError(null);
                goToMainActivity();
            } catch (Exception e) {
                intervalEditText.setError(getString(R.string.error_not_numeric));
            }
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestPermissions() {
        int coarseGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (coarseGranted != PackageManager.PERMISSION_GRANTED ||
                fineGranted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSIONS_CODE);
        }
    }
}
