package com.example.deliverable1fixed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A class representing the ClassInfo
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class ClassInfo extends AppCompatActivity implements View.OnClickListener{
    public Class selectedclass;
    public Button enroll;
    public String userID;
    public User user;
    private DatabaseReference referenceClassTypes;
    private DatabaseReference referenceClasses;
    private ArrayList<Class>screen;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_details);
        userID = getIntent().getExtras().getString("arg"); // passed from previous page
        referenceClassTypes = FirebaseDatabase.getInstance().getReference("ClassTypes");
        referenceClasses = FirebaseDatabase.getInstance().getReference("Classes");
        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        screen = new ArrayList<>();
        referenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassInfo.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
       // ArrayList<Class> hi = (user.getMyClasses());
        //int yp= userID.myClasses.size();
        getSelectedClass();
        setValues();

    }

    private void getSelectedClass() {
        Intent previousIntent = getIntent();
        String parsedStringID = previousIntent.getStringExtra("id");
        selectedclass = getParsedclass1(parsedStringID);
    }

    private Class getParsedclass1(String parsedID) {
        //No longer need
//        {
            //if(class1.instructor.getFullName().equals(parsedID))
       //         return class1;
       // }
        return null;
    }

    private void setValues(){
        TextView tv = (TextView) findViewById(R.id.classDet);
        //ImageView iv = (ImageView) findViewById(R.id.dbImage);

        tv.setText(selectedclass.getName());
        //iv.setImageResource(selectedclass1.getCapacity());
    }
    public void enrollingClick(View view) {
        userID = getIntent().getExtras().getString("arg");
        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        referenceUsers.child("myClasses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Class classObject = snapshot1.getValue(Class.class);
                    //if(classObject != null) {
                    screen.add(classObject);
                    //}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassInfo.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
        referenceUsers.child("myClasses").child(String.valueOf(user.getMyClasses().size())).setValue(selectedclass);
        user.addClass(selectedclass);

    }

    public void unenrollingClick(View view) {
        userID = getIntent().getExtras().getString("arg");
        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        referenceUsers.child("myClasses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Class classObject = snapshot1.getValue(Class.class);
                    //if(classObject != null) {
                    screen.add(classObject);
                    //}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassInfo.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
        int val= user.getMyClasses().indexOf(selectedclass);
        if (val!=-1){
            referenceUsers.child("myClasses").child(String.valueOf(val)).removeValue();
            user.getMyClasses().remove(val);
        }

    }
    @Override
    public void onClick(View view) {
        Intent intentView=null;
        switch (view.getId()) {
            case R.id.instructorBackMain:
                userID = getIntent().getExtras().getString("arg");
                DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                referenceUsers.child("myClasses").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Class classObject = snapshot1.getValue(Class.class);
                            //if(classObject != null) {
                            screen.add(classObject);
                            //}
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ClassInfo.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                });
                //Intent intentBackMain = new Intent(ClassInfo.this, NavClasses.class);
               // intentBackMain.putExtra("arg", userID);
               // startActivity(intentBackMain);
                break;
        }

    }

}