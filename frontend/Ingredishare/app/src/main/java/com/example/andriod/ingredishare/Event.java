package com.example.andriod.ingredishare;

public class Event {

    private String name;
    private String id;
    private String data;
    private String photo;

    public Event(String name, String eventId, String data, String photo) {
        this.name = name;
        this.id = eventId;
        this.data = data;
        this.photo = photo;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }
}
