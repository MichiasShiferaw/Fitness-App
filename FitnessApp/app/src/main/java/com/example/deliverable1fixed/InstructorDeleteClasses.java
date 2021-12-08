package com.example.deliverable1fixed;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.Set;

/**
 * A class representing the 'deleting' classes
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class InstructorDeleteClasses extends AppCompatActivity implements View.OnClickListener{

    private String userID;
    private User user;

    private DatabaseReference referenceClasses;

    private Spinner classesDeleteSpinner;
    private String selectedClassDelete;
    private ArrayList<String> classesDeleteList;

    private Spinner classesCancelSpinner;
    private String selectedClassCancel;
    private ArrayList<String> classesCancelList;

    private Hashtable<String, String> classesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_instructor_delete_classes);

        userID = getIntent().getExtras().getString("arg"); // passed from previous page
        referenceClasses = FirebaseDatabase.getInstance().getReference("Classes");

        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        referenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InstructorDeleteClasses.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });

        classesDeleteSpinner = (Spinner) findViewById(R.id.deleteClassToTeachSpinner);
        classesDeleteList = new ArrayList<>();
        classesDeleteList.add(0, "Select a class to delete");

        classesCancelSpinner = (Spinner) findViewById(R.id.cancelClassToTeachSpinner);
        classesCancelList = new ArrayList<>();
        classesCancelList.add(0, "Select a class to cancel");

        classesMap = new Hashtable<String, String>();

        pullClassesData();
        initializeDeleteSpinnerDropdown();
        initializeCancelSpinnerDropdown();
    }

    /** Pulls Classes data from realtime database */
    private void pullClassesData() {
        referenceClasses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Class classObject = snapshot.getValue(Class.class);
                    if(classObject != null) {
                        String testIfCancelled = classObject.day;
                        User instructor = classObject.instructor;
                        if (testIfCancelled.equals("N/A")) {
                            if (instructor.getUsername().equals(user.getUsername())) {
                                String uID = snapshot.getKey();
                                String classDescription = classObject.name + "-" + "(cancelled)";
                                if (!(classesDeleteList.contains(classDescription))) {
                                    classesDeleteList.add(classDescription);
                                    classesMap.put(classDescription, uID);
                                }
                            }
                        } else {
                            if (instructor.getUsername().equals(user.getUsername())) {
                                String uID = snapshot.getKey();
                                String classDescription = classObject.name + "-" + classObject.day + "'s : " + classObject.timeInterval;
                                if (!(classesDeleteList.contains(classDescription))) {
                                    classesCancelList.add(classDescription);
                                    classesDeleteList.add(classDescription);
                                    classesMap.put(classDescription, uID);
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InstructorDeleteClasses.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Initializes deleteClass spinner dropdown adapter. And instantiates OnClick item listener. */
    private void initializeDeleteSpinnerDropdown() {
        ArrayAdapter<String> classesAdapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, classesDeleteList);
        classesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classesDeleteSpinner.setAdapter(classesAdapter);
        classesDeleteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a class to delete"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedClassDelete = item;
                } else {
                    selectedClassDelete = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorDeleteClasses.this, "Select a class to delete", Toast.LENGTH_LONG).show();
                selectedClassDelete = "";
            }
        });
    }

    /** Initializes cancelClass spinner dropdown adapter. And instantiates OnClick item listener. */
    private void initializeCancelSpinnerDropdown() {
        ArrayAdapter<String> classesAdapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, classesCancelList);
        classesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classesCancelSpinner.setAdapter(classesAdapter);
        classesCancelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a class to cancel"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedClassCancel = item;
                } else {
                    selectedClassCancel = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorDeleteClasses.this, "Select a class to cancel", Toast.LENGTH_LONG).show();
                selectedClassCancel = "";
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelClassToTeachBtn:
                cancelClass();
                break;
            case R.id.deleteClassToTeachBtn:
                deleteClass();
                break;
            case R.id.homeBtn:
                Intent intentView = new Intent(InstructorDeleteClasses.this, InstructorMain.class);
                intentView.putExtra("arg", userID);
                startActivity(intentView);
                break;
        }
    }

    /** Deletes the selected existing class permanently from the realtime database.
     * Refreshes the data initially pulled, the ArrayLists/Hashtable holding data and re-initializes the Spinners onSuccess. */
    private void deleteClass() {
        if (selectedClassDelete.equals("")) {
            Toast.makeText(InstructorDeleteClasses.this, "Select a class to delete", Toast.LENGTH_SHORT).show();
        } else {
            String key = classesMap.get(selectedClassDelete);
            if (key != null) {
                referenceClasses.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        referenceClasses.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(InstructorDeleteClasses.this, "Class deleted", Toast.LENGTH_SHORT).show();
                                classesDeleteList.clear();
                                classesDeleteList.add(0, "Select a class to delete");
                                classesMap.clear();
                                pullClassesData();
                                initializeDeleteSpinnerDropdown();
                                initializeCancelSpinnerDropdown();
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(InstructorDeleteClasses.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    /** Cancels the selected existing class by removing details associated with it from the realtime database (day, timeSlot, difficulty, capacity).
     * Refreshes the data initially pulled, the ArrayLists/Hashtable holding data and re-initializes the Spinners onSuccess. */
    private void cancelClass() {
        if (selectedClassCancel.equals("")) {
            Toast.makeText(InstructorDeleteClasses.this, "Select a class to cancel", Toast.LENGTH_SHORT).show();
        } else {
            Resources res = getResources();
            String[] fields = res.getStringArray(R.array.fields);
            String key = classesMap.get(selectedClassCancel);
            if (key != null) {
                referenceClasses.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (String child : fields) {
                            if (child.equals("capacity")) {
                                referenceClasses.child(key).child(child).
                                        setValue(Integer.parseInt("0")).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(InstructorDeleteClasses.this, child + " updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                referenceClasses.child(key).child(child).
                                        setValue("N/A").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(InstructorDeleteClasses.this, child + " updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        Toast.makeText(InstructorDeleteClasses.this, "Class: " + selectedClassCancel + " cancelled", Toast.LENGTH_SHORT).show();
                        classesCancelList.clear();
                        classesCancelList.add(0, "Select a class to cancel");
                        classesDeleteList.clear();
                        classesDeleteList.add(0, "Select a class to delete");
                        classesMap.clear();
                        pullClassesData();
                        initializeDeleteSpinnerDropdown();
                        initializeCancelSpinnerDropdown();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(InstructorDeleteClasses.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}

