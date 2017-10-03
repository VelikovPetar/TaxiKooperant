package com.petarvelikov.taxikooperant.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.petarvelikov.taxikooperant.R;
import com.petarvelikov.taxikooperant.application.App;
import com.petarvelikov.taxikooperant.constants.Constants;
import com.petarvelikov.taxikooperant.di.component.DaggerActivityComponent;
import com.petarvelikov.taxikooperant.model.status.StatusModel;
import com.petarvelikov.taxikooperant.model.tcp.TcpService;
import com.petarvelikov.taxikooperant.view_model.MessageViewModel;
import com.petarvelikov.taxikooperant.view_model.StatusViewModel;

import org.w3c.dom.Text;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @Inject
    MessageViewModel messageViewModel;
    @Inject
    StatusViewModel statusViewModel;
    private Disposable messagesDisposable;
    private Disposable statusDisposable;


    private TextView textViewConnection, textViewServer, textViewLocation,
            textViewExit, textViewSettings;
    private Button buttonStop;
    private boolean shouldGoForeground = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App app = (App) getApplication();
        DaggerActivityComponent.builder()
                .appComponent(app.component())
                .build()
                .inject(this);
        bindUi();
        if (getIntent().hasExtra(Constants.SHOULD_STOP_FOREGROUND)) {
            goBackground();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(Constants.SHOULD_STOP_FOREGROUND)) {
            goBackground();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseViewModels();
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribeForStatusUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unsubscribeForStatusUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        goBackground();
        shouldGoForeground = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        goForeground();
        Log.d("LIFE", "OnStop Main");
    }

    private void bindUi() {
        Button startButton = (Button) findViewById(R.id.btnStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TcpService.class);
                intent.setAction(Constants.ACTION.START_RINGING);
                startService(intent);
            }
        });
        setupExitButton();
        setupSettingsButton();
        setupStopButton();
        textViewConnection = (TextView) findViewById(R.id.txtConnectionStatus);
        textViewServer = (TextView) findViewById(R.id.txtServerStatus);
        textViewLocation = (TextView) findViewById(R.id.txtLocationStatus);
    }

    private void setupExitButton() {
        textViewExit = (TextView) findViewById(R.id.txtExit);
        textViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupSettingsButton() {
        textViewSettings = (TextView) findViewById(R.id.txtSettings);
        textViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettingsActivity();
            }
        });
    }

    private void setupStopButton() {
        buttonStop = (Button) findViewById(R.id.btnStopRinging);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TcpService.class);
                intent.setAction(Constants.ACTION.STOP_RINGING);
                startService(intent);
            }
        });
    }

    private void subscribeForStatusUpdates() {
        statusViewModel.getStatusObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StatusModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        statusDisposable = d;
                        Log.d("Main", "Rx Subscribe");
                    }

                    @Override
                    public void onNext(@NonNull StatusModel status) {
                        Log.d("Main", "Rx Next");
                        updateStatus(status);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Main", "Rx Error");
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Main", "Rx Complete");
                    }
                });
    }

    private void unsubscribeForStatusUpdates() {
        if (statusDisposable != null && !statusDisposable.isDisposed()) {
            statusDisposable.dispose();
            Log.d("Main", "Rx Dispose");
        }
    }

    private void updateStatus(StatusModel status) {
        switch (status.getNetworkStatus()) {
            case StatusModel.NOT_CONNECTED:
                textViewConnection.setText(R.string.status_no_connection);
                textViewConnection.setTextColor(getResources().getColor(R.color.colorRed));
                break;
            case StatusModel.CONNECTING:
                textViewConnection.setText(R.string.status_connecting);
                textViewConnection.setTextColor(getResources().getColor(R.color.colorYellow));
                break;
            case StatusModel.CONNECTED:
                textViewConnection.setText(R.string.status_connected);
                textViewConnection.setTextColor(getResources().getColor(R.color.colorGreen));
                break;
        }
        switch (status.getServerStatus()) {
            case StatusModel.NOT_CONNECTED:
                textViewServer.setText(R.string.status_no_connection);
                textViewServer.setTextColor(getResources().getColor(R.color.colorRed));
                break;
            case StatusModel.CONNECTING:
                textViewServer.setText(R.string.status_connecting);
                textViewServer.setTextColor(getResources().getColor(R.color.colorYellow));
                break;
            case StatusModel.CONNECTED:
                textViewServer.setText(R.string.status_connected);
                textViewServer.setTextColor(getResources().getColor(R.color.colorGreen));
                break;
        }
        switch (status.getLocationServiceStatus()) {
            case StatusModel.NO_LOCATION_SERVICE:
                textViewLocation.setText(R.string.no_location);
                textViewLocation.setTextColor(getResources().getColor(R.color.colorRed));
                break;
            case StatusModel.NETWORK:
                textViewLocation.setText(R.string.network);
                textViewLocation.setTextColor(getResources().getColor(R.color.colorYellow));
                break;
            case StatusModel.GPS:
                textViewLocation.setText(R.string.gps);
                textViewLocation.setTextColor(getResources().getColor(R.color.colorGreen));
                break;
        }
    }

    private void subscribeForMessageUpdates() {
        // TODO
    }

    private void unsubscribeForMessageUpdates() {
        // TODO
    }

    private void goForeground() {
        if (!shouldGoForeground) {
            return;
        }
        Log.d("Main", "Going foreground");
        Intent intent = new Intent(this, TcpService.class);
        intent.setAction(Constants.ACTION.START_FOREGROUND);
        startService(intent);

    }

    private void goBackground() {
        Log.d("Main", "Going background");
        Intent intent = new Intent(this, TcpService.class);
        intent.setAction(Constants.ACTION.STOP_FOREGROUND);
        startService(intent);

    }

    private void goToSettingsActivity() {
        shouldGoForeground = false;
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(Constants.IS_COMING_FROM_MAIN_ACTIVITY, true);
        startActivity(intent);
    }

    private void releaseViewModels() {
        statusViewModel.dispose();
        statusViewModel = null;
        messageViewModel.dispose();
        messageViewModel = null;
    }
}
