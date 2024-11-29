package com.plcoding.biometricauth;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.plcoding.biometricauth.models.LowerCaseGenerator;
import com.plcoding.biometricauth.models.NumericGenerator;
import com.plcoding.biometricauth.models.UpperCaseGenerator;
import com.plcoding.biometricauth.models.SpecialCharGenerator;

public class PasswordGenerator extends AppCompatActivity {

    private EditText editPasswordSize;
    private TextView textPasswordGenerated,textErrorMessage;
    private CheckBox checkLower, checkUpper,checkSpecialChar, checkNumeric;
    private Button btnGenerate, btnCopy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_generator);

        initViews();
        clickListeners();
    }

    private void clickListeners() {
        btnGenerate.setOnClickListener(view -> {
            int passwordSize = Integer.parseInt(editPasswordSize.getText().toString());

            textErrorMessage.setText("");

            if(passwordSize<8){
                textErrorMessage.setText("Password Size must be greater than 8");
                return;
            }

            com.plcoding.biometricauth.models.PasswordGenerator.clear();
            if(checkLower.isChecked()) com.plcoding.biometricauth.models.PasswordGenerator.add(new LowerCaseGenerator());
            if(checkNumeric.isChecked()) com.plcoding.biometricauth.models.PasswordGenerator.add(new NumericGenerator());
            if(checkUpper.isChecked()) com.plcoding.biometricauth.models.PasswordGenerator.add(new UpperCaseGenerator());
            if(checkSpecialChar.isChecked()) com.plcoding.biometricauth.models.PasswordGenerator.add(new SpecialCharGenerator());


            if(com.plcoding.biometricauth.models.PasswordGenerator.isEmpty()){
                textErrorMessage.setText("Please select at least one password content type");
                return;
            }

            String password = com.plcoding.biometricauth.models.PasswordGenerator.generatePassword(passwordSize);
            textPasswordGenerated.setText(password);

        });

        btnCopy.setOnClickListener(view ->{
            ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            manager.setPrimaryClip(ClipData.newPlainText("password",textPasswordGenerated.getText().toString()));
            Toast.makeText(this, "Password Copied", Toast.LENGTH_SHORT).show();
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
    }
}