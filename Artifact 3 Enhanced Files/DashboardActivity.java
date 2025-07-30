package com.example.cs360inventoryapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;

import com.example.cs360inventoryapp.R;
import com.example.cs360inventoryapp.adapters.FolderAdapter;
import com.example.cs360inventoryapp.data.DatabaseHelper;
import com.example.cs360inventoryapp.data.models.User;
import com.example.cs360inventoryapp.data.models.Folder;
import com.example.cs360inventoryapp.data.models.InventoryItem;


import com.example.cs360inventoryapp.utils.InventorySorter;
import com.example.cs360inventoryapp.utils.InventoryComparators;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class DashboardActivity extends AppCompatActivity implements FolderAdapter.OnFolderInteractionListener {

    private static final String TAG = "DashboardActivity";
    public static final String EXTRA_FOLDER_ID = "com.example.cs360inventoryapp.FOLDER_ID"; // For passing folder id

    private RecyclerView recyclerViewFolders;
    private FolderAdapter folderAdapter;
    private DatabaseHelper dbHelper;
    private Button btnAddItem;
    private Button btnAddFolder;
    private TextView tvDashboardTitle;
    private ImageView ivSettings;
    private TextView tvEmptyFolders;
    private long currentUserId = -1;
    private List<Folder> allFolders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Retrieve logged-in user id from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(LoginActivity.EXTRA_USER_ID)) {
            currentUserId = intent.getLongExtra(LoginActivity.EXTRA_USER_ID, -1);
        }

        if (currentUserId == -1) {
            // If id wasn't passed correctly, something is wrong.
            Log.e(TAG, "No valid User ID passed to DashboardActivity! Redirecting to login.");
            Toast.makeText(this, "Error: User session invalid.", Toast.LENGTH_LONG).show();
            // Redirect back to Login
            Intent loginIntent = new Intent(this, LoginActivity.class);
            // Add flags to clear the task stack in case this somehow got launched incorrectly
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }
        Log.d(TAG, "Dashboard started for User ID: " + currentUserId);

        initializeViews();
        setDashboardTitle();
        setupRecyclerView();
        setupClickListeners();
        handleFirstLoginSmsCheck();
    }

    private void initializeViews() {
        recyclerViewFolders = findViewById(R.id.recyclerViewFolders);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnAddFolder = findViewById(R.id.btnAddFolder);
        ivSettings = findViewById(R.id.ivSettings);
        tvDashboardTitle = findViewById(R.id.tvDashboardTitle);
        tvEmptyFolders = findViewById(R.id.tvEmptyFolders);
        dbHelper = DatabaseHelper.getInstance(this);
    }

    private void setupClickListeners() {
        btnAddItem.setOnClickListener(v -> {
            // Check if user has folders before adding item
            if (allFolders == null || allFolders.isEmpty()) {
                showCreateFolderFirstDialog();
            } else {
                Intent addIntent = new Intent(DashboardActivity.this, AddEditItemActivity.class);
                addIntent.putExtra(LoginActivity.EXTRA_USER_ID, currentUserId);
                startActivity(addIntent);
            }
        });

        btnAddFolder.setOnClickListener(v -> showAddFolderDialog());

        ivSettings.setOnClickListener(v -> {
            Log.d(TAG, "Settings icon clicked.");
            Intent settingsIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
            settingsIntent.putExtra(LoginActivity.EXTRA_USER_ID, currentUserId);
            startActivity(settingsIntent);
        });
    }

    private void setDashboardTitle() {
        if (currentUserId != -1 && dbHelper != null && tvDashboardTitle != null) {
            User userSettings = dbHelper.getUserSettings(currentUserId);
            if (userSettings != null && userSettings.getBusinessName() != null && !userSettings.getBusinessName().isEmpty()) {
                // Set the retrieved business name
                tvDashboardTitle.setText(userSettings.getBusinessName());
                Log.d(TAG, "Dashboard title set to: " + userSettings.getBusinessName());
            } else {
                // Fallback if settings or name not found or empty
                tvDashboardTitle.setText(getString(R.string.label_grid_header)); // Use default string
                Log.w(TAG, "Could not retrieve business name for user " + currentUserId + ", using default title.");
            }
        } else {
            // Fallback if something is wrong (view not found)
            if(tvDashboardTitle != null) {
                tvDashboardTitle.setText(getString(R.string.label_grid_header));
            }
            Log.e(TAG, "Could not set dashboard title due to invalid state (userId, dbHelper, or view).");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId != -1) { // Only load if user id is valid
            loadFolders();
            setDashboardTitle();
        }
    }

    private void setupRecyclerView() {
        recyclerViewFolders.setLayoutManager(new LinearLayoutManager(this));
        folderAdapter = new FolderAdapter(this, this);
        recyclerViewFolders.setAdapter(folderAdapter);
    }

    private void loadFolders() {
        allFolders = dbHelper.getAllFolders(currentUserId);
        Log.d(TAG, "Loaded " + allFolders.size() + " folders from database for user " + currentUserId);

        // Get item counts for each folder
        List<Integer> itemCounts = new ArrayList<>();
        for (Folder folder : allFolders) {
            int count = dbHelper.getItemCountInFolder(folder.getId());
            itemCounts.add(count);
        }

        folderAdapter.setFolders(allFolders, itemCounts);

        // Toggle visibility based on list content
        if (allFolders.isEmpty()) {
            recyclerViewFolders.setVisibility(View.GONE);
            tvEmptyFolders.setVisibility(View.VISIBLE);
            Log.d(TAG, "No folders found for user " + currentUserId + ". Showing placeholder.");
        } else {
            recyclerViewFolders.setVisibility(View.VISIBLE);
            tvEmptyFolders.setVisibility(View.GONE);
            Log.d(TAG, "Folders displayed for user " + currentUserId + ": " + allFolders.size() + " folders.");
        }
    }

    private void showCreateFolderFirstDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Create Folder First")
                .setMessage("You need to create at least one folder before adding items. Would you like to create a folder now?")
                .setPositiveButton("Create Folder", (dialog, which) -> showAddFolderDialog())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void showAddFolderDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Folder name");

        new AlertDialog.Builder(this)
                .setTitle("Add New Folder")
                .setMessage("Enter a name for the new folder:")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String folderName = input.getText().toString().trim();
                    if (!folderName.isEmpty()) {
                        createFolder(folderName);
                    } else {
                        Toast.makeText(this, "Folder name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createFolder(String folderName) {
        long folderId = dbHelper.addFolder(folderName, currentUserId);
        if (folderId != -1) {
            Toast.makeText(this, "Folder '" + folderName + "' created successfully", Toast.LENGTH_SHORT).show();
            loadFolders(); // Refresh the list
        } else {
            Toast.makeText(this, "Error creating folder. Name might already exist.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditFolderDialog(Folder folder) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(folder.getName());
        input.selectAll();

        new AlertDialog.Builder(this)
                .setTitle("Edit Folder")
                .setMessage("Enter new name for the folder:")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        if (dbHelper.updateFolder(folder.getId(), newName, currentUserId)) {
                            Toast.makeText(this, "Folder renamed successfully", Toast.LENGTH_SHORT).show();
                            loadFolders(); // Refresh the list
                        } else {
                            Toast.makeText(this, "Error renaming folder. Name might already exist.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Folder name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // FolderAdapter.OnFolderInteractionListener implementation

    @Override
    public void onFolderClick(Folder folder) {
        Log.d(TAG, "Folder clicked: " + folder.getName());
        Intent intent = new Intent(this, FolderContentsActivity.class);
        intent.putExtra(EXTRA_FOLDER_ID, folder.getId());
        intent.putExtra(LoginActivity.EXTRA_USER_ID, currentUserId);
        startActivity(intent);
    }

    @Override
    public void onFolderEditClick(Folder folder) {
        Log.d(TAG, "Edit clicked for folder: " + folder.getName());
        showEditFolderDialog(folder);
    }

    @Override
    public void onFolderDeleteClick(Folder folder) {
        Log.d(TAG, "Delete clicked for folder: " + folder.getName());

        // Check if folder has items
        int itemCount = dbHelper.getItemCountInFolder(folder.getId());
        if (itemCount > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Folder")
                    .setMessage("This folder contains " + itemCount + " item(s). Please move or delete all items before deleting the folder.")
                    .setPositiveButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        // Show confirmation dialog for empty folder
        new AlertDialog.Builder(this)
                .setTitle("Delete Folder")
                .setMessage("Are you sure you want to delete '" + folder.getName() + "'?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (dbHelper.deleteFolder(folder.getId(), currentUserId)) {
                        Toast.makeText(this, "Folder deleted.", Toast.LENGTH_SHORT).show();
                        loadFolders(); // Refresh the list
                    } else {
                        Toast.makeText(this, "Error deleting folder.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    // SMS permission handling for first login
    private void handleFirstLoginSmsCheck() {
        SharedPreferences sharedPref = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        boolean firstCheckDone = sharedPref.getBoolean(LoginActivity.PREF_FIRST_LOGIN_SMS_CHECK_DONE, false);

        if (!firstCheckDone) {
            Log.d(TAG, "First login SMS check not done yet.");
            // Check if permission is currently granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, show the rationale screen immediately
                Log.d(TAG, "SMS permission not granted on first login. Launching SmsPermissionActivity.");
                Intent intent = new Intent(this, SmsPermissionActivity.class);
                intent.putExtra(LoginActivity.EXTRA_USER_ID, currentUserId);
                startActivity(intent);
            } else {
                // Permission already granted on first login (unlikely but I think it's still technically possible)
                Log.d(TAG, "SMS permission was already granted on first login.");
            }

            // Mark the first login check as completed so it doesn't run again
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(LoginActivity.PREF_FIRST_LOGIN_SMS_CHECK_DONE, true);
            editor.apply();
            Log.d(TAG, "Marked first login SMS check as done.");

        } else {
            Log.d(TAG, "First login SMS check already completed.");
        }
    }
}
