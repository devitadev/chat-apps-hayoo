package com.temp.chatapps_hayoo.models;

import java.io.Serializable;

public class User implements Serializable {
    private String email, name, imageProfile, phoneNumber, fcmToken, id;
    private double latitude, longitude;

    public User(String email, String name, String imageProfile, String phoneNumber, String fcmToken, double latitude, double longitude, String id) {
        this.email = email;
        this.name = name;
        this.imageProfile = imageProfile;
        this.phoneNumber = phoneNumber;
        this.fcmToken = fcmToken;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    public User() { }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getImageProfile() { return imageProfile; }

    public void setImageProfile(String imageProfile) { this.imageProfile = imageProfile; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getFcmToken() { return fcmToken; }

    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

}
