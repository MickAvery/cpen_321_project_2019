package com.example.andriod.ingredishare.event;

public class Event {

    private String name;
    private String userid;
    private String description;
    private double latitude;
    private double longitude;
    private String type;
    private String email;
    private Long date;

    public Event(String userid, String name, String description, double latitude,
                 double longitude, String type, Long date) {
        this.userid = userid;
        this.name = name;
        this.type = type;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.email = userid;
        this.date = date;
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

    public String getEmail() { return email; }

    public Long getDate(){ return date; }
}
