package com.example.cs360inventoryapp.data.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a folder for organizing inventory items
 */
public class Folder implements Serializable {
    private long id;
    private String name;
    private long userId;
    private Date createdAt;

    public Folder() {
        this.createdAt = new Date();
    }

    public Folder(long id, String name, long userId, Date createdAt) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}