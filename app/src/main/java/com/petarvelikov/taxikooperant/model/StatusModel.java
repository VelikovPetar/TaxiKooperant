package com.petarvelikov.taxikooperant.model;

public class StatusModel {

    public static final int NOT_CONNECTED = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;

    public static final int NO_LOCATION_SERVICE = 0;
    public static final int NETWORK = 1;
    public static final int GPS = 2;

    public static final String NO_LOGGED_DRIVER = "Нема најавен возач";

    private String driverStatus;
    private int networkStatus;
    private int locationServiceStatus;
    private int serverStatus;

    public StatusModel(String driverStatus, int networkStatus, int locationServiceStatus, int serverStatus) {
        this.driverStatus = driverStatus;
        this.networkStatus = networkStatus;
        this.locationServiceStatus = locationServiceStatus;
        this.serverStatus = serverStatus;
    }

    public String getDriverStatus() {
        return driverStatus;
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

    public void setDriverStatus(String driverStatus) {
        this.driverStatus = driverStatus;
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
