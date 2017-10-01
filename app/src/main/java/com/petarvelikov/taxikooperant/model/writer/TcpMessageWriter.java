package com.petarvelikov.taxikooperant.model.writer;

import android.location.Location;
import android.util.Log;

import com.petarvelikov.taxikooperant.model.interfaces.MessageWriter;
import com.petarvelikov.taxikooperant.model.location.LocationUpdater;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class TcpMessageWriter {

    private MessageWriter messageWriter;
    private IntervalGenerator intervalGenerator;
    private LocationUpdater locationUpdater;
    private Disposable intervalDisposable, locationDisposable;
    private MessagesGenerator messagesGenerator;
    private Location location;

    @Inject
    public TcpMessageWriter(MessageWriter messageWriter, IntervalGenerator intervalGenerator,
                            LocationUpdater locationUpdater, MessagesGenerator messagesGenerator) {
        this.messageWriter = messageWriter;
        this.intervalGenerator = intervalGenerator;
        this.locationUpdater = locationUpdater;
        this.messagesGenerator = messagesGenerator;
    }

    public void startSendingUpdates() {
        locationUpdater.startListeningLocationChanges();
        subscribeToLocationUpdates();
        subscribeToIntervalUpdates();
    }

    public void stopSendingUpdates() {
        dispose();
        locationUpdater.stopListeningLocationChanges();
    }

    private void subscribeToLocationUpdates() {
        locationUpdater.getLocationObservable()
                .subscribe(new Observer<Location>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        locationDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Location location) {
                        TcpMessageWriter.this.location = location;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void subscribeToIntervalUpdates() {
        intervalGenerator.asObservable()
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        intervalDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        writeLocationUpdate();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void writeLocationUpdate() {
        if (location != null) {
            byte[] message = messagesGenerator.commonMessage(location);
            String msg = "";
            for (byte b : message) msg += (char) b;
            Log.d("Messages", msg);
//            messageWriter.writeMessage(message);
        } else {
            Log.d("Messages", "Null location");
        }
    }

    private void dispose() {
        if (locationDisposable != null && !locationDisposable.isDisposed()) {
            locationDisposable.dispose();
        }
        if (intervalDisposable != null && !intervalDisposable.isDisposed()) {
            intervalDisposable.dispose();
        }
    }
}
