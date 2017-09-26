package com.petarvelikov.taxikooperant.model.tcp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.petarvelikov.taxikooperant.application.App;
import com.petarvelikov.taxikooperant.model.reader.TcpMessageReader;
import com.petarvelikov.taxikooperant.model.writer.TcpMessageWriter;

import javax.inject.Inject;


public class TcpService extends Service {

    @Inject
    TcpClient tcpClient;
    @Inject
    TcpMessageWriter tcpMessageWriter;
    @Inject
    TcpMessageReader tcpMessageReader;

    private final Binder binder = new TcpServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App app = (App) getApplication();
        app.component().inject(this);
        startWork();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopWork();
    }

    public void startWork() {
//        new Thread(tcpClient)
//                .start();
        tcpMessageWriter.startSendingUpdates();
        tcpMessageReader.startListeningForMessages();
    }

    public void stopWork() {
        tcpMessageReader.stopListeningForMessages();
        tcpMessageWriter.stopSendingUpdates();
        if (tcpClient != null) {
            tcpClient.stop();
            tcpClient = null;
        }
    }

    public class TcpServiceBinder extends Binder {
        public TcpService getTcpService() {
            return TcpService.this;
        }
    }
}
