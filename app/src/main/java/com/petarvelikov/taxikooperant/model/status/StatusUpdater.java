package com.petarvelikov.taxikooperant.model.status;

import com.petarvelikov.taxikooperant.model.interfaces.LocationStatusObservable;
import com.petarvelikov.taxikooperant.model.interfaces.NetworkStatusObservable;
import com.petarvelikov.taxikooperant.view_model.StatusViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class StatusUpdater implements StatusViewModel.ObservableStatusModel {

    private NetworkStatusObservable networkStatusObservable;
    private LocationStatusObservable locationStatusObservable;
    private BehaviorSubject<StatusModel> statusSubject;

    private StatusModel status = new StatusModel(
            StatusModel.NO_LOGGED_DRIVER,
            StatusModel.NOT_CONNECTED,
            StatusModel.NO_LOCATION_SERVICE,
            StatusModel.NOT_CONNECTED
    );

    @Inject
    public StatusUpdater(NetworkStatusObservable networkStatusObservable,
                         LocationStatusObservable locationStatusObservable) {
        this.networkStatusObservable = networkStatusObservable;
        this.locationStatusObservable = locationStatusObservable;
        this.statusSubject = BehaviorSubject.createDefault(status);
    }

    @Override
    public Observable<StatusModel> getObservableStatusModel() {
        return statusSubject;
    }
}
