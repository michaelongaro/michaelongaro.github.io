package com.example.cs360inventoryapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs360inventoryapp.R;
import com.example.cs360inventoryapp.data.models.InventoryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying InventoryItems in a RecyclerView.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<InventoryItem> inventoryItems;
    private final LayoutInflater inflater;
    private final OnItemInteractionListener listener;
    private final Context context;

    /**
     * Interface for handling item clicks (edit) and delete button clicks.
     */
    public interface OnItemInteractionListener {
        void onItemEditClick(InventoryItem item);
        void onItemDeleteClick(InventoryItem item);
    }

    public InventoryAdapter(Context context, OnItemInteractionListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.inventoryItems = new ArrayList<>(); // Initialize with empty list
        this.listener = listener;
        this.context = context;
    }

    /**
     * Updates the data set used by the adapter.
     * @param newItems The new list of items.
     */
    public void setItems(List<InventoryItem> newItems) {
        this.inventoryItems.clear();
        if (newItems != null) {
            this.inventoryItems.addAll(newItems);
        }
        notifyDataSetChanged(); // Notify adapter about data change. I know this isn't the ideal approach
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the grid_item layout for each item
        View view = inflater.inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem currentItem = inventoryItems.get(position);
        holder.bind(currentItem, listener);
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    /**
     * ViewHolder class holds references to the views within grid_item.xml
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivItemImage;
        final TextView tvItemName;
        final TextView tvItemQuantity;
        final ImageButton btnDeleteItem;

        ViewHolder(View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
        }

        /**
         * Binds data from an InventoryItem to the views and sets listeners.
         * @param item The item to display.
         * @param listener The listener for interactions.
         */
        void bind(final InventoryItem item, final OnItemInteractionListener listener) {
            tvItemName.setText(item.getName());
            tvItemQuantity.setText(String.format(Locale.getDefault(), "Qty: %d", item.getQuantity()));

            // TODO: Load image using item.getImagePath()
            ivItemImage.setImageResource(R.drawable.ic_launcher_background); // Replace with actual logic later for "Pro" version

            // Set listener for the entire item view (for editing)
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemEditClick(item);
                }
            });

            // Set listener for the delete button
            btnDeleteItem.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemDeleteClick(item);
                }
            });
        }
    }
}
