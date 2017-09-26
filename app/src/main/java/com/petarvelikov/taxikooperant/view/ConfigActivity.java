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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.petarvelikov.taxikooperant.R;
import com.petarvelikov.taxikooperant.application.App;

import javax.inject.Inject;

public class ConfigActivity extends AppCompatActivity {

    private static final String USER_ID = "user_id";
    private static final int LOCATION_PERMISSIONS_CODE = 10001;

    @Inject
    SharedPreferences sharedPreferences;
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        App app = (App) getApplication();
        app.component().inject(this);
        button = (Button) findViewById(R.id.btnMain);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
        requestPermissions();
        if (sharedPreferences.contains(USER_ID)) {
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
                        button.setEnabled(false);
                    } else {
                        button.setEnabled(true);
                    }
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
