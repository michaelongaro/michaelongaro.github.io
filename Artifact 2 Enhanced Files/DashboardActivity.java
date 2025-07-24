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
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

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


import com.example.cs360inventoryapp.utils.InventorySorter;
import com.example.cs360inventoryapp.utils.InventoryComparators;

import java.util.List;
import java.util.Comparator;

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
    private Spinner spinnerSortCriteria;
    private Spinner spinnerSortDirection;
    private long currentUserId = -1;

    private List<InventoryItem> allItems; // Store unsorted items
    private InventoryComparators.SortCriteria currentSortCriteria = InventoryComparators.SortCriteria.NAME;
    private InventoryComparators.SortDirection currentSortDirection = InventoryComparators.SortDirection.ASCENDING;

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
        setupSortingControls();
        setupClickListeners();
        handleFirstLoginSmsCheck();
    }

    private void initializeViews() {
        recyclerViewInventory = findViewById(R.id.recyclerViewInventory);
        btnAddItem = findViewById(R.id.btnAddItem);
        ivSettings = findViewById(R.id.ivSettings);
        tvDashboardTitle = findViewById(R.id.tvDashboardTitle);
        tvEmptyInventory = findViewById(R.id.tvEmptyInventory);
        spinnerSortCriteria = findViewById(R.id.spinnerSortCriteria);
        spinnerSortDirection = findViewById(R.id.spinnerSortDirection);
        dbHelper = DatabaseHelper.getInstance(this);
    }

    private void setupSortingControls() {
        // Setup sort criteria spinner
        String[] criteriaOptions = new String[InventoryComparators.SortCriteria.values().length];
        for (int i = 0; i < InventoryComparators.SortCriteria.values().length; i++) {
            criteriaOptions[i] = InventoryComparators.SortCriteria.values()[i].getDisplayName();
        }

        ArrayAdapter<String> criteriaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, criteriaOptions);
        criteriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortCriteria.setAdapter(criteriaAdapter);

        // Setup sort direction spinner
        String[] directionOptions = new String[InventoryComparators.SortDirection.values().length];
        for (int i = 0; i < InventoryComparators.SortDirection.values().length; i++) {
            directionOptions[i] = InventoryComparators.SortDirection.values()[i].getDisplayName();
        }

        ArrayAdapter<String> directionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, directionOptions);
        directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortDirection.setAdapter(directionAdapter);

        // Set default selections
        spinnerSortCriteria.setSelection(0); // Defaults to Name
        spinnerSortDirection.setSelection(0); // Defaults to Ascending

        // Setup listeners
        spinnerSortCriteria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortCriteria = InventoryComparators.SortCriteria.values()[position];
                Log.d(TAG, "Sort criteria changed to: " + currentSortCriteria.getDisplayName());
                applySorting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSortDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortDirection = InventoryComparators.SortDirection.values()[position];
                Log.d(TAG, "Sort direction changed to: " + currentSortDirection.getDisplayName());
                applySorting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupClickListeners() {
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
        allItems = dbHelper.getAllItems(currentUserId);
        Log.d(TAG, "Loaded " + allItems.size() + " items from database for user " + currentUserId);
        applySorting();
    }

    private void applySorting() {
        if (allItems == null) {
            return;
        }

        Log.d(TAG, "Applying sort: " + currentSortCriteria.getDisplayName() + " " + currentSortDirection.getDisplayName());

        // Create appropriate comparator based on current selection
        Comparator<InventoryItem> comparator = InventoryComparators.getComparator(currentSortCriteria, currentSortDirection);

        // Apply merge sort
        List<InventoryItem> sortedItems = InventorySorter.mergeSort(allItems, comparator);

        // Update adapter with sorted items
        inventoryAdapter.setItems(sortedItems);

        // Toggle visibility based on list content
        if (sortedItems.isEmpty()) {
            recyclerViewInventory.setVisibility(View.GONE);
            tvEmptyInventory.setVisibility(View.VISIBLE);
            Log.d(TAG, "Inventory empty for user " + currentUserId + ". Showing placeholder.");
        } else {
            recyclerViewInventory.setVisibility(View.VISIBLE);
            tvEmptyInventory.setVisibility(View.GONE);
            Log.d(TAG, "Inventory sorted and displayed for user " + currentUserId + ": " + sortedItems.size() + " items.");
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
