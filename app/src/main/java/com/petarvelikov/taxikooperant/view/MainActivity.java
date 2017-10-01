package com.petarvelikov.taxikooperant.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private TcpService tcpService;
    private ServiceConnection tcpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TcpService.TcpServiceBinder binder = (TcpService.TcpServiceBinder) service;
            tcpService = binder.getTcpService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private boolean isServiceStarted;

    @Inject
    MessageViewModel messageViewModel;
    @Inject
    StatusViewModel statusViewModel;
    private Disposable messagesDisposable;
    private Disposable statusDisposable;

    private TextView tv;

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
        Log.d("Main", "On create");
        if (getIntent().hasExtra(Constants.SHOULD_STOP_FOREGROUND)) {
            isServiceStarted = true;
            goBackground();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(Constants.SHOULD_STOP_FOREGROUND)) {
            isServiceStarted = true;
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        goForeground();
    }

    private void bindUi() {
        Button startButton = (Button) findViewById(R.id.btnStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TcpService.class);
                intent.setAction(Constants.ACTION.START_SERVICE);
                startService(intent);
                isServiceStarted = true;
            }
        });
        Button stopButton = (Button) findViewById(R.id.btnStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TcpService.class);
                intent.setAction(Constants.ACTION.STOP_SERVICE);
                stopService(intent);
                isServiceStarted = false;
            }
        });
        tv = (TextView) findViewById(R.id.txtTest);
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
                    public void onNext(@NonNull StatusModel statusModel) {
                        tv.setText(statusModel.toString());
                        Log.d("Main", "Rx Next");
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

    private void subscribeForMessageUpdates() {
        // TODO
    }

    private void unsubscribeForMessageUpdates() {
        // TODO
    }

    private void goForeground() {
        if (isServiceStarted) {
            Log.d("Main", "Going foreground");
            Intent intent = new Intent(this, TcpService.class);
            intent.setAction(Constants.ACTION.START_FOREGROUND);
            startService(intent);
        }
    }

    private void goBackground() {
        if (isServiceStarted) {
            Log.d("Main", "Going background");
            Intent intent = new Intent(this, TcpService.class);
            intent.setAction(Constants.ACTION.STOP_FOREGROUND);
            startService(intent);
        }
    }

    private void releaseViewModels() {
        statusViewModel.dispose();
        statusViewModel = null;
        messageViewModel.dispose();
        messageViewModel = null;
    }
}
