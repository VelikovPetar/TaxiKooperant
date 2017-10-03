package com.petarvelikov.taxikooperant.model.status;

import com.petarvelikov.taxikooperant.model.interfaces.ConnectionStatusObservable;
import com.petarvelikov.taxikooperant.model.interfaces.LocationStatusObservable;
import com.petarvelikov.taxikooperant.view_model.StatusViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class StatusUpdater implements StatusViewModel.ObservableStatusModel {

    private ConnectionStatusObservable connectionStatusObservable;
    private LocationStatusObservable locationStatusObservable;
    private BehaviorSubject<StatusModel> statusSubject;
    private Disposable serverDisposable, networkDisposable, locationDisposable;

    private StatusModel status = new StatusModel(
            StatusModel.NOT_CONNECTED,
            StatusModel.NO_LOCATION_SERVICE,
            StatusModel.NOT_CONNECTED
    );

    @Inject
    public StatusUpdater(ConnectionStatusObservable connectionStatusObservable,
                         LocationStatusObservable locationStatusObservable) {
        this.connectionStatusObservable = connectionStatusObservable;
        this.locationStatusObservable = locationStatusObservable;
        this.statusSubject = BehaviorSubject.createDefault(status);
        observe();
    }

    @Override
    public Observable<StatusModel> getObservableStatusModel() {
        return statusSubject;
    }

    private void observe() {
        connectionStatusObservable.getNetworkStatusObservable()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        networkDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Integer nStatus) {
                        status.setNetworkStatus(nStatus);
                        statusSubject.onNext(status);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        connectionStatusObservable.getServerStatusObservable()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        serverDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Integer sStatus) {
                        status.setServerStatus(sStatus);
                        statusSubject.onNext(status);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        locationStatusObservable.getLocationStatusObservable()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        locationDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Integer lStatus) {
                        status.setLocationServiceStatus(lStatus);
                        statusSubject.onNext(status);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void dispose() {
        dispose(serverDisposable);
        dispose(networkDisposable);
        dispose(locationDisposable);
    }

    private void dispose(Disposable d) {
        if (d != null && !d.isDisposed()) {
            d.dispose();
        }
    }
}
