package com.example.andriod.ingredishare;

public class Event {

    private String name;
    private String userid;
    private String description;
    private float latitude;
    private float longitude;

    public Event(String userid, String name, String description, float latitude, float longitude) {
        this.userid = userid;
        this.name = name;
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

    public float getLatitude(){
        return latitude;
    }

    public float getLongitude(){
        return longitude;
    }
}
