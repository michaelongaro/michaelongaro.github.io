package com.example.cs360inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

import com.example.cs360inventoryapp.data.models.User;
import com.example.cs360inventoryapp.data.models.InventoryItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Manages the SQLite database for users and inventory items.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database Info
    private static final String DATABASE_NAME = "inventoryManager.db";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_INVENTORY_ITEMS = "inventory_items";

    // Users Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_USERNAME = "username";
    private static final String KEY_USER_PASSWORD = "password_hash";
    private static final String KEY_USER_BUSINESS_NAME = "business_name";
    private static final String KEY_USER_SMS_ENABLED = "sms_enabled"; // Integer of 0 or 1
    private static final String KEY_USER_PHONE_NUMBER = "phone_number";

    // Inventory Items Table Columns
    private static final String KEY_ITEM_ID = "id";
    private static final String KEY_ITEM_NAME = "name";
    private static final String KEY_ITEM_QUANTITY = "quantity";
    private static final String KEY_ITEM_DESCRIPTION = "description";
    private static final String KEY_ITEM_BARCODE = "barcode";
    private static final String KEY_ITEM_IMAGE_PATH = "image_path";
    private static final String KEY_ITEM_USER_ID = "user_id";
    private static final String KEY_ITEM_CREATED_AT = "created_at";

    // Singleton instance
    private static DatabaseHelper instance;
    private Context context;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Called when the database connection is being configured.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL query to create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USER_USERNAME + " TEXT UNIQUE NOT NULL," +
                KEY_USER_PASSWORD + " TEXT NOT NULL," + // Stores the BCrypt salted hash
                KEY_USER_BUSINESS_NAME + " TEXT," +
                KEY_USER_SMS_ENABLED + " INTEGER NOT NULL DEFAULT 0," + // defaults to disabled
                KEY_USER_PHONE_NUMBER + " TEXT" +
                ")";

        // SQL query to create inventory items table
        String CREATE_INVENTORY_ITEMS_TABLE = "CREATE TABLE " + TABLE_INVENTORY_ITEMS +
                "(" +
                KEY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_ITEM_NAME + " TEXT NOT NULL," +
                KEY_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                KEY_ITEM_DESCRIPTION + " TEXT," +
                KEY_ITEM_BARCODE + " TEXT," +
                KEY_ITEM_IMAGE_PATH + " TEXT," +
                KEY_ITEM_USER_ID + " INTEGER NOT NULL," +
                KEY_ITEM_CREATED_AT + " INTEGER NOT NULL," + // Storing as a timestamp
                "FOREIGN KEY(" + KEY_ITEM_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ") ON DELETE CASCADE" +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_INVENTORY_ITEMS_TABLE);
        Log.i(TAG, "Database tables created.");
    }

    // Called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add created_at column to existing inventory_items table
            String ALTER_TABLE_ADD_CREATED_AT = "ALTER TABLE " + TABLE_INVENTORY_ITEMS +
                    " ADD COLUMN " + KEY_ITEM_CREATED_AT + " INTEGER NOT NULL DEFAULT " + System.currentTimeMillis();
            db.execSQL(ALTER_TABLE_ADD_CREATED_AT);
            Log.i(TAG, "Database upgraded: added created_at column");
        }

        if (oldVersion != newVersion) {
            Log.w(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
        }
    }

    // User Methods
    /**
     * Adds a new user to the database.
     *
     * @param username The username.
     * @param plainPassword The user's plaintext password.
     * @return true if the user was added successfully, false otherwise.
     */
    public boolean addUser(String username, String plainPassword) {
        SQLiteDatabase db = getWritableDatabase();
        long result = -1;

        // Hash the password
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_USERNAME, username);
            values.put(KEY_USER_PASSWORD, hashedPassword);
            values.put(KEY_USER_BUSINESS_NAME, "My Inventory"); // Default name
            values.put(KEY_USER_SMS_ENABLED, 0); // Default SMS disabled (false)
            values.put(KEY_USER_PHONE_NUMBER, "");

            result = db.insertOrThrow(TABLE_USERS, null, values);
            db.setTransactionSuccessful();
            Log.i(TAG, "User added successfully: " + username);
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to add user to database", e);
        } finally {
            db.endTransaction();
        }
        return result != -1;
    }

    /**
     * Checks if a user exists with the given username.
     *
     * @param username The username to check.
     * @return true if the user exists, false otherwise.
     */
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + KEY_USER_ID + " FROM " + TABLE_USERS +
                " WHERE " + KEY_USER_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    /**
     * Validates user credentials by comparing the hashed password.
     *
     * @param username The username attempting to log in.
     * @param plainPasswordAttempt The plain-text password entered by the user.
     * @return true if credentials are valid, false otherwise.
     */
    public boolean checkUserCredentials(String username, String plainPasswordAttempt) {
        SQLiteDatabase db = getReadableDatabase();
        String storedHash = null;
        String query = "SELECT " + KEY_USER_PASSWORD + " FROM " + TABLE_USERS +
                " WHERE " + KEY_USER_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        try {
            if (cursor.moveToFirst()) {
                storedHash = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PASSWORD));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving password hash for user: " + username, e);
            return false; // Error during retrieval
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        // If user not found or hash is null/empty, credentials are invalid
        if (storedHash == null || storedHash.isEmpty()) {
            Log.w(TAG, "No stored hash found for user: " + username);
            return false;
        }

        // Use BCrypt to check the password
        try {
            boolean passwordMatches = BCrypt.checkpw(plainPasswordAttempt, storedHash);
            Log.d(TAG, "Password check for " + username + ": " + passwordMatches);
            return passwordMatches;
        } catch (IllegalArgumentException e) {
            // This can happen if the stored hash is invalid/corrupted
            Log.e(TAG, "Error checking password (invalid hash format?) for user: " + username, e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error checking password for user: " + username, e);
            return false;
        }
    }

    /**
     * Retrieves user details and their settings by username.
     * Excludes the password.
     *
     * @param username The username to look up.
     * @return User object if found, null otherwise.
     */
    public User getUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        User user = null;
        String query = "SELECT " + KEY_USER_ID + ", " + KEY_USER_USERNAME + ", " +
                KEY_USER_BUSINESS_NAME + ", " + KEY_USER_SMS_ENABLED + ", " + KEY_USER_PHONE_NUMBER +
                " FROM " + TABLE_USERS + " WHERE " + KEY_USER_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        try {
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_USERNAME));
                String businessName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_BUSINESS_NAME));
                int smsEnabledInt = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_SMS_ENABLED));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PHONE_NUMBER));
                user = new User(id, name, businessName, smsEnabledInt == 1, phoneNumber);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user data for: " + username, e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return user;
    }

    /**
     * Retrieves user settings by user ID.
     *
     * @param userId The ID of the user.
     * @return User object containing settings (or null if user not found).
     */
    public User getUserSettings(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        User user = null;
        String query = "SELECT " + KEY_USER_ID + ", " + KEY_USER_USERNAME + ", " +
                KEY_USER_BUSINESS_NAME + ", " + KEY_USER_SMS_ENABLED + ", " + KEY_USER_PHONE_NUMBER +
                " FROM " + TABLE_USERS + " WHERE " + KEY_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_USERNAME));
                String businessName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_BUSINESS_NAME));
                int smsEnabledInt = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_SMS_ENABLED));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PHONE_NUMBER));
                user = new User(id, name, businessName, smsEnabledInt == 1, phoneNumber);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user settings for ID: " + userId, e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return user; // Returns null if user not found
    }

    /**
     * Updates the settings for a specific user.
     *
     * @param userId       The ID of the user to update.
     * @param businessName The new business name.
     * @param smsEnabled   The new SMS enabled status.
     * @return true if successful, false otherwise.
     */
    public boolean updateUserSettings(long userId, String businessName, boolean smsEnabled, String phoneNumber) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_BUSINESS_NAME, businessName);
        values.put(KEY_USER_SMS_ENABLED, smsEnabled ? 1 : 0);
        values.put(KEY_USER_PHONE_NUMBER, phoneNumber);

        int rowsAffected = 0;
        db.beginTransaction();
        try {
            rowsAffected = db.update(TABLE_USERS, values, KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            db.setTransactionSuccessful();
            Log.i(TAG, "User settings updated for user ID: " + userId + ". Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.e(TAG, "Error updating user settings for ID: " + userId, e);
            rowsAffected = 0;
        } finally {
            db.endTransaction();
        }
        return rowsAffected > 0;
    }

    /**
     * Updates only the SMS preference for a specific user.
     *
     * @param userId       The ID of the user to update.
     * @param smsEnabled   The new SMS enabled status.
     * @return true if successful, false otherwise.
     */
    public boolean updateUserSmsPreference(long userId, boolean smsEnabled) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_SMS_ENABLED, smsEnabled ? 1 : 0); // Store boolean as 0 or 1

        int rowsAffected = 0;
        db.beginTransaction();
        try {
            rowsAffected = db.update(TABLE_USERS, values, KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            db.setTransactionSuccessful();
            Log.i(TAG, "User SMS preference updated for user ID: " + userId + " to " + smsEnabled + ". Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.e(TAG, "Error updating user SMS preference for ID: " + userId, e);
            rowsAffected = 0;
        } finally {
            db.endTransaction();
        }
        return rowsAffected > 0;
    }

    // Inventory Item Methods

    /**
     * Adds a new inventory item associated with a specific user.
     *
     * @param item   The InventoryItem object to add.
     * @param userId The ID of the user who owns this item.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long addItem(InventoryItem item, long userId) {
        SQLiteDatabase db = getWritableDatabase();
        long itemId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_NAME, item.getName());
            values.put(KEY_ITEM_QUANTITY, item.getQuantity());
            values.put(KEY_ITEM_DESCRIPTION, item.getDescription());
            values.put(KEY_ITEM_BARCODE, item.getBarcode());
            values.put(KEY_ITEM_IMAGE_PATH, item.getImagePath()); // Store image path if available
            values.put(KEY_ITEM_USER_ID, userId);
            values.put(KEY_ITEM_CREATED_AT, item.getCreatedAt().getTime());

            itemId = db.insertOrThrow(TABLE_INVENTORY_ITEMS, null, values);
            db.setTransactionSuccessful();
            Log.i(TAG, "Item added successfully: ID " + itemId + ", Name: " + item.getName());
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to add item to database", e);
        } finally {
            db.endTransaction();
        }
        return itemId;
    }

    /**
     * Retrieves a single inventory item by its ID.
     *
     * @param itemId The ID of the item to retrieve.
     * @return The InventoryItem object, or null if not found.
     */
    public InventoryItem getItem(long itemId) {
        SQLiteDatabase db = getReadableDatabase();
        InventoryItem item = null;
        String SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE_INVENTORY_ITEMS, KEY_ITEM_ID);

        Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{String.valueOf(itemId)});
        try {
            if (cursor.moveToFirst()) {
                item = new InventoryItem();
                item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ITEM_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ITEM_NAME)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ITEM_QUANTITY)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ITEM_DESCRIPTION)));
                item.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ITEM_BARCODE)));
                item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ITEM_IMAGE_PATH)));

                // Handle created_at with fallback for existing data
                int createdAtIndex = cursor.getColumnIndex(KEY_ITEM_CREATED_AT);
                if (createdAtIndex != -1 && !cursor.isNull(createdAtIndex)) {
                    long timestamp = cursor.getLong(createdAtIndex);
                    item.setCreatedAt(new Date(timestamp));
                } else {
                    item.setCreatedAt(new Date()); // Fallback for existing data
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get item from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return item;
    }

    /**
     * Retrieves all inventory items for a specific user.
     *
     * @param userId The ID of the user whose items to retrieve.
     * @return A List of InventoryItem objects for that user.
     */
    public List<InventoryItem> getAllItems(long userId) {
        List<InventoryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = ?",
                TABLE_INVENTORY_ITEMS, KEY_ITEM_USER_ID);
        Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) {
                do {
                    InventoryItem item = new InventoryItem();
                    item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ITEM_ID)));
                    item.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ITEM_NAME)));
                    item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ITEM_QUANTITY)));
                    item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ITEM_DESCRIPTION)));
                    item.setBarcode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ITEM_BARCODE)));
                    item.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ITEM_IMAGE_PATH)));

                    // Handle created_at with fallback for existing data
                    int createdAtIndex = cursor.getColumnIndex(KEY_ITEM_CREATED_AT);
                    if (createdAtIndex != -1 && !cursor.isNull(createdAtIndex)) {
                        long timestamp = cursor.getLong(createdAtIndex);
                        item.setCreatedAt(new Date(timestamp));
                    } else {
                        item.setCreatedAt(new Date()); // Fallback for existing data
                    }

                    items.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get all items from database", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        Log.d(TAG, "Retrieved " + items.size() + " items from database.");
        return items;
    }

    /**
     * Updates an existing inventory item, ensuring it belongs to the correct user.
     *
     * @param item   The InventoryItem object with updated information (must have a valid ID).
     * @param userId The ID of the user attempting the update.
     * @return The number of rows affected (should be 1 if successful).
     */
    public int updateItem(InventoryItem item, long userId) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_NAME, item.getName());
        values.put(KEY_ITEM_QUANTITY, item.getQuantity());
        values.put(KEY_ITEM_DESCRIPTION, item.getDescription());
        values.put(KEY_ITEM_BARCODE, item.getBarcode());
        values.put(KEY_ITEM_IMAGE_PATH, item.getImagePath());

        // createdAt isn't updated here since it is meant to only capture the initial
        // creation time of the item.

        db.beginTransaction();
        try {
            rowsAffected = db.update(TABLE_INVENTORY_ITEMS, values,
                    KEY_ITEM_ID + " = ? AND " + KEY_ITEM_USER_ID + " = ?",
                    new String[]{String.valueOf(item.getId()), String.valueOf(userId)});
            db.setTransactionSuccessful();
            if (rowsAffected > 0) {
                Log.i(TAG, "Item updated successfully for user " + userId + ": ID " + item.getId());
            } else {
                Log.w(TAG, "Item update failed or item not found/owned by user " + userId + ": ID " + item.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to update item", e);
        } finally {
            db.endTransaction();
        }
        return rowsAffected;
    }

    /**
     * Deletes an inventory item, ensuring it belongs to the correct user.
     *
     * @param itemId The ID of the item to delete.
     * @param userId The ID of the user attempting the deletion.
     * @return The number of rows affected (should be 1 if successful).
     */
    public int deleteItem(long itemId, long userId) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        db.beginTransaction();
        try {
            rowsAffected = db.delete(TABLE_INVENTORY_ITEMS,
                    KEY_ITEM_ID + " = ? AND " + KEY_ITEM_USER_ID + " = ?",
                    new String[]{String.valueOf(itemId), String.valueOf(userId)});
            db.setTransactionSuccessful();
            if (rowsAffected > 0) {
                Log.i(TAG, "Item deleted successfully by user " + userId + ": ID " + itemId);
            } else {
                Log.w(TAG, "Item deletion failed or item not found/owned by user " + userId + ": ID " + itemId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to delete item", e);
        } finally {
            db.endTransaction();
        }
        return rowsAffected;
    }
}
