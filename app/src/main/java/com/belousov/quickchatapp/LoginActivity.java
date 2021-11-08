package com.belousov.quickchatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView sign_up, sign_in;
    EditText login_email, login_password;

    FirebaseAuth auth;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sign_up = findViewById(R.id.sing_up);
        sign_in = findViewById(R.id.sign_up);
        login_email = findViewById(R.id.reg_email);
        login_password = findViewById(R.id.reg_password);

        sign_in.setOnClickListener(v -> {
            String email = login_email.getText().toString();
            String password = login_password.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter Valid Data", Toast.LENGTH_SHORT).show();
            } else if (!email.matches(emailPattern)) {
                login_email.setError("Not valid Email");
                Toast.makeText(this, "Not valid Email", Toast.LENGTH_SHORT).show();
            } else if (password.length() > 6) {
                login_password.setError("Not valid password");
                Toast.makeText(this, "Please enter valid password", Toast.LENGTH_SHORT).show();
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Error in login", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        sign_up = findViewById(R.id.sing_up);
        sign_up.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        });

    }
}