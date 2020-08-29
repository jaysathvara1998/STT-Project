package com.exapmle.sttproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView tvRegister;
    TextInputEditText etUserName;
    TextInputEditText etPassword;
    TextInputLayout txtUserName;
    TextInputLayout txtPassword;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("User");
    ArrayList<UserModel> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        txtUserName = findViewById(R.id.txtUserName);
        txtPassword = findViewById(R.id.txtPassword);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Login", "Value is: " + dataSnapshot.getValue());
                Log.e("Login", "DataSnapshot Key: " + dataSnapshot.getKey());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    myRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HashMap<String, Object> value = (HashMap<String, Object>) snapshot.getValue();

                            String email = value.get("email").toString();
                            String password = value.get("password").toString();
                            String name = value.get("name").toString();
                            String id = value.get("id").toString();
                            UserModel model = new UserModel(name, email, password, id);
                            userList.add(model);
                            Log.e("Login", "Email: " + email + " Password: " + password);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Login", "Failed to read value.", error.toException());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (validate()) {
                    if (!userList.isEmpty()) {
                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i).email.equals(etUserName.getText().toString()) && userList.get(i).password.equals(etPassword.getText().toString())) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                return;
                            }
                        }
                    }
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Boolean validate() {
        if (Objects.requireNonNull(etUserName.getText()).toString().isEmpty()) {
            txtUserName.setError("Enter Username");
            txtPassword.setError("");
            return false;
        }

        if (Objects.requireNonNull(etPassword.getText()).toString().isEmpty()) {
            txtPassword.setError("Enter Password");
            txtUserName.setError("");
            return false;
        }

        return true;
    }
}