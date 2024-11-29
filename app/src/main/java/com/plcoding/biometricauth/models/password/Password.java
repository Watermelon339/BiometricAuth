package com.plcoding.biometricauth.models.password;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.plcoding.biometricauth.models.utils.EncryptionUtils;

import java.util.Date;

import javax.crypto.SecretKey;

public class Password {
    public Password(){

    }

    private int id;
    private String name, login, password;
    private Date updateDate;

    public Password(String name, String login, String password){

        this.name = name;
        this.login = login;
        this.password=password;
        this.updateDate = new Date();
    }

    @SuppressLint("Range")
    public static Password fromCursor(Cursor cursor, SecretKey secretKey) throws Exception {
        Password password = new Password();

        password.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        password.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        password.login = cursor.getString(cursor.getColumnIndexOrThrow("login"));

        // Retrieve and decode the encrypted password
        String encryptedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
        byte[] iv = Base64.decode(cursor.getString(cursor.getColumnIndexOrThrow("iv")), Base64.DEFAULT);
        password.password = EncryptionUtils.decrypt(encryptedPassword, secretKey, iv);

        password.updateDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("update_date")));

        return password;
    }

    @Override
    public String toString(){
        return "Password{" +
                "id= " + id +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", updateDate=" +updateDate +
                '}';
    }

    public ContentValues getContentValues(SecretKey secretKey) throws Exception {
        ContentValues values = new ContentValues();

        // Generate a random IV for this password
        byte[] iv = EncryptionUtils.generateIv();
        String encryptedPassword = EncryptionUtils.encrypt(password, secretKey, iv);

        Log.d("ENCRYPTION", "Encrypted Password: " + encryptedPassword);
        Log.d("ENCRYPTION", "Generated IV: " + Base64.encodeToString(iv, Base64.DEFAULT));

        values.put("name", name);
        values.put("login", login);
        values.put("password", encryptedPassword); // Save the encrypted password
        values.put("iv", Base64.encodeToString(iv, Base64.DEFAULT)); // Save IV as Base64
        values.put("update_date", updateDate.getTime());

        return values;
    }


    public int getName() {
        return 0;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
