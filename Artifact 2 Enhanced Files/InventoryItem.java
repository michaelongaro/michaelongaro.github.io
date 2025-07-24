package com.example.cs360inventoryapp.data.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an inventory item.
 * Implements Serializable to allow passing between activities via Intent extras.
 */
public class InventoryItem implements Serializable {

    private long id;
    private String name;

    private int quantity;
    private String description;
    private String barcode;
    private String imagePath;
    private Date createdAt;

    public InventoryItem() {
        this.createdAt = new Date(); // Defaults to current time
    }

    public InventoryItem(long id, String name, int quantity, String description, String barcode, String imagePath, Date createdAt) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.barcode = barcode;
        this.imagePath = imagePath;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                '}';
    }
}
