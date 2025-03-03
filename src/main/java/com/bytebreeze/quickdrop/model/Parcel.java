package com.bytebreeze.quickdrop.model;

import jakarta.persistence.*;

@Entity
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String description;
    private double weight;
    private String size;
    private String pickupLocation;
    private String receiverName;
    private String receiverAddress;

    // ✅ Default Constructor
    public Parcel() {}

    // ✅ Constructor with Parameters
    public Parcel(String category, String description, double weight, String size, String pickupLocation, String receiverName, String receiverAddress) {
        this.category = category;
        this.description = description;
        this.weight = weight;
        this.size = size;
        this.pickupLocation = pickupLocation;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
    }

    // ✅ Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }
}
