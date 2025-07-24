package com.example.cs360inventoryapp.utils;

import com.example.cs360inventoryapp.data.models.InventoryItem;
import java.util.Comparator;

/**
 * Collection of comparators for sorting inventory items by different criteria.
 */
public class InventoryComparators {

    /**
     * Comparator for sorting by item name (case-insensitive).
     */
    public static class NameComparator implements Comparator<InventoryItem> {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            if (item1.getName() == null && item2.getName() == null) return 0;
            if (item1.getName() == null) return 1;
            if (item2.getName() == null) return -1;
            return item1.getName().compareToIgnoreCase(item2.getName());
        }
    }

    /**
     * Comparator for sorting by item quantity.
     */
    public static class QuantityComparator implements Comparator<InventoryItem> {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            return Integer.compare(item1.getQuantity(), item2.getQuantity());
        }
    }

    /**
     * Comparator for sorting by creation date.
     */
    public static class DateComparator implements Comparator<InventoryItem> {
        @Override
        public int compare(InventoryItem item1, InventoryItem item2) {
            if (item1.getCreatedAt() == null && item2.getCreatedAt() == null) return 0;
            if (item1.getCreatedAt() == null) return 1;
            if (item2.getCreatedAt() == null) return -1;
            return item1.getCreatedAt().compareTo(item2.getCreatedAt());
        }
    }

    /**
     * Enum for sort criteria.
     */
    public enum SortCriteria {
        NAME("Name"),
        QUANTITY("Quantity"),
        DATE("Date Added");

        private final String displayName;

        SortCriteria(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enum for sort direction.
     */
    public enum SortDirection {
        ASCENDING("Ascending"),
        DESCENDING("Descending");

        private final String displayName;

        SortDirection(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Factory method to create appropriate comparator based on criteria and direction.
     */
    public static Comparator<InventoryItem> getComparator(SortCriteria criteria, SortDirection direction) {
        Comparator<InventoryItem> comparator;

        switch (criteria) {
            case NAME:
                comparator = new NameComparator();
                break;
            case QUANTITY:
                comparator = new QuantityComparator();
                break;
            case DATE:
                comparator = new DateComparator();
                break;
            default:
                comparator = new NameComparator();
        }

        if (direction == SortDirection.DESCENDING) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}