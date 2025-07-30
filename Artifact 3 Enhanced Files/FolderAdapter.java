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
import com.example.cs360inventoryapp.data.models.Folder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying folders in a RecyclerView
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private List<Folder> folders;
    private List<Integer> itemCounts; // Count of items in each folder
    private final LayoutInflater inflater;
    private final OnFolderInteractionListener listener;

    public interface OnFolderInteractionListener {
        void onFolderClick(Folder folder);
        void onFolderEditClick(Folder folder);
        void onFolderDeleteClick(Folder folder);
    }

    public FolderAdapter(Context context, OnFolderInteractionListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.folders = new ArrayList<>();
        this.itemCounts = new ArrayList<>();
        this.listener = listener;
    }

    public void setFolders(List<Folder> newFolders, List<Integer> newItemCounts) {
        this.folders.clear();
        this.itemCounts.clear();

        if (newFolders != null) {
            this.folders.addAll(newFolders);
        }
        if (newItemCounts != null) {
            this.itemCounts.addAll(newItemCounts);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.folder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < folders.size()) {
            Folder folder = folders.get(position);
            int itemCount = position < itemCounts.size() ? itemCounts.get(position) : 0;
            holder.bind(folder, itemCount, listener);
        }
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivFolderIcon;
        final TextView tvFolderName;
        final TextView tvItemCount;
        final ImageButton btnEditFolder;
        final ImageButton btnDeleteFolder;

        ViewHolder(View itemView) {
            super(itemView);
            ivFolderIcon = itemView.findViewById(R.id.ivFolderIcon);
            tvFolderName = itemView.findViewById(R.id.tvFolderName);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            btnEditFolder = itemView.findViewById(R.id.btnEditFolder);
            btnDeleteFolder = itemView.findViewById(R.id.btnDeleteFolder);
        }

        void bind(final Folder folder, int itemCount, final OnFolderInteractionListener listener) {
            tvFolderName.setText(folder.getName());
            tvItemCount.setText(String.format(Locale.getDefault(), "%d items", itemCount));

            // Set folder icon
            ivFolderIcon.setImageResource(R.drawable.ic_folder);

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFolderClick(folder);
                }
            });

            btnEditFolder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFolderEditClick(folder);
                }
            });

            btnDeleteFolder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFolderDeleteClick(folder);
                }
            });
        }
    }
}