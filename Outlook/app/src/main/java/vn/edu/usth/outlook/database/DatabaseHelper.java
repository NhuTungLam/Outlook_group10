package vn.edu.usth.outlook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import vn.edu.usth.outlook.Email_Sent;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 2;

    // User table
    private static final String TABLE_NAME = "user";
    private static final String COL_ID = "ID";
    private static final String COL_EMAIL = "EMAIL";
    private static final String COL_USERNAME = "USERNAME";
    private static final String COL_PHONE = "PHONE";
    private static final String COL_PASSWORD = "PASSWORD";

    // Email table
    private static final String TABLE_EMAIL = "emails";
    private static final String COL_SENT_EMAIL_ID = "sent_email_id";
    private static final String COL_SENDER = "sender";
    private static final String COL_RECEIVER = "receiver";
    private static final String COL_SUBJECT = "subject";
    private static final String COL_CONTENT = "content";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_IS_READ = "is_read";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create user table
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_USERNAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_PASSWORD + " TEXT" +
                ")");

        // Create emails table
        db.execSQL("CREATE TABLE " + TABLE_EMAIL + " (" +
                COL_SENT_EMAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SENDER + " TEXT, " +
                COL_RECEIVER + " TEXT, " +
                COL_SUBJECT + " TEXT, " +
                COL_CONTENT + " TEXT, " +
                COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COL_IS_READ + " INTEGER DEFAULT 0" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAIL);
        onCreate(db);
    }

    // Insert user into database
    public boolean insertUser(String email, String username, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PHONE, phone);
        contentValues.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    // Check if email already exists
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_PHONE + " = ?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public boolean hasUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        boolean hasUsers = false;
        if (cursor.moveToFirst()) {
            hasUsers = cursor.getInt(0) > 0;
        }
        cursor.close();
        return hasUsers;
    }

    // Send an email
    public boolean sendEmail(String sender, String receiver, String subject, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SENDER, sender);
        values.put(COL_RECEIVER, receiver);
        values.put(COL_SUBJECT, subject);
        values.put(COL_CONTENT, content);
        long result = db.insert(TABLE_EMAIL, null, values);
        db.close();
        return result != -1;
    }

    // Get list of sent emails for a specific sender
    public List<Email_Sent> getSentEmails(String senderEmail) {
        List<Email_Sent> emailList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_EMAIL + " WHERE " + COL_SENDER + " = ? ORDER BY " + COL_TIMESTAMP + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{senderEmail});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SENT_EMAIL_ID));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_SENDER));
                String receiver = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECEIVER));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                
                Email_Sent email = new Email_Sent(id, sender, receiver, subject, content, timestamp);
                emailList.add(email);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return emailList;
    }
}