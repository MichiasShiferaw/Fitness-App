package com.example.deliverable1fixed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class MemberMain extends AppCompatActivity implements View.OnClickListener{


    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        LoadingScreen load= new LoadingScreen();
        setContentView(R.layout.activity_member_main);

        userID = getIntent().getExtras().getString("arg"); // passed from previous page
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        Button backBtn = (Button) findViewById(R.id.memberBackMain);
        backBtn.setOnClickListener(this);

        Button searchClasses = (Button) findViewById(R.id.memberSearchClassesBtn);
        searchClasses.setOnClickListener(this);

        Button viewMyClass = (Button) findViewById(R.id.memberMyViewClassBtn);
        viewMyClass.setOnClickListener(this);

        Button unenrollClasses = (Button) findViewById(R.id.memberUnenrollClassesBtn);
        unenrollClasses.setOnClickListener(this);

        final TextView UsernameWTextView = (TextView) findViewById(R.id.memberUsername);
        final TextView TypeWTextView = (TextView) findViewById(R.id.memberUserType);

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
                Toast.makeText(MemberMain.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.memberSearchClassesBtn:
                Intent intentSearch = new Intent(MemberMain.this, MemberSearchClasses.class);
                intentSearch.putExtra("arg", userID);
                startActivity(intentSearch);
                break;
            case R.id.memberMyViewClassBtn:
                Intent intentViewMembers = new Intent(MemberMain.this, MemberViewClass.class);
                intentViewMembers.putExtra("arg", userID);
                startActivity(intentViewMembers);
                break;
            case R.id.memberUnenrollClassesBtn:
                Intent intentDelete = new Intent(MemberMain.this, MemberUnenrollClasses.class);
                intentDelete.putExtra("arg", userID);
                startActivity(intentDelete);
                break;
            case R.id.memberBackMain:
                Intent intentBackMain = new Intent(MemberMain.this, HomeScreen.class);
                intentBackMain.putExtra("arg", userID);
                startActivity(intentBackMain);
                break;
        }
    }

}