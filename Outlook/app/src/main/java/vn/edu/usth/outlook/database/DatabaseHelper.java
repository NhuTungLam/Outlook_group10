package vn.edu.usth.outlook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.outlook.Email_Sent;
import vn.edu.usth.outlook.Email_receiver;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database and Table names
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 3; // Tăng version để thực hiện nâng cấp

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
    private static final String COL_CONTENT = "content";
    private static final String COL_IS_DELETED = "is_deleted";
    private static final String COL_IS_ARCHIVED = "is_archived";
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
                COL_CONTENT + " TEXT, " +
                COL_IS_DELETED + " INTEGER DEFAULT 0, " +
                COL_IS_ARCHIVED + " INTEGER DEFAULT 0, " +
                COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COL_IS_READ + " INTEGER DEFAULT 0" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Kiểm tra và thêm cột is_deleted nếu chưa tồn tại
            if (!isColumnExists(db, TABLE_EMAIL, COL_IS_DELETED)) {
                db.execSQL("ALTER TABLE " + TABLE_EMAIL + " ADD COLUMN " + COL_IS_DELETED + " INTEGER DEFAULT 0");
            }

            // Kiểm tra và thêm cột is_archived nếu chưa tồn tại
            if (!isColumnExists(db, TABLE_EMAIL, COL_IS_ARCHIVED)) {
                db.execSQL("ALTER TABLE " + TABLE_EMAIL + " ADD COLUMN " + COL_IS_ARCHIVED + " INTEGER DEFAULT 0");
            }
            if (!isColumnExists(db, TABLE_EMAIL, COL_TIMESTAMP)) {
                db.execSQL("ALTER TABLE " + TABLE_EMAIL + " ADD COLUMN " + COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            }
            if (!isColumnExists(db, TABLE_EMAIL, COL_IS_READ)) {
                db.execSQL("ALTER TABLE " + TABLE_EMAIL + " ADD COLUMN " + COL_IS_READ + " INTEGER DEFAULT 0");
            }

        }
    }

    // Hàm kiểm tra sự tồn tại của cột trong bảng
    private boolean isColumnExists(SQLiteDatabase db, String tableName, String columnName) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        int columnIndex = cursor.getColumnIndex("name");
        while (cursor.moveToNext()) {
            if (cursor.getString(columnIndex).equals(columnName)) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
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

    // Get the email of a user based on their username
    public String getUserEmail(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_EMAIL + " FROM " + TABLE_USER + " WHERE " + COL_USERNAME + " = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            String email = cursor.getString(0);
            cursor.close();
            return email;
        } else {
            cursor.close();
            return null;
        }
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

    public boolean sendEmail(String sender, String receiver, String subject, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SENDER, sender);
        values.put(COL_RECEIVER, receiver);
        values.put(COL_SUBJECT, subject);
        values.put(COL_CONTENT, content);
        values.put(COL_TIMESTAMP, System.currentTimeMillis()); // Set the current time

        long result = db.insert(TABLE_EMAIL, null, values);
        db.close();

        return result != -1;
    }
    public boolean isEmailRead(int emailId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_IS_READ + " FROM " + TABLE_EMAIL + " WHERE " + COL_SENT_EMAIL_ID + " = ?", new String[]{String.valueOf(emailId)});
        boolean isRead = cursor.moveToFirst() && cursor.getInt(0) == 1;
        cursor.close();
        db.close();
        return isRead;
    }
    public long getEmailTimestamp(int emailId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_TIMESTAMP + " FROM " + TABLE_EMAIL + " WHERE " + COL_SENT_EMAIL_ID + " = ?", new String[]{String.valueOf(emailId)});

        long timestamp = 0;
        if (cursor.moveToFirst()) {
            timestamp = cursor.getLong(0);
        }
        cursor.close();
        db.close();

        return timestamp;
    }

    // Mark email as deleted
    public void markEmailAsDeleted(int emailId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_DELETED, 1);
        db.update(TABLE_EMAIL, values, COL_SENT_EMAIL_ID + " = ?", new String[]{String.valueOf(emailId)});
        db.close();
    }

    // Mark email as archived
    public void markEmailAsArchived(int emailId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_ARCHIVED, 1);
        db.update(TABLE_EMAIL, values, COL_SENT_EMAIL_ID + " = ?", new String[]{String.valueOf(emailId)});
        db.close();
    }

    public List<Email_receiver> getDeletedInboxEmails(String currentUser) {
        List<Email_receiver> deletedInboxEmails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM emails WHERE receiver = ? AND is_deleted = 1", new String[]{currentUser});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SENT_EMAIL_ID));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_SENDER));
                String receiver = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECEIVER));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
                deletedInboxEmails.add(new Email_receiver(id, sender, receiver, subject, content,timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return deletedInboxEmails;
    }

    public List<Email_Sent> getDeletedSentEmails(String currentUser) {
        List<Email_Sent> deletedSentEmails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM emails WHERE sender = ? AND is_deleted = 1", new String[]{currentUser});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SENT_EMAIL_ID));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_SENDER));
                String receiver = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECEIVER));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
                deletedSentEmails.add(new Email_Sent(id, sender, receiver, subject, content,timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return deletedSentEmails;
    }


    public List<Email_receiver> getArchivedInboxEmails(String currentUser) {
        List<Email_receiver> deletedInboxEmails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM emails WHERE receiver = ? AND is_archived = 1", new String[]{currentUser});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SENT_EMAIL_ID));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_SENDER));
                String receiver = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECEIVER));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
                deletedInboxEmails.add(new Email_receiver(id, sender, receiver, subject, content, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return deletedInboxEmails;
    }

    public List<Email_Sent> getArchivedSentEmails(String currentUser) {
        List<Email_Sent> deletedSentEmails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM emails WHERE sender = ? AND is_archived = 1", new String[]{currentUser});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SENT_EMAIL_ID));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_SENDER));
                String receiver = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECEIVER));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
                deletedSentEmails.add(new Email_Sent(id, sender, receiver, subject, content,timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return deletedSentEmails;
    }

    // Check if the user table has any users (for initial setup)
    public boolean hasUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USER, null);
        boolean hasUsers = cursor.moveToFirst() && cursor.getInt(0) > 0;
        cursor.close();
        return hasUsers;
    }
    public List<Email_Sent> getSentEmails(String senderEmail) {
        List<Email_Sent> emailList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to fetch sent emails where the sender matches the current user
        String query = "SELECT * FROM " + TABLE_EMAIL + " WHERE " + COL_SENDER + " = ? ORDER BY " + COL_SENT_EMAIL_ID + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{senderEmail});

        // Populate the emailList with email data
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SENT_EMAIL_ID));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_SENDER));
                String receiver = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECEIVER));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));

                // Create an Email_Sender object and add to list
                Email_Sent email = new Email_Sent(id ,sender, receiver, subject, content,timestamp);
                emailList.add(email);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return emailList;
    }

    public List<Email_receiver> getReceiveEmails(String receiverEmail) {
        List<Email_receiver> emailReceiveList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_EMAIL + " WHERE " + COL_RECEIVER + " = ? ORDER BY " + COL_SENT_EMAIL_ID + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{receiverEmail});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SENT_EMAIL_ID));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_SENDER));
                String receiver = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECEIVER));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBJECT));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));

                emailReceiveList.add(new Email_receiver(id, sender, receiver, subject, content, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return emailReceiveList;
    }

}
