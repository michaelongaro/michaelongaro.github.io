package com.example.cs360inventoryapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cs360inventoryapp.R;
import com.example.cs360inventoryapp.data.DatabaseHelper;
import com.example.cs360inventoryapp.data.models.InventoryItem;
import com.example.cs360inventoryapp.data.models.User;
import com.example.cs360inventoryapp.data.models.Folder;
import com.example.cs360inventoryapp.utils.SmsHelper;

import java.util.List;
import java.util.ArrayList;

public class AddEditItemActivity extends AppCompatActivity {

    private static final String TAG = "AddEditItemActivity";

    private EditText etItemName;
    private EditText etItemQuantity;
    private EditText etItemDesc;
    private EditText etItemBarcode;
    private Button btnDeleteItem;
    private Button btnSaveItem;
    private ImageView ivClose;
    private TextView tvAddEditTitle;
    private ImageView ivProductPreview; // Not implemented, but is used in "Pro" version
    private Button btnUploadImage; // Not implemented, but is used in "Pro" version

    // Folder selection components
    private RadioGroup rgFolderSelection;
    private RadioButton rbUseExisting;
    private RadioButton rbCreateNew;
    private LinearLayout layoutExistingFolder;
    private LinearLayout layoutNewFolder;
    private Spinner spinnerExistingFolders;
    private EditText etNewFolderName;

    private DatabaseHelper dbHelper;
    private InventoryItem currentItem; // Holds item being edited, null if adding
    private long currentItemId = -1;
    private long currentUserId = -1; // Store user ID for SMS check
    private long preselectedFolderId = -1; // For when coming from folder contents

    private List<Folder> availableFolders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        // Get user id and other data from intent
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(LoginActivity.EXTRA_USER_ID)) {
                currentUserId = intent.getLongExtra(LoginActivity.EXTRA_USER_ID, -1);
            }
            if (intent.hasExtra(DashboardActivity.EXTRA_FOLDER_ID)) {
                preselectedFolderId = intent.getLongExtra(DashboardActivity.EXTRA_FOLDER_ID, -1);
            }
        }

        if (currentUserId == -1) {
            Log.e(TAG, "Invalid User ID passed to AddEditItemActivity!");
            Toast.makeText(this, "Error: Invalid session.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = DatabaseHelper.getInstance(this);

        initializeViews();
        setupFolderSelection();

        // Check if editing an item
        if (intent != null && intent.hasExtra(FolderContentsActivity.EXTRA_ITEM_ID)) {
            currentItemId = intent.getLongExtra(FolderContentsActivity.EXTRA_ITEM_ID, -1);
            if (currentItemId != -1) {
                tvAddEditTitle.setText("Edit Item");
                loadItemData();
                // Show delete button only when editing
                btnDeleteItem.setVisibility(View.VISIBLE);
            } else {
                Log.e(TAG, "Invalid item ID passed.");
                Toast.makeText(this, "Error loading item.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // Adding a new item
            tvAddEditTitle.setText("Add New Item");
            currentItem = null;
            // Ensure delete button is hidden when adding
            btnDeleteItem.setVisibility(View.GONE);
        }

        setupClickListeners();
    }

    private void initializeViews() {
        etItemName = findViewById(R.id.etItemName);
        etItemQuantity = findViewById(R.id.etItemQuantity);
        etItemDesc = findViewById(R.id.etItemDesc);
        etItemBarcode = findViewById(R.id.etItemBarcode);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);
        btnSaveItem = findViewById(R.id.btnSaveItem);
        ivClose = findViewById(R.id.ivClose);
        tvAddEditTitle = findViewById(R.id.tvAddEditTitle);
        ivProductPreview = findViewById(R.id.ivProductPreview);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        // Folder selection views
        rgFolderSelection = findViewById(R.id.rgFolderSelection);
        rbUseExisting = findViewById(R.id.rbUseExisting);
        rbCreateNew = findViewById(R.id.rbCreateNew);
        layoutExistingFolder = findViewById(R.id.layoutExistingFolder);
        layoutNewFolder = findViewById(R.id.layoutNewFolder);
        spinnerExistingFolders = findViewById(R.id.spinnerExistingFolders);
        etNewFolderName = findViewById(R.id.etNewFolderName);
    }

    private void setupFolderSelection() {
        // Load available folders
        availableFolders = dbHelper.getAllFolders(currentUserId);

        if (availableFolders.isEmpty()) {
            // No folders exist, force create new
            rbCreateNew.setChecked(true);
            rbUseExisting.setEnabled(false);
            layoutExistingFolder.setVisibility(View.GONE);
            layoutNewFolder.setVisibility(View.VISIBLE);
        } else {
            // Setup existing folders spinner
            List<String> folderNames = new ArrayList<>();
            int preselectedIndex = 0;

            for (int i = 0; i < availableFolders.size(); i++) {
                Folder folder = availableFolders.get(i);
                folderNames.add(folder.getName());
                if (preselectedFolderId != -1 && folder.getId() == preselectedFolderId) {
                    preselectedIndex = i;
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, folderNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerExistingFolders.setAdapter(adapter);
            spinnerExistingFolders.setSelection(preselectedIndex);

            // Default to using existing folder
            rbUseExisting.setChecked(true);
        }

        // Setup radio group listener
        rgFolderSelection.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbUseExisting) {
                layoutExistingFolder.setVisibility(View.VISIBLE);
                layoutNewFolder.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbCreateNew) {
                layoutExistingFolder.setVisibility(View.GONE);
                layoutNewFolder.setVisibility(View.VISIBLE);
            }
        });

        // Set initial visibility
        if (rbUseExisting.isChecked()) {
            layoutExistingFolder.setVisibility(View.VISIBLE);
            layoutNewFolder.setVisibility(View.GONE);
        } else {
            layoutExistingFolder.setVisibility(View.GONE);
            layoutNewFolder.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        btnSaveItem.setOnClickListener(v -> saveItem());
        btnDeleteItem.setOnClickListener(v -> showDeleteConfirmationDialog());
        ivClose.setOnClickListener(v -> finish());
        btnUploadImage.setOnClickListener(v -> {
            Toast.makeText(this, "Image upload feature not implemented.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadItemData() {
        // Load item details from DB (Note: this should probably be on background thread)
        currentItem = dbHelper.getItem(currentItemId);
        if (currentItem != null) {
            etItemName.setText(currentItem.getName());
            etItemQuantity.setText(String.valueOf(currentItem.getQuantity()));
            etItemDesc.setText(currentItem.getDescription());
            etItemBarcode.setText(currentItem.getBarcode());

            // Set folder selection based on current item's folder
            if (currentItem.getFolderId() > 0) {
                for (int i = 0; i < availableFolders.size(); i++) {
                    if (availableFolders.get(i).getId() == currentItem.getFolderId()) {
                        spinnerExistingFolders.setSelection(i);
                        break;
                    }
                }
            }

            // TODO: Load image into ivProductPreview using currentItem.getImagePath()
        } else {
            Log.e(TAG, "Could not find item with ID: " + currentItemId);
            Toast.makeText(this, "Error loading item data.", Toast.LENGTH_SHORT).show();
            finish(); // Close if item not found
        }
    }

    private void saveItem() {
        // Reset errors
        etItemName.setError(null);
        etItemQuantity.setError(null);
        etNewFolderName.setError(null);

        // Get data from fields
        String name = etItemName.getText().toString().trim();
        String quantityStr = etItemQuantity.getText().toString().trim();
        String description = etItemDesc.getText().toString().trim();
        String barcode = etItemBarcode.getText().toString().trim();
        // String imagePath = currentItem != null ? currentItem.getImagePath() : null;
        // TODO: Get actual image path

        boolean cancel = false;
        View focusView = null;
        int quantity = 0;

        // Validate Name
        if (TextUtils.isEmpty(name)) {
            etItemName.setError(getString(R.string.error_field_required)); // Reuse string
            focusView = etItemName;
            cancel = true;
        }

        // Validate quantity
        if (TextUtils.isEmpty(quantityStr)) {
            etItemQuantity.setError(getString(R.string.error_field_required));
            focusView = focusView == null ? etItemQuantity : focusView; // Focus first error
            cancel = true;
        } else {
            try {
                quantity = Integer.parseInt(quantityStr);
                // Optional: Add check for negative quantity if needed
                // if (quantity < 0) { ... }
            } catch (NumberFormatException e) {
                etItemQuantity.setError("Invalid number");
                focusView = focusView == null ? etItemQuantity : focusView;
                cancel = true;
            }
        }

        // Determine target folder
        long targetFolderId = -1;
        if (rbCreateNew.isChecked()) {
            String newFolderName = etNewFolderName.getText().toString().trim();
            if (TextUtils.isEmpty(newFolderName)) {
                etNewFolderName.setError("Folder name required");
                focusView = focusView == null ? etNewFolderName : focusView;
                cancel = true;
            } else {
                // Create new folder
                targetFolderId = dbHelper.addFolder(newFolderName, currentUserId);
                if (targetFolderId == -1) {
                    etNewFolderName.setError("Error creating folder. Name might already exist.");
                    focusView = focusView == null ? etNewFolderName : focusView;
                    cancel = true;
                }
            }
        } else {
            // Use existing folder
            int selectedIndex = spinnerExistingFolders.getSelectedItemPosition();
            if (selectedIndex >= 0 && selectedIndex < availableFolders.size()) {
                targetFolderId = availableFolders.get(selectedIndex).getId();
            } else {
                Toast.makeText(this, "Please select a folder", Toast.LENGTH_SHORT).show();
                cancel = true;
            }
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // Data is valid, proceed with saving
            InventoryItem itemToSave = new InventoryItem();
            itemToSave.setName(name);
            itemToSave.setQuantity(quantity);
            itemToSave.setDescription(description);
            itemToSave.setBarcode(barcode);
            // itemToSave.setImagePath(imagePath); // Set image path

            boolean success = false;
            if (currentItemId == -1) {
                // Add new item
                long newId = dbHelper.addItem(itemToSave, currentUserId, targetFolderId);
                success = newId != -1;
                if (success) {
                    Log.i(TAG, "New item added with ID: " + newId + " to folder " + targetFolderId);
                }
            } else {
                // Update existing item
                itemToSave.setId(currentItemId); // Set the id for update
                int rowsAffected = dbHelper.updateItem(itemToSave, currentUserId, targetFolderId);
                success = rowsAffected > 0;
                if (success) {
                    Log.i(TAG, "Item updated with ID: " + currentItemId + " moved to folder " + targetFolderId);
                }
            }

            if (success) {
                Toast.makeText(this, "Item saved successfully.", Toast.LENGTH_SHORT).show();
                // Check for low stock and send SMS if needed and permitted
                checkAndSendLowStockAlert(name, quantity, currentUserId);
                finish(); // Close activity and return to dashboard
            } else {
                Toast.makeText(this, "Error saving item.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Checks if the quantity is low and sends an SMS if permission is granted.
     * @param itemName The name of the item.
     * @param quantity The current quantity.
     */
    private void checkAndSendLowStockAlert(String itemName, int quantity, long userId) {
        if (quantity <= 0) {
            Log.d(TAG, "Item '" + itemName + "' quantity is " + quantity + ". Checking settings and permission for user ID: " + userId);

            if (userId == -1) {
                Log.e(TAG, "Cannot check SMS settings, invalid user ID.");
                Toast.makeText(this, "Item '" + itemName + "' is out of stock! (User error)", Toast.LENGTH_LONG).show();
                return;
            }

            // Get user settings from DB
            User userSettings = dbHelper.getUserSettings(userId);

            // Check if user wants SMS and has permission
            if (userSettings != null && userSettings.isSmsEnabled()) {
                Log.d(TAG, "SMS notifications are enabled for user " + userId + ". Checking permission.");
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    String targetPhoneNumber = userSettings.getPhoneNumber();

                    if (!TextUtils.isEmpty(targetPhoneNumber)) {
                        Log.d(TAG, "SMS permission granted. Sending low stock alert to " + targetPhoneNumber);
                        SmsHelper.sendLowStockAlert(this, targetPhoneNumber, itemName, quantity);
                    } else {
                        // Phone number is missing in settings
                        Log.w(TAG, "Cannot send SMS alert: Phone number not set for user " + userId);
                        Toast.makeText(this, getString(R.string.alert_no_phone_set), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.w(TAG, "SMS permission not granted. Cannot send low stock alert via SMS.");
                    Toast.makeText(this, "Item '" + itemName + "' is out of stock! (SMS permission denied)", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d(TAG, "SMS notifications are disabled for user " + userId + ".");
                Toast.makeText(this, "Item '" + itemName + "' is out of stock! (SMS notifications disabled in settings)", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Method to show delete confirmation
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.btn_delete_item, (dialog, which) -> {
                    // User clicked delete
                    deleteItem();
                })
                .setNegativeButton(android.R.string.cancel, null) // Do nothing on cancel
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    // Method to perform deletion
    private void deleteItem() {
        if (currentItemId == -1 || currentUserId == -1) {
            Log.e(TAG, "Cannot delete item: Invalid item ID or user ID.");
            Toast.makeText(this, R.string.item_deleted_error, Toast.LENGTH_SHORT).show();
            return;
        }

        int rowsAffected = dbHelper.deleteItem(currentItemId, currentUserId);

        if (rowsAffected > 0) {
            Toast.makeText(this, R.string.item_deleted_success, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Item deleted successfully: ID " + currentItemId + " by user " + currentUserId);
            finish(); // Close activity and return to dashboard
        } else {
            Toast.makeText(this, R.string.item_deleted_error, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Error deleting item: ID " + currentItemId + " by user " + currentUserId);
        }
    }
}
