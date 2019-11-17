package com.example.andriod.ingredishare.Event;

public class Event {

    private String name;
    private String userid;
    private String description;
    private double latitude;
    private double longitude;
    private String type;

    public Event(String userid, String name, String description, double latitude, double longitude, String type) {
        this.userid = userid;
        this.name = name;
        this.type = type;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getUserId(){
        return userid;
    }
    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public String getType(){
        return type;
    }
}
