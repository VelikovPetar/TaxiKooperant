package com.petarvelikov.taxikooperant.model.tcp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.petarvelikov.taxikooperant.R;
import com.petarvelikov.taxikooperant.application.App;
import com.petarvelikov.taxikooperant.constants.Constants;
import com.petarvelikov.taxikooperant.model.messages.AbstractMessage;
import com.petarvelikov.taxikooperant.model.messages.RingBellMessage;
import com.petarvelikov.taxikooperant.model.reader.TcpMessageReader;
import com.petarvelikov.taxikooperant.model.sound_manager.SoundManager;
import com.petarvelikov.taxikooperant.model.writer.TcpMessageWriter;
import com.petarvelikov.taxikooperant.view.MainActivity;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


public class TcpService extends Service {

    private static final int NOTIFICATION_ID = 1001;

    @Inject
    TcpClient tcpClient;
    @Inject
    TcpMessageWriter tcpMessageWriter;
    @Inject
    TcpMessageReader tcpMessageReader;
    @Inject
    SoundManager soundManager;

    private Disposable disposable;

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
                Notification notification = buildNotification();
                startForeground(NOTIFICATION_ID, notification);
                listenForMessages();
                break;
            case Constants.ACTION.STOP_FOREGROUND:
                stopForeground(true);
                stopListeningForMessages();
                break;
            case Constants.ACTION.STOP_SERVICE:
                stopForeground(true);
                stopWork();
                stopSelf();
                break;
            case Constants.ACTION.STOP_RINGING:
                soundManager.stopSound();
                break;
            // TODO Remove this
            case Constants.ACTION.START_RINGING:
                soundManager.playSound(10);
                break;
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
        soundManager.stopSound();
        if (tcpClient != null) {
            tcpClient.stop();
            tcpClient = null;
        }
    }

    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.SHOULD_STOP_FOREGROUND, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent exitIntent = new Intent(this, TcpService.class);
        exitIntent.setAction(Constants.ACTION.STOP_SERVICE);
        PendingIntent exitPendingIntent = PendingIntent.getService(this, 0, exitIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_in_background))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
//                .addAction(R.drawable.ic_exit, getString(R.string.exit), exitPendingIntent)
                .addAction(new NotificationCompat.Action.Builder(R.drawable.ic_action_exit,
                        getString(R.string.exit), exitPendingIntent).build())
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        return notification;
    }

    private void listenForMessages() {
        tcpMessageReader.getObservableModel()
                .subscribe(new Observer<AbstractMessage>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull AbstractMessage abstractMessage) {
                        if (abstractMessage instanceof RingBellMessage) {
                            RingBellMessage message = (RingBellMessage) abstractMessage;
                            soundManager.playSound(message.getSeconds());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void stopListeningForMessages() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public class TcpServiceBinder extends Binder {
        public TcpService getTcpService() {
            return TcpService.this;
        }
    }
}
