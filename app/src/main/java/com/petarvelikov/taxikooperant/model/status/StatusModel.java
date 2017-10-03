package com.petarvelikov.taxikooperant.model.status;

public class StatusModel {

    public static final int NOT_CONNECTED = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;

    public static final int NO_LOCATION_SERVICE = 0;
    public static final int NETWORK = 1;
    public static final int GPS = 2;

    private int networkStatus;
    private int locationServiceStatus;
    private int serverStatus;

    public StatusModel(int networkStatus, int locationServiceStatus, int serverStatus) {
        this.networkStatus = networkStatus;
        this.locationServiceStatus = locationServiceStatus;
        this.serverStatus = serverStatus;
    }

    public int getNetworkStatus() {
        return networkStatus;
    }

    public int getLocationServiceStatus() {
        return locationServiceStatus;
    }

    public int getServerStatus() {
        return serverStatus;
    }

    public void setNetworkStatus(int networkStatus) {
        this.networkStatus = networkStatus;
    }

    public void setLocationServiceStatus(int locationServiceStatus) {
        this.locationServiceStatus = locationServiceStatus;
    }

    public void setServerStatus(int serverStatus) {
        this.serverStatus = serverStatus;
    }

}
