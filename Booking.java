package com.example.travelbooking.models;

import java.io.Serializable;

public class Booking implements Serializable {
    private int id;
    private int userId;
    private int packageId;
    private String customerName;
    private String customerEmail;
    private String destination;
    private String serviceType;
    private String imageUrl;
    private String travelDate;
    private int travelersCount;
    private double totalAmount;
    private String status; // "CONFIRMED" or "CANCELLED"
    private String createdAt;

    public Booking(int id, int userId, int packageId, String customerName, String customerEmail,
                   String destination, String serviceType, String imageUrl, String travelDate,
                   int travelersCount, double totalAmount, String status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.packageId = packageId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.destination = destination;
        this.serviceType = serviceType;
        this.imageUrl = imageUrl;
        this.travelDate = travelDate;
        this.travelersCount = travelersCount;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getPackageId() { return packageId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getDestination() { return destination; }
    public String getServiceType() { return serviceType; }
    public String getImageUrl() { return imageUrl; }
    public String getTravelDate() { return travelDate; }
    public int getTravelersCount() { return travelersCount; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(status);
    }
}
