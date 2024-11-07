package vn.edu.usth.outlook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database and Table names
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 2;

    // User Table
    private static final String TABLE_USER = "user";
    private static final String COL_USER_ID = "ID";
    private static final String COL_EMAIL = "EMAIL";
    private static final String COL_USERNAME = "USERNAME";
    private static final String COL_PHONE = "PHONE";
    private static final String COL_PASSWORD = "PASSWORD";

    // Email Table
    private static final String TABLE_EMAIL = "emails";
    private static final String COL_SENT_EMAIL_ID = "sent_email_id";
    private static final String COL_SENDER = "sender";
    private static final String COL_RECEIVER = "receiver";
    private static final String COL_SUBJECT = "subject";
    private static final String COL_Content = "content";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_IS_READ = "is_read";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User Table
        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_USERNAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_PASSWORD + " TEXT" +
                ")");

        // Create Email Table
        db.execSQL("CREATE TABLE " + TABLE_EMAIL + " (" +
                COL_SENT_EMAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SENDER + " TEXT, " +
                COL_RECEIVER + " TEXT, " +
                COL_SUBJECT + " TEXT, " +
                COL_Content + " TEXT, " +
                COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COL_IS_READ + " INTEGER DEFAULT 0" +  // 0 = unread, 1 = read
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAIL);
        onCreate(db);
    }

    // Insert new user into the User table
    public boolean insertUser(String email, String username, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PHONE, phone);
        contentValues.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USER, null, contentValues);
        db.close();
        return result != -1;
    }

    // Check if email already exists
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if username already exists
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if phone already exists
    public boolean checkPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_PHONE + " = ?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Verify user credentials for login
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // Send an email
    public boolean sendEmail(String sender, String receiver, String subject, String content) {
        ContentValues values = new ContentValues();
        values.put("sender", sender);
        values.put("receiver", receiver);
        values.put("subject", subject);
        values.put("content", content);

        long newRowId = getWritableDatabase().insert("emails", null, values);
        return newRowId != -1; // Return true if insertion was successful
    }

    // Retrieve emails for a specific user (inbox)
    public Cursor getUserEmails(String receiver) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_EMAIL +
                " WHERE " + COL_RECEIVER + " = ?" +
                " ORDER BY " + COL_TIMESTAMP + " DESC", new String[]{receiver});
    }

    // Mark email as read
    public boolean markEmailAsRead(int emailId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_IS_READ, 1); // 1 indicates read
        int result = db.update(TABLE_EMAIL, contentValues, COL_SENT_EMAIL_ID + " = ?", new String[]{String.valueOf(emailId)});
        db.close();
        return result > 0;
    }

    // Check if the user table has any users (for initial setup)
    public boolean hasUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USER, null);
        boolean hasUsers = cursor.moveToFirst() && cursor.getInt(0) > 0;
        cursor.close();
        return hasUsers;
    }
}
