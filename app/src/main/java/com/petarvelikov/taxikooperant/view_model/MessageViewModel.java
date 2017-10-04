package com.petarvelikov.taxikooperant.view_model;

import com.petarvelikov.taxikooperant.model.messages.AbstractMessage;
import com.petarvelikov.taxikooperant.model.messages.ClearMessage;
import com.petarvelikov.taxikooperant.model.messages.RingBellMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

public class MessageViewModel {

    private ObservableMessageModel observableMessageModel;
    private BehaviorSubject<AbstractMessage> messageSubject;
    private Disposable disposable, timerDisposable;
    private volatile AbstractMessage message = new ClearMessage();

    public MessageViewModel(ObservableMessageModel observableMessageModel) {
        messageSubject = BehaviorSubject.create();
        this.observableMessageModel = observableMessageModel;
        refresh();
    }

    // Gets new instance of the observable model,
    // which is recreated every time the TcpMessageReader stops listening (the service is destroyed)
    public void refresh() {
        dispose();
        observableMessageModel.getObservableModel()
                .subscribe(new Observer<AbstractMessage>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull AbstractMessage abstractMessage) {
                        message = abstractMessage;
                        messageSubject.onNext(message);
                        final RingBellMessage rbMessage = (RingBellMessage) abstractMessage;
                        long delay = calculateRemainingTime(rbMessage);
                        timerDisposable = Observable.timer(delay, TimeUnit.SECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        message = new ClearMessage();
                                        messageSubject.onNext(message);
                                    }
                                });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void markAsRead() {
        if (timerDisposable != null && !timerDisposable.isDisposed()) {
            timerDisposable.dispose();
        }
        message = new ClearMessage();
        messageSubject.onNext(message);
    }

    public Observable<AbstractMessage> getMessageObservable() {
        return messageSubject;
    }

    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (timerDisposable != null && !timerDisposable.isDisposed()) {
            timerDisposable.dispose();
        }
    }

    private long calculateRemainingTime(RingBellMessage message) {
        long currentTime = System.currentTimeMillis();
        long secondsPassed = (currentTime - message.getTimestamp()) / 1000;
        long delay = message.getSeconds() - secondsPassed;
        return Math.max(0, delay);
    }

    public interface ObservableMessageModel {
        Observable<AbstractMessage> getObservableModel();

        void dispose();
    }
}
