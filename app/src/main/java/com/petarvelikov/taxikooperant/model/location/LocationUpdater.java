package com.petarvelikov.taxikooperant.model.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.petarvelikov.taxikooperant.model.interfaces.LocationObservable;
import com.petarvelikov.taxikooperant.model.interfaces.LocationStatusObservable;
import com.petarvelikov.taxikooperant.model.status.StatusModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class LocationUpdater implements LocationListener,
        LocationStatusObservable, LocationObservable {

    private LocationManager locationManager;
    private Location lastLocation;
    private BehaviorSubject<Location> locationSubject;
    private BehaviorSubject<Integer> statusSubject;
    private boolean gpsAvailable = false, networkAvailable = false;

    @Inject
    public LocationUpdater(LocationManager locationManager) {
        this.locationManager = locationManager;
        locationSubject = BehaviorSubject.create();
        statusSubject = BehaviorSubject.createDefault(StatusModel.NO_LOCATION_SERVICE);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d("Location", "Changed");
            lastLocation = location;
            locationSubject.onNext(lastLocation);
            statusSubject.onNext(StatusModel.GPS);
        } else {
            statusSubject.onNext(StatusModel.NO_LOCATION_SERVICE);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (provider) {
            case LocationManager.GPS_PROVIDER:
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        statusSubject.onNext(StatusModel.GPS);
                        gpsAvailable = true;
                        break;
                    default:
                        if (networkAvailable) {
                            statusSubject.onNext(StatusModel.NETWORK);
                        } else {
                            statusSubject.onNext(StatusModel.NO_LOCATION_SERVICE);
                        }
                        gpsAvailable = false;
                        break;
                }
                break;
            case LocationManager.NETWORK_PROVIDER:
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        if (!gpsAvailable) {
                            statusSubject.onNext(StatusModel.NETWORK);
                        }
                        networkAvailable = true;
                        break;
                    default:
                        if (!gpsAvailable) {
                            statusSubject.onNext(StatusModel.NO_LOCATION_SERVICE);
                        }
                        networkAvailable = false;
                        break;
                }
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        updateLocationStatus();
    }

    @Override
    public void onProviderDisabled(String provider) {
        updateLocationStatus();
    }

    private void updateLocationStatus() {
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gpsEnabled && !networkEnabled) {
            // Show no location
            gpsAvailable = networkAvailable = false;
            statusSubject.onNext(StatusModel.NO_LOCATION_SERVICE);
        } else if (!gpsEnabled) {
            // Show network
            gpsAvailable = false;
            networkAvailable = true;
            statusSubject.onNext(StatusModel.NETWORK);
        } else if (!networkEnabled) {
            // Show gps
            gpsAvailable = true;
            networkAvailable = false;
            statusSubject.onNext(StatusModel.GPS);
        } else {
            gpsAvailable = networkAvailable = true;
            statusSubject.onNext(StatusModel.GPS);
        }
    }

    public void startListeningLocationChanges() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
            lastLocation = getLastKnownLocation();
            if (lastLocation != null) {
                locationSubject.onNext(lastLocation);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    public void stopListeningLocationChanges() {
        locationManager.removeUpdates(this);
    }

    private Location getLastKnownLocation() throws SecurityException {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location tmp = locationManager.getLastKnownLocation(provider);
            if (tmp == null)
                continue;
            if (bestLocation == null || bestLocation.getAccuracy() > tmp.getAccuracy()) {
                bestLocation = tmp;
            }
        }
        return bestLocation;
    }

    @Override
    public Observable<Location> getLocationObservable() {
        return locationSubject;
    }

    @Override
    public Observable<Integer> getLocationStatusObservable() {
        return statusSubject;
    }
}
