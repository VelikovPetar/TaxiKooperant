package com.petarvelikov.taxikooperant.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.petarvelikov.taxikooperant.R;
import com.petarvelikov.taxikooperant.application.App;
import com.petarvelikov.taxikooperant.di.component.DaggerActivityComponent;
import com.petarvelikov.taxikooperant.model.tcp.TcpService;

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
    }

    private void bindUi() {
        Button startButton = (Button) findViewById(R.id.btnStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService(new Intent(MainActivity.this, TcpService.class), tcpServiceConnection, Context.BIND_AUTO_CREATE);
            }
        });
        Button stopButton = (Button) findViewById(R.id.btnStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(tcpServiceConnection);
            }
        });
    }
}
