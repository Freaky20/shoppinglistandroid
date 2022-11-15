package com.shopping.list;

public class Location {
    private String name;
    private double latitude;
    private double longitude;
    private boolean geofence;
    private int locationID;

    public Location(){
        this.setGeofence(true);
        this.setLocationID(-1);
    }

    public Location(String name, double latitude, double longitude){
        this.setName(name);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setGeofence(true);
        this.setLocationID(-1);
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public double getLatitude() {

        return latitude;
    }

    public void setLatitude(double latitude) {

        this.latitude = latitude;
    }

    public double getLongitude() {

        return longitude;
    }

    public void setLongitude(double longitude) {

        this.longitude = longitude;
    }

    public boolean isGeofence() {

        return geofence;
    }

    public void setGeofence(boolean geofence) {

        this.geofence = geofence;
    }

    public int getLocationID() {

        return locationID;
    }

    public void setLocationID(int locationID) {

        this.locationID = locationID;
    }
}

