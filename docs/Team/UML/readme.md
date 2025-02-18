# Smart Parcel Delivery System

## Overview
The Smart Parcel Delivery System is designed to provide an efficient and seamless delivery experience for customers, e-commerce sellers, and delivery riders. The system ensures real-time parcel tracking, automated parcel management, and role-based access control.

## Table of Contents
- [System Requirements](#system-requirements)
- [Use Case Diagram](#use-case-diagram)
- [Class Diagram](#class-diagram)
- [Activity Diagrams](#activity-diagrams)
- [Code](#code)

## System Requirements
We will focus on the following set of requirements while designing the Smart Parcel Delivery System:

### General Requirements:
1. The system must support different user roles: Customer, E-Commerce Seller, Delivery Rider, and System Admin.
2. The system must allow users to register, log in, and manage their profiles.
3. Customers should be able to book parcels with predefined dimensions and weights.
4. The system should support real-time parcel tracking and delivery status updates.
5. The system should include authentication and authorization mechanisms.
6. Secure payment processing must be implemented for parcel bookings.
7. The system should support rider verification and delivery handover authentication.
8. The system should include analytics and reporting features for business insights.

### Role-Based Functionalities:
#### Customer:
- Register, log in, and update profile information.
- Book parcel deliveries with estimated cost calculation.
- Track parcel delivery status in real-time.
- Verify rider during parcel handover.
- Cancel parcel requests before rider acceptance.
- Rate and review delivery riders.

#### E-Commerce Seller:
- Manage multiple delivery requests through a dedicated dashboard.
- Track parcels and generate delivery reports.
- Analyze delivery performance and manage logistics.

#### Delivery Rider:
- Register and verify credentials to join the system.
- Accept or reject delivery requests based on availability.
- Update status and location for optimized route tracking.
- Verify receiver during parcel handover.
- Withdraw earnings and manage financial records.

#### System Admin:
- Manage users, roles, and system settings.
- Monitor system analytics and performance.
- Handle pricing control, fraud detection, and mitigation.
- Generate reports for cost analysis and business insights.

## Use Case Diagram
The main actors in our system are:
- **Customer** - Books parcels and tracks deliveries.
- **E-Commerce Seller** - Manages multiple deliveries and tracks orders.
- **Delivery Rider** - Accepts and delivers parcels.
- **System Admin** - Manages users and monitors system activities.

## Class Diagram
### Key Classes:
1. **User** (Base class for Customer, Seller, Rider, and Admin).
2. **Parcel** (Represents a package to be delivered).
3. **Order** (Contains parcel details and tracking information).
4. **Delivery** (Manages the rider and status updates).
5. **Payment** (Handles transactions upon delivery completion).
6. **Notification** (Sends alerts for status updates).
7. **Review** (Handles customer ratings and feedback).

## Activity Diagrams
### Parcel Booking Process:
1. Customer logs in.
2. Enters parcel details (weight, dimensions, destination).
3. Confirms order and selects payment method.
4. System assigns a delivery rider.
5. Customer receives confirmation notification.

### Parcel Delivery Process:
1. Rider receives a new delivery request.
2. Rider picks up the parcel from the sender.
3. Rider follows GPS-based route optimization.
4. Rider verifies the receiver upon delivery.
5. Payment is processed, and a confirmation notification is sent.

## Code
The system will be implemented using Java for backend processing, MySQL for database management, and a frontend interface designed in HTML/CSS with JavaScript for interactivity.

Example structure of a `Parcel` class in Java:
```java
public class Parcel {
    private String trackingNumber;
    private double weight;
    private String dimensions;
    private String senderId;
    private String receiverId;
    private String status;

    public Parcel(String trackingNumber, double weight, String dimensions, String senderId, String receiverId, String status) {
        this.trackingNumber = trackingNumber;
        this.weight = weight;
        this.dimensions = dimensions;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
```

This document provides a structured breakdown of the Smart Parcel Delivery System, covering its core requirements, use cases, class design, and implementation approach.

