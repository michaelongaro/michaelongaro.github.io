package com.example.cs360inventoryapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs360inventoryapp.R;
import com.example.cs360inventoryapp.adapters.InventoryAdapter;
import com.example.cs360inventoryapp.data.DatabaseHelper;
import com.example.cs360inventoryapp.data.models.Folder;
import com.example.cs360inventoryapp.data.models.InventoryItem;
import com.example.cs360inventoryapp.utils.InventorySorter;
import com.example.cs360inventoryapp.utils.InventoryComparators;

import java.util.List;
import java.util.Comparator;

public class FolderContentsActivity extends AppCompatActivity implements InventoryAdapter.OnItemInteractionListener {

    private static final String TAG = "FolderContentsActivity";
    public static final String EXTRA_ITEM_ID = "com.example.cs360inventoryapp.ITEM_ID";

    private RecyclerView recyclerViewInventory;
    private InventoryAdapter inventoryAdapter;
    private DatabaseHelper dbHelper;
    private Button btnAddItem;
    private ImageButton btnBack;
    private TextView tvFolderName;
    private ImageView ivSettings;
    private TextView tvEmptyInventory;
    private Spinner spinnerSortCriteria;
    private Spinner spinnerSortDirection;

    private long currentUserId = -1;
    private long currentFolderId = -1;
    private Folder currentFolder;

    private List<InventoryItem> allItems;
    private InventoryComparators.SortCriteria currentSortCriteria = InventoryComparators.SortCriteria.NAME;
    private InventoryComparators.SortDirection currentSortDirection = InventoryComparators.SortDirection.ASCENDING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_contents);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            currentUserId = intent.getLongExtra(LoginActivity.EXTRA_USER_ID, -1);
            currentFolderId = intent.getLongExtra(DashboardActivity.EXTRA_FOLDER_ID, -1);
        }

        if (currentUserId == -1 || currentFolderId == -1) {
            Log.e(TAG, "Invalid User ID or Folder ID passed to FolderContentsActivity!");
            Toast.makeText(this, "Error: Invalid session data.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d(TAG, "FolderContents started for User ID: " + currentUserId + ", Folder ID: " + currentFolderId);

        initializeViews();
        loadFolderInfo();
        setupRecyclerView();
        setupSortingControls();
        setupClickListeners();
    }

    private void initializeViews() {
        recyclerViewInventory = findViewById(R.id.recyclerViewInventory);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnBack = findViewById(R.id.btnBack);
        ivSettings = findViewById(R.id.ivSettings);
        tvFolderName = findViewById(R.id.tvFolderName);
        tvEmptyInventory = findViewById(R.id.tvEmptyInventory);
        spinnerSortCriteria = findViewById(R.id.spinnerSortCriteria);
        spinnerSortDirection = findViewById(R.id.spinnerSortDirection);
        dbHelper = DatabaseHelper.getInstance(this);
    }

    private void loadFolderInfo() {
        currentFolder = dbHelper.getFolder(currentFolderId);
        if (currentFolder != null) {
            tvFolderName.setText(currentFolder.getName());
        } else {
            Log.e(TAG, "Folder not found with ID: " + currentFolderId);
            Toast.makeText(this, "Error: Folder not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
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
        spinnerSortCriteria.setSelection(0);
        spinnerSortDirection.setSelection(0);

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
            Intent addIntent = new Intent(FolderContentsActivity.this, AddEditItemActivity.class);
            addIntent.putExtra(LoginActivity.EXTRA_USER_ID, currentUserId);
            addIntent.putExtra(DashboardActivity.EXTRA_FOLDER_ID, currentFolderId);
            startActivity(addIntent);
        });

        btnBack.setOnClickListener(v -> finish());

        ivSettings.setOnClickListener(v -> {
            Log.d(TAG, "Settings icon clicked.");
            Intent settingsIntent = new Intent(FolderContentsActivity.this, SettingsActivity.class);
            settingsIntent.putExtra(LoginActivity.EXTRA_USER_ID, currentUserId);
            startActivity(settingsIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId != -1 && currentFolderId != -1) {
            loadInventoryItems();
        }
    }

    private void setupRecyclerView() {
        int numberOfColumns = 2;
        recyclerViewInventory.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        inventoryAdapter = new InventoryAdapter(this, this);
        recyclerViewInventory.setAdapter(inventoryAdapter);
    }

    private void loadInventoryItems() {
        allItems = dbHelper.getItemsInFolder(currentFolderId, currentUserId);
        Log.d(TAG, "Loaded " + allItems.size() + " items from folder " + currentFolderId + " for user " + currentUserId);
        applySorting();
    }

    private void applySorting() {
        if (allItems == null) {
            return;
        }

        Log.d(TAG, "Applying sort: " + currentSortCriteria.getDisplayName() + " " + currentSortDirection.getDisplayName());

        Comparator<InventoryItem> comparator = InventoryComparators.getComparator(currentSortCriteria, currentSortDirection);
        List<InventoryItem> sortedItems = InventorySorter.mergeSort(allItems, comparator);

        inventoryAdapter.setItems(sortedItems);

        // Toggle visibility based on list content
        if (sortedItems.isEmpty()) {
            recyclerViewInventory.setVisibility(View.GONE);
            tvEmptyInventory.setVisibility(View.VISIBLE);
            Log.d(TAG, "Folder " + currentFolderId + " is empty. Showing placeholder.");
        } else {
            recyclerViewInventory.setVisibility(View.VISIBLE);
            tvEmptyInventory.setVisibility(View.GONE);
            Log.d(TAG, "Folder contents displayed: " + sortedItems.size() + " items.");
        }
    }

    // InventoryAdapter.OnItemInteractionListener implementation

    @Override
    public void onItemEditClick(InventoryItem item) {
        Log.d(TAG, "Edit clicked for item: " + item.getName());
        Intent intent = new Intent(FolderContentsActivity.this, AddEditItemActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, item.getId());
        intent.putExtra(LoginActivity.EXTRA_USER_ID, currentUserId);
        intent.putExtra(DashboardActivity.EXTRA_FOLDER_ID, currentFolderId);
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
                    int rowsAffected = dbHelper.deleteItem(item.getId(), currentUserId);
                    if (rowsAffected > 0) {
                        Toast.makeText(FolderContentsActivity.this, "Item deleted.", Toast.LENGTH_SHORT).show();
                        loadInventoryItems(); // Refresh the list
                    } else {
                        Toast.makeText(FolderContentsActivity.this, "Error deleting item.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}