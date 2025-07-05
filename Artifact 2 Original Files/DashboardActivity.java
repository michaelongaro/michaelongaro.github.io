package com.example.cs360inventoryapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;

import com.example.cs360inventoryapp.R;
import com.example.cs360inventoryapp.adapters.InventoryAdapter;
import com.example.cs360inventoryapp.data.DatabaseHelper;
import com.example.cs360inventoryapp.data.models.User;
import com.example.cs360inventoryapp.data.models.InventoryItem;

import java.util.List;

public class DashboardActivity extends AppCompatActivity implements InventoryAdapter.OnItemInteractionListener {

    private static final String TAG = "DashboardActivity";
    public static final String EXTRA_ITEM_ID = "com.example.cs360inventoryapp.ITEM_ID"; // For passing item id

    private RecyclerView recyclerViewInventory;
    private InventoryAdapter inventoryAdapter;
    private DatabaseHelper dbHelper;
    private Button btnAddItem;
    private TextView tvDashboardTitle;
    private ImageView ivSettings;
    private TextView tvEmptyInventory;
    private long currentUserId = -1;

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

        recyclerViewInventory = findViewById(R.id.recyclerViewInventory);
        btnAddItem = findViewById(R.id.btnAddItem);
        ivSettings = findViewById(R.id.ivSettings);
        tvDashboardTitle = findViewById(R.id.tvDashboardTitle);
        tvEmptyInventory = findViewById(R.id.tvEmptyInventory);
        dbHelper = DatabaseHelper.getInstance(this);

        setDashboardTitle();
        setupRecyclerView();

        btnAddItem.setOnClickListener(v -> {
            Intent addIntent = new Intent(DashboardActivity.this, AddEditItemActivity.class);
            // Pass user id to AddEditItemActivity
            addIntent.putExtra(LoginActivity.EXTRA_USER_ID, currentUserId);
            startActivity(addIntent);
        });

        ivSettings.setOnClickListener(v -> {
            Log.d(TAG, "Settings icon clicked.");
            Intent settingsIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
            // Pass the user id to SettingsActivity
            settingsIntent.putExtra(LoginActivity.EXTRA_USER_ID, currentUserId);
            startActivity(settingsIntent);
        });

        handleFirstLoginSmsCheck();
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
            loadInventoryItems();
            setDashboardTitle();
        }
    }

    private void setupRecyclerView() {
        int numberOfColumns = 2;
        recyclerViewInventory.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        inventoryAdapter = new InventoryAdapter(this, this); // Pass context and listener
        recyclerViewInventory.setAdapter(inventoryAdapter);
    }

    private void loadInventoryItems() {
        List<InventoryItem> items = dbHelper.getAllItems(currentUserId);
        inventoryAdapter.setItems(items); // Update adapter regardless of list size

        // Toggle visibility based on list content
        if (items.isEmpty()) {
            recyclerViewInventory.setVisibility(View.GONE); // Hide RecyclerView
            tvEmptyInventory.setVisibility(View.VISIBLE); // Show placeholder
            Log.d(TAG, "Inventory empty for user " + currentUserId + ". Showing placeholder.");
        } else {
            recyclerViewInventory.setVisibility(View.VISIBLE); // Show RecyclerView
            tvEmptyInventory.setVisibility(View.GONE); // Hide placeholder
            Log.d(TAG, "Inventory loaded for user " + currentUserId + ": " + items.size() + " items.");
        }
    }

    // InventoryAdapter.OnItemInteractionListener implementation

    @Override
    public void onItemEditClick(InventoryItem item) {
        Log.d(TAG, "Edit clicked for item: " + item.getName());
        Intent intent = new Intent(DashboardActivity.this, AddEditItemActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, item.getId());
        // Pass user id to AddEditItemActivity
        intent.putExtra(LoginActivity.EXTRA_USER_ID, currentUserId);
        startActivity(intent);
    }

    @Override
    public void onItemDeleteClick(InventoryItem item) {
        Log.d(TAG, "Delete clicked for item: " + item.getName());
        // Show confirmation dialog before deleting
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete '" + item.getName() + "'?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // User confirmed deletion
                    int rowsAffected = dbHelper.deleteItem(item.getId(), currentUserId);
                    if (rowsAffected > 0) {
                        Toast.makeText(DashboardActivity.this, "Item deleted.", Toast.LENGTH_SHORT).show();
                        loadInventoryItems(); // Refresh the list
                    } else {
                        Toast.makeText(DashboardActivity.this, "Error deleting item.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null) // Do nothing on cancel
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
