package com.petarvelikov.taxikooperant.model.tcp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

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
    @Inject
    TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;

    private Disposable disposable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App app = (App) getApplication();
        app.component().inject(this);
        registerPhoneStateListener();
        startWork();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopWork();
        unregisterPhoneStateListener();
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
                soundManager.playSound(7);
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
        return new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_in_background))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
//                .addAction(R.drawable.ic_exit, getString(R.string.exit), exitPendingIntent)
                .addAction(new NotificationCompat.Action.Builder(R.drawable.ic_action_exit,
                        getString(R.string.exit), exitPendingIntent).build())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
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

    private void registerPhoneStateListener() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING ||
                        state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    soundManager.stopSound();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void unregisterPhoneStateListener() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

}
