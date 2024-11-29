package com.plcoding.biometricauth.models.database;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.plcoding.biometricauth.R;
import com.plcoding.biometricauth.models.password.Password;
import com.plcoding.biometricauth.models.utils.EncryptionUtils;

import javax.crypto.SecretKey;

public class SavePassword extends AppCompatActivity {

    private EditText editPwdLogin, editPwdValue, editPwdName;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_password);

        initViews();
    }

    private void initViews() {
        editPwdLogin = findViewById(R.id.edit_pwd_login);
        editPwdValue = findViewById(R.id.edit_pwd_value);
        editPwdName = findViewById(R.id.edit_pwd_name);
        btnSave = findViewById(R.id.btn_save);

        // Get the generated password from the Intent (if any)
        String generatedPassword = getIntent().getStringExtra("pwd");
        if (generatedPassword != null) {
            editPwdValue.setText(generatedPassword);
        }

        btnSave.setOnClickListener(v -> {
            // Retrieve inputs
            String name = editPwdName.getText().toString().trim();
            String login = editPwdLogin.getText().toString().trim();
            String passwordValue = editPwdValue.getText().toString().trim();

            // Validate inputs
            if (name.isEmpty() || login.isEmpty() || passwordValue.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Encrypt and save the password
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(SavePassword.this);
                SecretKey secretKey = retrieveOrGenerateKey(SavePassword.this); // Retrieve or generate the key
                Log.d("SAVE_PASSWORD", "Encryption key retrieved successfully.");

                Password password = new Password(name, login, passwordValue);
                Log.d("SAVE_PASSWORD", "Password object created: " + password.toString());

                boolean saved = dbHelper.insert(password, secretKey);

                if (saved) {
                    Toast.makeText(this, "Password is saved successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("SAVE_PASSWORD", "Password saved successfully.");
                    btnSave.setEnabled(false);
                } else {
                    Toast.makeText(this, "Failed to save password. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("SAVE_PASSWORD", "Database insert operation returned false.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SAVE_PASSWORD", "Error occurred while saving password: " + e.getMessage());
                Toast.makeText(this, "An error occurred while saving the password", Toast.LENGTH_LONG).show();
            }
        });
    }

    private SecretKey retrieveOrGenerateKey(Context context) {
        try {
            // Check if the key exists in Android Keystore; if not, generate one
            return EncryptionUtils.generateKey(); // Replace this with your secure key retrieval logic
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate or retrieve encryption key");
        }
    }
}
