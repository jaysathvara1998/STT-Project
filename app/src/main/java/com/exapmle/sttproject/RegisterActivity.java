package com.exapmle.sttproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    TextView tvLogin;
    TextInputEditText etUserName;
    TextInputEditText etName;
    TextInputEditText etPassword;
    TextInputLayout txtUserName;
    TextInputLayout txtName;
    TextInputLayout txtPassword;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Constants.USER);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        etUserName = findViewById(R.id.etUserName);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        txtUserName = findViewById(R.id.txtUserName);
        txtName = findViewById(R.id.txtName);
        txtPassword = findViewById(R.id.txtPassword);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    String key = myRef.push().getKey();
                    UserModel userModel = new UserModel(etName.getText().toString(), etUserName.getText().toString(), etPassword.getText().toString(), key);
                    myRef.child(key).setValue(userModel);

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra(Constants.USER, userModel);
                    startActivity(intent);

//                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    Toast.makeText(getApplicationContext(), "Registration Success", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validate() {
        if (etName.getText().toString().isEmpty()) {
            etName.setError("Enter Name");
            return false;
        }

        if (etUserName.getText().toString().isEmpty()) {
            etUserName.setError("Enter UserName");
            return false;
        }

        if (etPassword.getText().toString().isEmpty()) {
            etPassword.setError("Enter Password");
            return false;
        }

        return true;
    }
}