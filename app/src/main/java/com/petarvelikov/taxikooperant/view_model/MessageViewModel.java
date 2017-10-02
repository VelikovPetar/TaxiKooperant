package com.petarvelikov.taxikooperant.view_model;

import com.petarvelikov.taxikooperant.model.messages.AbstractMessage;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class MessageViewModel {

    private ObservableMessageModel observableMessageModel;
    private PublishSubject<AbstractMessage> messageSubject;
    private Disposable disposable;

    public MessageViewModel(ObservableMessageModel observableMessageModel) {
        messageSubject = PublishSubject.create();
        this.observableMessageModel = observableMessageModel;
        this.observableMessageModel.getObservableModel()
                .subscribe(new Observer<AbstractMessage>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull AbstractMessage abstractMessage) {
                        messageSubject.onNext(abstractMessage);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<AbstractMessage> getMessageObservable() {
        return messageSubject;
    }

    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ObservableMessageModel {
        Observable<AbstractMessage> getObservableModel();

        void dispose();
    }
}
