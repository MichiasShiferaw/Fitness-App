package com.example.deliverable1fixed;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Hashtable;
/**
 * A class representing the Admin account functionality and behavior
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class AdminAccounts extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference reference;
    private String userID;

    private Hashtable<String, String> users;

    private Spinner instructorsDropdown;
    private String selectedInstructorEmail;

    private Spinner memberDropdown;
    private String selectedMemberEmail;

    private ArrayList<String> instructorEmails; // data showed in the instructor account spinner
    private ArrayList<String> memberEmails; // data showed in the member account spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_admin_manage_accounts);

        userID = getIntent().getExtras().getString("arg"); // passed from previous page
        reference = FirebaseDatabase.getInstance().getReference("Users");

        Button homeButton = (Button) findViewById(R.id.home);
        homeButton.setOnClickListener(this);

        Button deleteInstructorsButton = (Button) findViewById(R.id.deleteInstructorBtn);
        deleteInstructorsButton.setOnClickListener(this);

        Button deleteMembersButton = (Button) findViewById(R.id.deleteMemberBtn);
        deleteMembersButton.setOnClickListener(this);

        instructorsDropdown = (Spinner) findViewById(R.id.manageInstructorsSpinner);
        memberDropdown = (Spinner) findViewById(R.id.manageMembersSpinner);

        users = new Hashtable<String, String>();

        instructorEmails = new ArrayList<>();
        instructorEmails.add(0, "Select instructor");

        memberEmails = new ArrayList<>();
        memberEmails.add(0, "Select member");

        getUserData();
        initializeInstructorDropdown();
        initializeMemberDropdown();
    }

    /** Pulls user data from realtime database. */
    private void getUserData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String type = snapshot.child("type").getValue(String.class);
                    String uID = snapshot.getKey();
                    String email = snapshot.child("email").getValue(String.class);
                    String fullName = snapshot.child("fullName").getValue(String.class);

                    if (type != null && uID != null && email!= null) {
                        String uSpinnerInfo = fullName + ": " + email;
                        if (!(instructorEmails.contains(email) || memberEmails.contains(email))) {

                            if (type.equals("Instructor")) {
                                instructorEmails.add(uSpinnerInfo);
                            }

                            if (type.equals("Member")) {
                                memberEmails.add(uSpinnerInfo);
                            }

                            if ((type.equals("Instructor") || type.equals("Member"))) {
                                users.put(uSpinnerInfo, uID);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminAccounts.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Initializes the instructor dropdown spinner adapter. And instantiates on click item listener. */
    public void initializeInstructorDropdown() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, instructorEmails);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructorsDropdown.setAdapter(adapter);

        instructorsDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select instructor"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedInstructorEmail = item;
                } else {
                    selectedInstructorEmail = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AdminAccounts.this, "Select instructor", Toast.LENGTH_LONG).show();
                selectedInstructorEmail = "";
            }
        });
    }

    /** Initializes the member dropdown spinner adapter. And instantiates on click item listener. */
    public void initializeMemberDropdown() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, memberEmails);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberDropdown.setAdapter(adapter);

        memberDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select member"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedMemberEmail = item;
                } else {
                    selectedMemberEmail = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AdminAccounts.this, "Select member", Toast.LENGTH_LONG).show();
                selectedMemberEmail = "";
            }
        });
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.home:
                Intent intent = new Intent(AdminAccounts.this, AdminMain.class);
                intent.putExtra("arg", userID);
                startActivity(intent);
                break;
            case R.id.deleteInstructorBtn:
                deleteInstructor();
                break;
            case R.id.deleteMemberBtn:
                deleteMember();
                break;
        }
    }

    /** Deletes instructors from the realtime database.
     * Refreshes the data pulled, the ArrayLists/HashMaps holding data and re-initializes the Spinners onSuccess*/
    public void deleteInstructor() {
        if (selectedInstructorEmail.equals("")) {
            Toast.makeText(AdminAccounts.this, "Select an instructor to delete", Toast.LENGTH_SHORT).show();
        } else {
            String key = users.get(selectedInstructorEmail);
            if (key != null) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AdminAccounts.this, "Instructor deleted", Toast.LENGTH_SHORT).show();
                                instructorEmails.clear();
                                instructorEmails.add(0, "Select instructor");
                                memberEmails.clear();
                                memberEmails.add(0, "Select member");
                                users.clear();
                                getUserData();
                                initializeInstructorDropdown();
                                initializeMemberDropdown();
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminAccounts.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    /** Deletes instructors from the realtime database.
     * Refreshes the data pulled, the ArrayLists/HashMaps holding data and re-initializes the Spinners onSuccess*/
    public void deleteMember() {
        if (selectedMemberEmail.equals("")) {
            Toast.makeText(AdminAccounts.this, "Select a member to delete", Toast.LENGTH_SHORT).show();
        } else {
            String key = users.get(selectedMemberEmail);
            if (key != null) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AdminAccounts.this, "Member deleted", Toast.LENGTH_SHORT).show();
                                instructorEmails.clear();
                                instructorEmails.add(0, "Select instructor");
                                memberEmails.clear();
                                memberEmails.add(0, "Select member");
                                users.clear();
                                getUserData();
                                initializeInstructorDropdown();
                                initializeMemberDropdown();
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminAccounts.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}