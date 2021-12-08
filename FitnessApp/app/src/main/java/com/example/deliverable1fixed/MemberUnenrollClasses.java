package com.example.deliverable1fixed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
 * A class representing the 'deleting' classes
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class MemberUnenrollClasses extends AppCompatActivity implements View.OnClickListener{

    private String userID;
    private User user;

    private Spinner classesunenrollSpinner;
    private String selectedClassunenroll;
    private String selectedViewClass;
    private ArrayList<String> classesunenrollList;
    private ArrayList<String> viewmyClassesList;
    private Spinner viewmyClasses;

    private Hashtable<String, String> classesMap;
    private Hashtable<String, Class> classesMap1;
    private TextView details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_member_unenroll_classes);

        userID = getIntent().getExtras().getString("arg"); // passed from previous page

        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        referenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MemberUnenrollClasses.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });

        classesunenrollSpinner = (Spinner) findViewById(R.id.myunenrollClassToTeachSpinner);
        classesunenrollList = new ArrayList<>();
        classesunenrollList.add(0, "Select a class to Unenroll");

        viewmyClasses = (Spinner) findViewById(R.id.myClassesSpinner);
        viewmyClassesList = new ArrayList<>();
        viewmyClassesList.add(0, "Select a class to View More Details");

        details = (TextView) findViewById(R.id.classDetails);
        classesMap = new Hashtable<String, String>();
        classesMap1 = new Hashtable<String, Class>();

        pullClassesData();
        initializeUnenrollSpinnerDropdown();
        initializeViewSpinnerDropdown();
    }

    
    
    /** Pulls Classes data from realtime database */
    private void pullClassesData() {
        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        referenceUsers.child("myClasses").addListenerForSingleValueEvent(new ValueEventListener() {
        //referenceClasses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Class classObject = snapshot.getValue(Class.class);
                    if(classObject != null) {
                        String testIfCancelled = classObject.day;
                        String n = classObject.name;
                        if (!(n.equals("Onboarding"))) {
                            if (testIfCancelled.equals("N/A")) {
                                String uID = snapshot.getKey();
                                String classDescription = classObject.name + "-" + "(cancelled)";
                                if (!(classesunenrollList.contains(classDescription))) {
                                    classesunenrollList.add(classDescription);
                                    viewmyClassesList.add(classDescription);
                                    classesMap.put(classDescription, uID);
                                    classesMap1.put(classDescription, classObject);
                                }
                            } else {
                                String uID = snapshot.getKey();
                                String classDescription = classObject.name + "-" + classObject.day + "'s : " + classObject.timeInterval;
                                if (!(classesunenrollList.contains(classDescription))) {
                                    classesunenrollList.add(classDescription);
                                    viewmyClassesList.add(classDescription);
                                    classesMap.put(classDescription, uID);
                                    classesMap1.put(classDescription, classObject);
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MemberUnenrollClasses.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Initializes unenrollClass spinner dropdown adapter. And instantiates OnClick item listener. */
    private void initializeUnenrollSpinnerDropdown() {
        ArrayAdapter<String> classesAdapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, classesunenrollList);
        classesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classesunenrollSpinner.setAdapter(classesAdapter);
        classesunenrollSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a class to Unenroll"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedClassunenroll = item;
                } else {
                    selectedClassunenroll = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MemberUnenrollClasses.this, "Select a class to unenroll", Toast.LENGTH_LONG).show();
                selectedClassunenroll = "";
            }
        });
    }

    /** Initializes cancelClass spinner dropdown adapter. And instantiates OnClick item listener. */
    private void initializeViewSpinnerDropdown() {
        ArrayAdapter<String> classesAdapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, viewmyClassesList);
        classesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewmyClasses.setAdapter(classesAdapter);
        viewmyClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a class to View More Details"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedViewClass = item;
                    details.setVisibility(View.GONE);
                } else {
                    selectedViewClass = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MemberUnenrollClasses.this, "Select a class to view", Toast.LENGTH_LONG).show();
                selectedViewClass = "";
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.mycancelClassToTeachBtn:
            case R.id.viewClassDetailBtn:
                viewClassDetails();
                break;
                //cancelClass();
                //break;
            case R.id.myunenrollClassToTeachBtn:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm unenrollment");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        unenrollClass();
                        dialog.dismiss(); }});
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }});

                AlertDialog alert = builder.create();
                alert.show();

                break;
            case R.id.myhomeBtn:
                Intent intentView = new Intent(MemberUnenrollClasses.this, MemberMain.class);
                intentView.putExtra("arg", userID);
                startActivity(intentView);
                break;
        }
    }

    private void pullClassData(String classSelected) {
        ArrayList<String> membersDesc = new ArrayList<String>();
        String output;
        Class  key = classesMap1.get(classSelected);
        if (key != null) {
            if (key.getInstructor()==(null)){
                details.setText(key.getDifficultyLevel() + "-" + key.getName() + "\n" + key.getDay() + "'s at " + key.getTimeInterval() + "\n (" + key.getCapacity() + " spots left)");
            }else {
                details.setText(key.getDifficultyLevel() + "-" + key.getName() + "\n" + key.getDay() + "'s at " + key.getTimeInterval() + "\nTaught by " + key.getInstructor().getFullName() + "\n (" + key.getCapacity() + " spots left)");
                details.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(MemberUnenrollClasses.this, "This class currently has no published details", Toast.LENGTH_LONG).show();
            details.setVisibility(View.GONE);

        }
        // return membersDesc;
    }

    /** Displays ListView of members enrolled in the selected class. */
    private void viewClassDetails() {
        if (!(selectedViewClass.equals(""))) {
            pullClassData(selectedViewClass);
            //setUpList(pullMembersData(selectedClass));
        } else {
            Toast.makeText(MemberUnenrollClasses.this, "Please select a class to view its description", Toast.LENGTH_LONG).show();
            details.setVisibility(View.GONE);
        }
    }

    /** unenrolls the selected existing class permanently from the realtime database.
     * Refreshes the data initially pulled, the ArrayLists/Hashtable holding data and re-initializes the Spinners onSuccess. */
    private void unenrollClass() {
        if (selectedClassunenroll.equals("")) {
            Toast.makeText(MemberUnenrollClasses.this, "Select a class to unenroll", Toast.LENGTH_SHORT).show();
        } else {
            Class key = classesMap1.get(selectedClassunenroll);
            DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users").child(userID);
            if (key != null) {
                referenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String className = key.getName();
                        int val=user.removeClass(key);
                        if (val!=-1) {
                            referenceUsers.child("myClasses").child(String.valueOf(val)).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(MemberUnenrollClasses.this,
                                                    "Class unenrolled", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                        //user.getMyClasses().remove(val);
                        viewmyClassesList.clear();
                        viewmyClassesList.add(0, "Select a class to View More Details");
                        classesunenrollList.clear();
                        classesunenrollList.add(0, "Select a class to Unenroll");
                        classesMap.clear();
                        classesMap1.clear();
                        pullClassesData();
                        initializeUnenrollSpinnerDropdown();
                        initializeViewSpinnerDropdown();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MemberUnenrollClasses.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

    }

}

