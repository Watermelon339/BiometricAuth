package com.plcoding.biometricauth.models.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.plcoding.biometricauth.models.password.Password;
import com.plcoding.biometricauth.models.utils.EncryptionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "PASSWORDS";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Updated schema to include IV column
        db.execSQL(
                "CREATE TABLE PASSWORDS (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, " +
                        "password TEXT, " +
                        "login TEXT, " +
                        "update_date DATE, " +
                        "iv TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PASSWORDS");
        onCreate(db);
    }

    public boolean insert(Password password, SecretKey secretKey) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = password.getContentValues(secretKey);
            return db.insert("PASSWORDS", null, values) != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Password> getPasswordList(SecretKey secretKey) {
        List<Password> passwordList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM PASSWORDS", null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Password pwd = Password.fromCursor(cursor, secretKey);
                    passwordList.add(pwd);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return passwordList;
    }
}
