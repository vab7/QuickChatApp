package com.belousov.quickchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;

    DatabaseReference databaseReference;

    ArrayList<Users> users;

    RecyclerView mainRecyclerView;

    UserAdapter userAdapter;

    ImageView log_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        }

        users = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("user");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    users.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        log_out = findViewById(R.id.log_out);
        mainRecyclerView = findViewById(R.id.main_recycler_view);

        userAdapter = new UserAdapter(users);
        mainRecyclerView.setAdapter(userAdapter);

        log_out.setOnClickListener(log_out -> {
            Dialog dialog = new Dialog(this, R.style.Dialog);
            dialog.setContentView(R.layout.dialog_layout);

            dialog.show();

            TextView yes, no;
            yes = dialog.findViewById(R.id.yes);
            no = dialog.findViewById(R.id.no);

            yes.setOnClickListener(y -> {
                FirebaseAuth.getInstance().signOut();
                dialog.dismiss();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            });

            no.setOnClickListener(n -> {
                dialog.dismiss();
            });
        });

    }
}