package com.example.deliverable1fixed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/**
 * A class representing the Admin page functionality and behavior
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class AdminMain extends AppCompatActivity implements View.OnClickListener{

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        Button classes = (Button) findViewById(R.id.classes);
        classes.setOnClickListener(this);

        Button accounts = (Button) findViewById(R.id.accounts);
        accounts.setOnClickListener(this);

        Button backBtn = (Button) findViewById(R.id.adminBackMain);
        backBtn.setOnClickListener(this);

        userID = getIntent().getExtras().getString("arg"); // passed from previous page
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        final TextView UsernameWTextView = (TextView) findViewById(R.id.adminUsername);
        final TextView TypeWTextView = (TextView) findViewById(R.id.adminUserType);
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    String username = userProfile.getUsername();
                    String type = userProfile.getType();
                    UsernameWTextView.setText("Username: " + username);
                    TypeWTextView.setText("Type: " + type);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminMain.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.classes:
                Intent intentClasses = new Intent(AdminMain.this, AdminClass.class);
                intentClasses.putExtra("arg", userID);
                startActivity(intentClasses);
                break;

            case R.id.accounts:
                Intent intentAccounts = new Intent(AdminMain.this, AdminAccounts.class);
                intentAccounts.putExtra("arg", userID);
                startActivity(intentAccounts);
                break;

            case R.id.adminBackMain:
                Intent intentBackMain = new Intent(AdminMain.this, HomeScreen.class);
                intentBackMain.putExtra("arg", userID);
                startActivity(intentBackMain);
                break;
        }
    }
}
