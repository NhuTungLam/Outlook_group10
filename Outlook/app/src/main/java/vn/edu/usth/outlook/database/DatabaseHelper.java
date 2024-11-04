package vn.edu.usth.outlook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final String TABLE_NAME = "user";
    private static final String COL_ID = "ID";
    private static final String COL_EMAIL = "EMAIL";
    private static final String COL_USERNAME = "USERNAME";
    private static final String COL_PHONE = "PHONE";
    private static final String COL_PASSWORD = "PASSWORD";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_USERNAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_PASSWORD + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert user into database
    public boolean insertUser(String email, String username, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PHONE, phone);
        contentValues.put(COL_PASSWORD, password); // Nên mã hóa mật khẩu
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close(); // Đóng db sau khi thực hiện
        return result != -1;
    }

    // Check if email already exists
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Đóng con trỏ sau khi sử dụng
        return exists;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Đóng con trỏ sau khi sử dụng
        return exists;
    }

    public boolean checkPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_PHONE + " = ?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Đóng con trỏ sau khi sử dụng
        return exists;
    }

    public boolean checkPasswordExists(String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_PASSWORD + " = ?", new String[]{password});
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Đóng con trỏ sau khi sử dụng
        return exists;
    }

    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        boolean isValid = cursor.getCount() > 0; // Kiểm tra nếu có bản ghi khớp
        cursor.close();
        return isValid;
    }

    public boolean hasUsers() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            // Check if table exist
            Cursor tableCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'", null);
            if (tableCursor != null) {
                boolean tableExists = tableCursor.getCount() > 0;
                tableCursor.close();
                if (!tableExists) {
                    return false; // Bảng chưa tồn tại
                }
            }

            // check if have user in table
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                cursor.close();
                return count > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}