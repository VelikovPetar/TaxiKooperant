package com.petarvelikov.taxikooperant.model.tcp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.petarvelikov.taxikooperant.R;
import com.petarvelikov.taxikooperant.application.App;
import com.petarvelikov.taxikooperant.constants.Constants;
import com.petarvelikov.taxikooperant.model.reader.TcpMessageReader;
import com.petarvelikov.taxikooperant.model.writer.TcpMessageWriter;
import com.petarvelikov.taxikooperant.view.MainActivity;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case Constants.ACTION.START_FOREGROUND:
                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.putExtra(Constants.SHOULD_STOP_FOREGROUND, true);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string.app_in_background))
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();
                startForeground(1001, notification);
                break;

            case Constants.ACTION.STOP_FOREGROUND:
                stopForeground(true);
        }
        return START_STICKY;
    }

    public void startWork() {
        new Thread(tcpClient)
                .start();
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
