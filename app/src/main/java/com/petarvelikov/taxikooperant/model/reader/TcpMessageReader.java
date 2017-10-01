package com.petarvelikov.taxikooperant.model.reader;

import android.util.Log;

import com.petarvelikov.taxikooperant.model.interfaces.MessageObservable;
import com.petarvelikov.taxikooperant.model.messages.AbstractMessage;
import com.petarvelikov.taxikooperant.model.messages.ErrorMessage;
import com.petarvelikov.taxikooperant.model.messages.RingBellMessage;
import com.petarvelikov.taxikooperant.view_model.MessageViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

@Singleton
public class TcpMessageReader implements MessageViewModel.ObservableMessageModel {

    private MessageObservable messageObservable;
    private MessageParser parser;
    private Disposable disposable;
    private PublishSubject<AbstractMessage> messageSubject;

    @Inject
    public TcpMessageReader(MessageObservable messageObservable, MessageParser parser) {
        this.messageObservable = messageObservable;
        this.parser = parser;
        this.messageSubject = PublishSubject.create();
        Log.d("Injection", "A reader is created");
    }

    @Override
    public Observable<AbstractMessage> getObservableModel() {
        return messageSubject;
    }

    public void startListeningForMessages() {
        messageObservable.getMessageObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<byte[], AbstractMessage>() {
                    @Override
                    public AbstractMessage apply(@NonNull byte[] message) throws Exception {
                        Log.d("Reader", "Mapping bytes to Message");
                        return parser.parse(message);
                    }
                })
                .subscribe(new Observer<AbstractMessage>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull AbstractMessage abstractMessage) {
                        if (abstractMessage instanceof ErrorMessage) {
                            ErrorMessage e = (ErrorMessage) abstractMessage;
                            Log.d("Reader", e.getMessage());
                        } else if (abstractMessage instanceof RingBellMessage) {
                            RingBellMessage m = (RingBellMessage) abstractMessage;
                            Log.d("Reader", "Seconds " + m.getSeconds());
                        } else {
                            Log.d("Reader", "Error");
                        }
                        messageSubject.onNext(abstractMessage);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Reader", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void stopListeningForMessages() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void dispose() {
        stopListeningForMessages();
    }
}
