package com.example.andriod.ingredishare;

public class Event {

    private String name;
    private String userid;
    private String description;
    private String photo;

    public Event(String userid, String name, String description, String photo) {
        this.userid = userid;
        this.name = name;
        this.description = description;
        this.photo = photo;
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
}
