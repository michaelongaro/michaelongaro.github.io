package com.example.cs360inventoryapp.data.models;

public class User {
    private long id;
    private String username;
    // I'm not storing the password in the model after retrieval for security reasons.
    private String businessName;
    private boolean smsEnabled;
    private String phoneNumber;

    // Constructor
    public User(long id, String username, String businessName, boolean smsEnabled, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.businessName = businessName;
        this.smsEnabled = smsEnabled;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getBusinessName() {
        return businessName;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }
    public String getPhoneNumber() { return phoneNumber; }
}
