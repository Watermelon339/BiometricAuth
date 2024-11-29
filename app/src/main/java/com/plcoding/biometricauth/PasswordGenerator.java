package com.plcoding.biometricauth;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.plcoding.biometricauth.models.database.DatabaseHelper;
import com.plcoding.biometricauth.models.database.SavePassword;
import com.plcoding.biometricauth.models.generators.LowerCaseGenerator;
import com.plcoding.biometricauth.models.generators.NumericGenerator;
import com.plcoding.biometricauth.models.generators.UpperCaseGenerator;
import com.plcoding.biometricauth.models.generators.SpecialCharGenerator;
import com.plcoding.biometricauth.models.password.Password;
import com.plcoding.biometricauth.models.password.PasswordAdapter;
import com.plcoding.biometricauth.models.utils.EncryptionUtils;

import java.util.List;

import javax.crypto.SecretKey;

public class PasswordGenerator extends AppCompatActivity {

    private EditText editPasswordSize;
    private TextView textPasswordGenerated,textErrorMessage;
    private CheckBox checkLower, checkUpper,checkSpecialChar, checkNumeric;
    private Button btnGenerate, btnCopy, btnSave;

    private RecyclerView reclyclerSavedPasswords;
    private PasswordAdapter passwordAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_generator);

        initViews();
        clickListeners();
        displaySavedPasswords(); //TODO: TO BE REMOVED LATER
    }

    private void displaySavedPasswords() {
        try {
            // Retrieve or generate the SecretKey
            SecretKey secretKey = retrieveOrGenerateKey();

            DatabaseHelper db = new DatabaseHelper(PasswordGenerator.this);
            List<Password> passwordList = db.getPasswordList(secretKey);
            Log.e("PWD_LIST", passwordList.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PWD_LIST", "Error retrieving saved passwords: " + e.getMessage());
        }
    }

    private SecretKey retrieveOrGenerateKey() {
        try {
            // Generate or retrieve the key
            return EncryptionUtils.generateKey(); // Use your secure key management logic
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("KEY_ERROR", "Failed to generate or retrieve encryption key: " + e.getMessage());
            throw new RuntimeException("Failed to generate or retrieve encryption key");
        }
    }

    private void clickListeners() {
        btnGenerate.setOnClickListener(view -> {
            int passwordSize = Integer.parseInt(editPasswordSize.getText().toString());

            textErrorMessage.setText("");

            if(passwordSize<8){
                textErrorMessage.setText("Password Size must be greater than 8");
                return;
            }

            com.plcoding.biometricauth.models.generators.PasswordGenerator.clear();
            if(checkLower.isChecked()) com.plcoding.biometricauth.models.generators.PasswordGenerator.add(new LowerCaseGenerator());
            if(checkNumeric.isChecked()) com.plcoding.biometricauth.models.generators.PasswordGenerator.add(new NumericGenerator());
            if(checkUpper.isChecked()) com.plcoding.biometricauth.models.generators.PasswordGenerator.add(new UpperCaseGenerator());
            if(checkSpecialChar.isChecked()) com.plcoding.biometricauth.models.generators.PasswordGenerator.add(new SpecialCharGenerator());


            if(com.plcoding.biometricauth.models.generators.PasswordGenerator.isEmpty()){
                textErrorMessage.setText("Please select at least one password content type");
                return;
            }

            String password = com.plcoding.biometricauth.models.generators.PasswordGenerator.generatePassword(passwordSize);
            textPasswordGenerated.setText(password);

            btnSave.setEnabled(true);

        });

        btnCopy.setOnClickListener(view ->{
            ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            manager.setPrimaryClip(ClipData.newPlainText("password",textPasswordGenerated.getText().toString()));
            Toast.makeText(this, "Password Copied", Toast.LENGTH_SHORT).show();
        });

        btnSave.setOnClickListener(view ->{
            String genPwd = textPasswordGenerated.getText().toString();
            Intent intent = new Intent(PasswordGenerator.this, SavePassword.class);
            intent.putExtra("pwd",genPwd);
            startActivity(intent);
        });
    }

    private void initViews(){
        editPasswordSize = findViewById(R.id.edit_pwd_size);
        textPasswordGenerated = findViewById(R.id.text_password_result);
        textErrorMessage = findViewById(R.id.text_error);
        checkLower = findViewById(R.id.check_lower);
        checkUpper = findViewById(R.id.check_upper);
        checkSpecialChar = findViewById(R.id.check_special_char);
        checkNumeric = findViewById(R.id.check_numeric);
        btnGenerate = findViewById(R.id.btn_generate);
        btnCopy = findViewById(R.id.btn_copy);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setEnabled(false);

        reclyclerSavedPasswords = findViewById(R.id.recycler_saved_passwords);
        reclyclerSavedPasswords.setLayoutManager(new LinearLayoutManager(this));
    }
}