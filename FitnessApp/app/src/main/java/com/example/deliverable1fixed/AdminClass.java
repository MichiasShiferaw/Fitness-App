package com.example.deliverable1fixed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
 * A class representing the Admin Class behavior
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class AdminClass extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference reference;
    private String userID;

    private Spinner deleteSpinner;
    private String selectedClassTypeForDeletion;
    private ArrayList<String> classTypesForDeletion;

    private Spinner editSpinner;
    private String selectedClassTypeForEditing;
    private ArrayList<String> classTypesForEditing;

    private Hashtable<String, String> classTypesMap;

    private EditText editTextCreateName, editTextCreateDesc, editTextEditName, editTextEditDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_admin_manage_classes);

        userID = getIntent().getExtras().getString("arg");
        reference = FirebaseDatabase.getInstance().getReference("ClassTypes");

        Button homeButton = (Button) findViewById(R.id.home);
        homeButton.setOnClickListener(this);

        Button createButton = (Button) findViewById(R.id.createClassBtn);
        homeButton.setOnClickListener(this);

        Button deleteButton = (Button) findViewById(R.id.deleteClassBtn);
        homeButton.setOnClickListener(this);

        Button editButton = (Button) findViewById(R.id.editClassBtn);
        homeButton.setOnClickListener(this);

        editTextCreateName = (EditText) findViewById(R.id.text_create_class_name_field);
        editTextCreateDesc = (EditText) findViewById(R.id.text_create_class_description_field);
        editTextEditName = (EditText) findViewById(R.id.text_edit_class_name_field);
        editTextEditDesc = (EditText) findViewById(R.id.text_edit_class_description_field);

        deleteSpinner = (Spinner) findViewById(R.id.deleteClassSpinner);
        editSpinner = (Spinner) findViewById(R.id.editClassSpinner);

        classTypesMap = new Hashtable<String, String>();

        classTypesForDeletion = new ArrayList<>();
        classTypesForDeletion.add(0, "Select class type");

        classTypesForEditing = new ArrayList<>();
        classTypesForEditing.add(0, "Select class type");

        getClassTypeData();
        initializeDeleteClassTypeDropdown();
        initializeEditClassTypeDropdown();
    }

    /** Pulls ClassType data from realtime database */
    private void getClassTypeData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String uID = snapshot.getKey();
                    if(name != null && uID != null) {
                        if(!(classTypesForDeletion.contains(name) || classTypesForEditing.contains(name))) {
                            classTypesForDeletion.add(name);
                            classTypesForEditing.add(name);
                            classTypesMap.put(name, uID);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminClass.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Initializes the deleteClass dropdown spinner adapter. And instantiates on click item listener. */
    private void initializeDeleteClassTypeDropdown() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, classTypesForDeletion);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deleteSpinner.setAdapter(adapter);

        deleteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select class type"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedClassTypeForDeletion = item;
                } else {
                    selectedClassTypeForDeletion = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AdminClass.this, "Select class type", Toast.LENGTH_LONG).show();
                selectedClassTypeForDeletion = "";
            }
        });
    }

    /** Initializes the editClass dropdown spinner adapter. And instantiates on click item listener. */
    private void initializeEditClassTypeDropdown() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, classTypesForEditing);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSpinner.setAdapter(adapter);

        editSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select class type"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedClassTypeForEditing = item;
                } else {
                    selectedClassTypeForEditing = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AdminClass.this, "Select class type", Toast.LENGTH_LONG).show();
                selectedClassTypeForEditing = "";
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home:
                Intent intentClasses = new Intent(AdminClass.this, AdminMain.class);
                intentClasses.putExtra("arg", userID);
                startActivity(intentClasses);
                break;
            case R.id.createClassBtn:
                createClassType();
                break;
            case R.id.deleteClassBtn:
                deleteClassType();
                break;
            case R.id.editClassBtn:
                editClassType();
                break;
        }
    }

    /** Creates a new class type and pushes it to the realtime database.
     * Refreshes the data initially pulled, the ArrayLists/Hashtable holding data and re-initializes the Spinners onSuccess*/
    private void createClassType() {

        // name field validation
        String name = editTextCreateName.getText().toString().trim();
        if(name.isEmpty()) {
            String estring = "Enter a valid name";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(getResources().getColor(R.color.white));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            editTextCreateName.requestFocus();
            editTextCreateName.setError(ssbuilder);
            return;
        }

        // description field validation
        String description = editTextCreateDesc.getText().toString().trim();
        if(description.isEmpty()) {
            String estring = "Enter a valid description";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(getResources().getColor(R.color.white));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            editTextCreateDesc.setError(ssbuilder);
            editTextCreateDesc.requestFocus();
            return;
        }

        // determines if there already exists a ClassType with the entered name.
        if(classTypesForDeletion.contains(name)) {
            Toast.makeText(AdminClass.this, "This class type already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // creates a new ClassType and pushes it to the realtime database
        ClassType newClass = new ClassType(name, description);
        reference.push().setValue(newClass).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AdminClass.this, "ClassType: " + name + " created", Toast.LENGTH_SHORT).show();
                editTextCreateName.setText("");
                editTextCreateDesc.setText("");
                classTypesForDeletion.clear();
                classTypesForDeletion.add(0, "Select class type");
                classTypesForEditing.clear();
                classTypesForEditing.add(0, "Select class type");
                classTypesMap.clear();
                getClassTypeData();
                initializeDeleteClassTypeDropdown();
                initializeEditClassTypeDropdown();
            }
        });
    }

    /** Edits an existing class type's details and pushes them to the realtime database.
     * Refreshes the data initially pulled, the ArrayLists/Hashtable holding data and re-initializes the Spinners onSuccess*/
    private void editClassType() {

        // name field validation
        String name = editTextEditName.getText().toString().trim();
        if (selectedClassTypeForEditing.equals("")) {
            Toast.makeText(AdminClass.this, "Select a class type to edit", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.isEmpty()){
            String estring = "Enter a valid name";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(getResources().getColor(R.color.white));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            editTextEditName.requestFocus();
            editTextEditName.setError(ssbuilder);
            return;
        }

        // description field validation
        String description = editTextEditDesc.getText().toString().trim();
        if(description.isEmpty()) {
            String estring = "Enter a valid description";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(getResources().getColor(R.color.white));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            editTextEditDesc.setError(ssbuilder);
            editTextEditDesc.requestFocus();
            return;
        }

        // pushes the edited details of an existing ClassType to the realtime database
        String key = classTypesMap.get(selectedClassTypeForEditing);
        if (key != null) {
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    reference.child(key).child("name").
                            setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AdminClass.this, "Name updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                    reference.child(key).child("description").
                            setValue(description).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AdminClass.this, "Description updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(AdminClass.this, "ClassType: " + name + " updated", Toast.LENGTH_SHORT).show();
                    editTextEditName.setText("");
                    editTextEditDesc.setText("");
                    classTypesForDeletion.clear();
                    classTypesForDeletion.add(0, "Select class type");
                    classTypesForEditing.clear();
                    classTypesForEditing.add(0, "Select class type");
                    classTypesMap.clear();
                    getClassTypeData();
                    initializeDeleteClassTypeDropdown();
                    initializeEditClassTypeDropdown();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AdminClass.this, "Database Error", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /** Deletes an existing class type from the realtime database.
     * Refreshes the data initially pulled, the ArrayLists/Hashtable holding data and re-initializes the Spinners onSuccess*/
    private void deleteClassType() {
        if (selectedClassTypeForDeletion.equals("")) {
            Toast.makeText(AdminClass.this, "Select a class type to delete", Toast.LENGTH_SHORT).show();
        } else {
            String key = classTypesMap.get(selectedClassTypeForDeletion);
            if (key != null) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AdminClass.this, "ClassType: " +  selectedClassTypeForDeletion + " deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                        classTypesForDeletion.clear();
                        classTypesForDeletion.add(0, "Select class type");
                        classTypesForEditing.clear();
                        classTypesForEditing.add(0, "Select class type");
                        classTypesMap.clear();
                        getClassTypeData();
                        initializeEditClassTypeDropdown();
                        initializeDeleteClassTypeDropdown();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminClass.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}