package com.example.cs360inventoryapp.utils;

import com.example.cs360inventoryapp.data.models.InventoryItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for sorting inventory items using Merge Sort algorithm.
 * Provides O(n log n) performance with stable sorting.
 */
public class InventorySorter {

    /**
     * Sorts a list of inventory items using Merge Sort algorithm.
     *
     * @param items The list of items to sort
     * @param comparator The comparator to use for ordering
     * @return A new sorted list (original list is not modified)
     */
    public static List<InventoryItem> mergeSort(List<InventoryItem> items, Comparator<InventoryItem> comparator) {
        if (items == null || items.size() <= 1) {
            return new ArrayList<>(items != null ? items : new ArrayList<>());
        }

        // Divide: Find the middle of the list
        int middle = items.size() / 2;
        List<InventoryItem> leftHalf = new ArrayList<>(items.subList(0, middle));
        List<InventoryItem> rightHalf = new ArrayList<>(items.subList(middle, items.size()));

        // Conquer: Recursively sort both halves
        List<InventoryItem> sortedLeft = mergeSort(leftHalf, comparator);
        List<InventoryItem> sortedRight = mergeSort(rightHalf, comparator);

        // Combine: Merge the sorted halves
        return merge(sortedLeft, sortedRight, comparator);
    }

    /**
     * Merges two sorted lists into one sorted list.
     */
    private static List<InventoryItem> merge(List<InventoryItem> left, List<InventoryItem> right,
                                             Comparator<InventoryItem> comparator) {
        List<InventoryItem> result = new ArrayList<>();
        int leftIndex = 0;
        int rightIndex = 0;

        // Merge elements while both lists have remaining items
        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (comparator.compare(left.get(leftIndex), right.get(rightIndex)) <= 0) {
                result.add(left.get(leftIndex));
                leftIndex++;
            } else {
                result.add(right.get(rightIndex));
                rightIndex++;
            }
        }

        // Add any remaining elements from left list
        while (leftIndex < left.size()) {
            result.add(left.get(leftIndex));
            leftIndex++;
        }

        // Add any remaining elements from right list
        while (rightIndex < right.size()) {
            result.add(right.get(rightIndex));
            rightIndex++;
        }

        return result;
    }
}