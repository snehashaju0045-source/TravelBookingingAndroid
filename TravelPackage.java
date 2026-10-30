package com.example.travelbooking.models;

import java.io.Serializable;

public class TravelPackage implements Serializable {
    private int id;
    private String destination;
    private String serviceType;
    private String details;
    private double price;
    private String imageUrl;
    private String duration;
    private double rating;
    private boolean active;

    public TravelPackage(int id, String destination, String serviceType, String details, double price, String imageUrl, String duration, double rating, boolean active) {
        this.id = id;
        this.destination = destination;
        this.serviceType = serviceType;
        this.details = details;
        this.price = price;
        this.imageUrl = imageUrl;
        this.duration = duration;
        this.rating = rating;
        this.active = active;
    }

    public int getId() { return id; }
    public String getDestination() { return destination; }
    public String getServiceType() { return serviceType; }
    public String getDetails() { return details; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getDuration() { return duration; }
    public double getRating() { return rating; }
    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}
