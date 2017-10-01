package com.petarvelikov.taxikooperant.view_model;

import android.util.Log;

import com.petarvelikov.taxikooperant.model.status.StatusModel;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class StatusViewModel {

    private ObservableStatusModel observableStatusModel;
    private BehaviorSubject<StatusModel> statusSubject;
    private Disposable disposable;

    public StatusViewModel(ObservableStatusModel observableStatusModel) {
        Log.d("Main", "SVM created");
        statusSubject = BehaviorSubject.create();
        this.observableStatusModel = observableStatusModel;
        this.observableStatusModel.getObservableStatusModel()
                .subscribe(new Observer<StatusModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull StatusModel statusModel) {
                        Log.d("Main", "SVM OnNext");
                        statusSubject.onNext(statusModel);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Main", "SVM OnError");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Observable<StatusModel> getStatusObservable() {
        return statusSubject;
    }

    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ObservableStatusModel {
        Observable<StatusModel> getObservableStatusModel();
        void dispose();
    }
}
