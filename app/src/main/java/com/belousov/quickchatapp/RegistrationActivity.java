package com.belousov.quickchatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    TextView sign_in, sign_up;
    EditText reg_name, reg_email, reg_password, reg_cPassword;
    CircleImageView circleImageView;

    ProgressDialog progressDialog;

    Uri img_uri;
    Users users;

    String name, email, password, cPassword, uid, img_URI, status;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    final int GET_ACTIVITY_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        sign_in = findViewById(R.id.sign_in);
        sign_up = findViewById(R.id.sign_up);
        reg_name = findViewById(R.id.reg_name);
        reg_email = findViewById(R.id.reg_email);
        reg_password = findViewById(R.id.reg_password);
        reg_cPassword = findViewById(R.id.reg_cPassword);
        circleImageView = findViewById(R.id.profile_img);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        sign_in.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        circleImageView.setOnClickListener(v -> { getImgFromStorage(); });

        sign_up.setOnClickListener(v -> {
            progressDialog.show();

            name = reg_name.getText().toString();
            email = reg_email.getText().toString();
            password = reg_password.getText().toString();
            cPassword = reg_cPassword.getText().toString();

            if (
                    TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)
            ) {
                progressDialog.dismiss();

                if (TextUtils.isEmpty(name)) reg_name.setError("Enter Name");
                if (TextUtils.isEmpty(email)) reg_email.setError("Enter Email");
                if (TextUtils.isEmpty(password)) reg_password.setError("Enter Password");
                if (TextUtils.isEmpty(cPassword)) reg_cPassword.setError("Enter Password");
            } else if (!email.matches(emailPattern)) {
                progressDialog.dismiss();
                reg_email.setError("Enter Correct Email");
            } else if (password.length() < 6) {
                progressDialog.dismiss();
                reg_password.setError("Enter 6 Character Password");
            } else if (!password.equals(cPassword)) {
                progressDialog.dismiss();
                reg_cPassword.setError("Enter The Same Password");
            } else if (img_uri == null) {
                progressDialog.dismiss();
                getImgFromStorage();
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(auth -> {
                    if (auth.isSuccessful()) {
                        uid = this.auth.getUid();
                        databaseReference = database.getReference().child("user").child(uid);
                        storageReference = storage.getReference().child("upload").child(uid);

                        if (img_uri != null) {
                            storageReference.putFile(img_uri).addOnCompleteListener(file -> {
                                if (file.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        img_URI = uri.toString();
                                        status = "I'm using this application";
                                        users = new Users(uid, name, email, img_URI, status);

                                        databaseReference.setValue(users).addOnCompleteListener(users -> {
                                            if (users.isSuccessful()) {
                                                progressDialog.dismiss();

                                                Intent intent = new Intent(
                                                        this,
                                                        HomeActivity.class
                                                );
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                error();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    });
                                } else {
                                    error();
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            error();
                            progressDialog.dismiss();
                        }
                    } else {
                        progressDialog.dismiss();
                        error();
                    }
                });
            }
        });

    }

    private void getImgFromStorage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GET_ACTIVITY_GALLERY);
    }

    private void error() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_ACTIVITY_GALLERY) {
            if (data != null) {
                img_uri = data.getData();
                circleImageView.setImageURI(img_uri);
            }
        }
    }
}