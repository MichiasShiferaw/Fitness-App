package com.example.deliverable1fixed;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Objects;
import java.util.Set;
/**
 * A class representing the editing classes
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class InstructorEditClasses extends AppCompatActivity implements View.OnClickListener{

    private static class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

    private String userID;
    private User user;

    private DatabaseReference referenceClassTypes;
    private DatabaseReference referenceClasses;

    private EditText editTextEditCapacity;
    private EditText editTextEditName;

    private Spinner daysSpinner;
    private String selectedDay;
    private String[] daysList;

    private Spinner timeSlotsSpinner;
    private String selectedTimeSlot;
    private String[] timeSlotsList;

    private Spinner difficultyLevelsSpinner;
    private String selectedDifficultyLevel;
    private String[] difficultyLevelsList;

    private Spinner classTypesSpinner;
    private String selectedClassType;
    private ArrayList<String> classTypesList;
    private Hashtable<String, ClassType> classTypesMap;

    private Spinner classesSpinner;
    private String selectedClassDesc;
    private ArrayList<Class> classesList;
    private ArrayList<String> classesDescList;
    private Hashtable<String, String> classesMap;
    private Hashtable<String, Class> classesObjMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_instructor_edit_classes);

        userID = getIntent().getExtras().getString("arg"); // passed from previous page
        referenceClassTypes = FirebaseDatabase.getInstance().getReference("ClassTypes");
        referenceClasses = FirebaseDatabase.getInstance().getReference("Classes");

        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        referenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InstructorEditClasses.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });

        Resources res = getResources();

        editTextEditName = (EditText) findViewById(R.id.text_edit_class_name_field);

        editTextEditCapacity= (EditText) findViewById(R.id.text_edit_class_capacity_field);
        editTextEditCapacity.setTransformationMethod(new InstructorEditClasses.NumericKeyBoardTransformationMethod());

        Button home = (Button) findViewById(R.id.homeBtn);
        home.setOnClickListener(this);

        Button editClass = (Button) findViewById(R.id.editClassToTeachBtn);
        editClass.setOnClickListener(this);

        daysSpinner = (Spinner) findViewById(R.id.editClassDaySpinner);
        daysList = res.getStringArray(R.array.days);

        timeSlotsSpinner = (Spinner) findViewById(R.id.editClassTimeSlotSpinner);
        timeSlotsList = res.getStringArray(R.array.timeSlots);

        difficultyLevelsSpinner = (Spinner) findViewById(R.id.editClassDifficultySpinner);
        difficultyLevelsList = res.getStringArray(R.array.difficultyLevels);

        classTypesSpinner = (Spinner) findViewById(R.id.editClassTypeSpinner);
        classTypesList = new ArrayList<>();
        classTypesList.add(0, "Select a class type");
        classTypesMap = new Hashtable<String, ClassType>();

        classesList = new ArrayList<>();

        classesSpinner = (Spinner) findViewById(R.id.editClassSelectSpinner);
        classesDescList = new ArrayList<>();
        classesDescList.add(0, "Select a class to edit");
        classesMap = new Hashtable<String, String>();
        classesObjMap = new Hashtable<String, Class>();

        pullClassTypeData();
        pullClassesData();
        initializeAllSpinnerDropdowns();
        checkDayAndClassType(new ClassType(), ""); // initialize pull with null values
        checkExistingName(""); // initialize pull with null values
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editClassToTeachBtn:
                editClass();
                break;
            case R.id.homeBtn:
                Intent intentView = new Intent(InstructorEditClasses.this, InstructorMain.class);
                intentView.putExtra("arg", userID);
                startActivity(intentView);
                break;
        }
    }

    /** Pulls ClassType data from realtime database */
    private void pullClassTypeData() {
        referenceClassTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ClassType classTypeObject = snapshot.getValue(ClassType.class);
                    String name = snapshot.child("name").getValue(String.class);
                    if(name != null && classTypeObject != null) {
                        if(!(classTypesList.contains(name))) {
                            classTypesList.add(name);
                            classTypesMap.put(name, classTypeObject);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InstructorEditClasses.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
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
                        if (instructor.getUsername().equals(user.getUsername())) {
                            String uID = snapshot.getKey();
                            String classDescription;
                            if (testIfCancelled.equals("N/A")) {
                                classDescription = classObject.name + "-" + "(cancelled)";
                            } else {
                                classDescription = classObject.name + "-" + classObject.day + "'s : " + classObject.timeInterval;
                            }
                            if (!(classesDescList.contains(classDescription)) && !(classesList.contains(classObject))) {
                                classesList.add(classObject);
                                classesObjMap.put(uID, classObject);
                                classesDescList.add(classDescription);
                                classesMap.put(classDescription, uID);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InstructorEditClasses.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Initializes all dropdown spinner adapters. And instantiates OnClick item listener for each. */
    private void initializeAllSpinnerDropdowns() {

        // classesSpinner
        ArrayAdapter<String> classesAdapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, classesDescList);
        classesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classesSpinner.setAdapter(classesAdapter);
        classesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a class to edit"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedClassDesc = item;
                } else {
                    selectedClassDesc = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorEditClasses.this, "Select class to edit", Toast.LENGTH_LONG).show();
                selectedClassDesc = "";
            }
        });

        // classTypesSpinner
        ArrayAdapter<String> classTypesAdapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, classTypesList);
        classTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classTypesSpinner.setAdapter(classTypesAdapter);
        classTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a class type"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedClassType = item;
                } else {
                    selectedClassType = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorEditClasses.this, "Select class type", Toast.LENGTH_LONG).show();
                selectedClassType = "";
            }
        });

        // daysSpinner
        ArrayAdapter<String> daysAdapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, daysList);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(daysAdapter);
        daysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a day"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedDay = item;
                } else {
                    selectedDay = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorEditClasses.this, "Select a day", Toast.LENGTH_LONG).show();
                selectedDay = "";
            }
        });

        // timeSlotSpinner
        ArrayAdapter<String> timeSlotAdapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, timeSlotsList);
        timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotsSpinner.setAdapter(timeSlotAdapter);
        timeSlotsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a time slot"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedTimeSlot = item;
                } else {
                    selectedTimeSlot = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorEditClasses.this, "Select a time slot", Toast.LENGTH_LONG).show();
                selectedTimeSlot = "";
            }
        });

        // difficultyLevelsSpinner
        ArrayAdapter<String> difficultyLevelsAdapter =
                new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, difficultyLevelsList);
        difficultyLevelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultyLevelsSpinner.setAdapter(difficultyLevelsAdapter);
        difficultyLevelsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a difficulty"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    selectedDifficultyLevel = item;
                } else {
                    selectedDifficultyLevel = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InstructorEditClasses.this, "Select a difficulty", Toast.LENGTH_LONG).show();
                selectedDifficultyLevel = "";
            }
        });
    }

    /** Checks that the selected classType doesn't already exist on the same day.
     * The return value is used later on during the creation validation process.
     * @param classType ClassType selected by instructor
     * @param day Day selected by instructor
     * @return String "" if there is no instructor with a class of the same ClassType on the selected day.
     * String instructor fullName if there is an instructor with a class of the same ClassType on the selected day.*/
    private String checkDayAndClassType(ClassType classType, String day) {
        if(classesList != null) {
            for (int i = 0; i < classesList.size(); i++) {
                if (classesList.get(i).classType.getName().equals(classType.getName())) {
                    String dayX = classesList.get(i).day;
                    if (!(dayX.equals("N/A"))) {
                        if (dayX.equals(day)) {
                            return classesList.get(i).instructor.getFullName();
                        }
                    }
                }
            }
            return "";
        }
        return "";
    }

    /** Checks that the entered class name doesn't already exist.
     * @param name String name inputted by the instructor.
     * @return String "" if there is no class of the same name.
     * String class existing name if there is a class of the same name.*/
    private String checkExistingName(String name) {
        if(classesList != null) {
            for (int i = 0; i < classesList.size(); i++) {
                if (classesList.get(i).name.equals(name)) {
                    return classesList.get(i).name;
                }
            }
            return "";
        }
        return "";
    }

    /** Checks that at least one field has been filled with data.
    * @return Boolean true if all fields are empty. Boolean false otherwise. */
    private boolean checkIfFieldsAreEmpty() {
        return selectedDifficultyLevel.equals("") && selectedDay.equals("") && selectedTimeSlot.equals("") &&
                editTextEditName.getText().toString().trim().equals("") && editTextEditCapacity.getText().toString().trim().equals("")
                && classTypesMap.get(selectedClassType) == null;
    }

    /** Records the data entered and matches it to it's name for mapping purposes.
     * Used during the editing process later on.
     * @return Hashtable<String, String> where keys are the data name (labels) and the value held is the actual data entered by the instructor. */
    private Hashtable<String, String> fieldsToBeEdited() {
        Hashtable<String, String> fieldsMap = new Hashtable<String, String>();
        if(!(editTextEditName.getText().toString().trim().equals(""))) {
            fieldsMap.put("name", editTextEditName.getText().toString().trim());
        }
        if (!(selectedTimeSlot.equals(""))) {
            fieldsMap.put("timeInterval", selectedTimeSlot);
        }
        if (!(selectedDay.equals(""))) {
            fieldsMap.put("day", selectedDay);
        }
        if (!(selectedDifficultyLevel.equals(""))) {
            fieldsMap.put("difficultyLevel", selectedDifficultyLevel);
        }
        if (!(editTextEditCapacity.getText().toString().trim().equals(""))) {
            fieldsMap.put("capacity", editTextEditCapacity.getText().toString().trim());
        }
        if (classTypesMap.get(selectedClassType) != null) {
            fieldsMap.put("classType", selectedClassType);
        }
        return fieldsMap;
    }

    /** Edits the details of an existing class using the new details inputted by the instructor and pushes the updates to the realtime database.
     * Refreshes the data initially pulled, the ArrayLists/Hashtable holding data and re-initializes the Spinners onSuccess. */
    private void editClass() {
        // form validation
        String key = classesMap.get(selectedClassDesc);
        if (key != null) {
            if(!(checkIfFieldsAreEmpty())) {
                Class c = classesObjMap.get(key);
                if (c != null) {
                    // verify if there exists a class of with the same name
                    String name = editTextEditName.getText().toString().trim();
                    if (!(checkExistingName(name).equals(""))) {
                        Toast.makeText(InstructorEditClasses.this, "Already existing " + c.classType.getName() +
                                " class named: " + checkExistingName(name), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // verify if there exists a class of the same type on the selectedDay
                    if(!(checkDayAndClassType(c.classType, selectedDay).equals(""))) {
                        Toast.makeText(InstructorEditClasses.this, "Already existing " + c.classType.getName() +
                                        " class on " + selectedDay + " scheduled by: " + checkDayAndClassType(c.classType, selectedDay)
                                , Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // push new class details to realtime database
                    referenceClasses.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Hashtable<String, String> fields = fieldsToBeEdited();
                            Set<String> keys = fields.keySet();
                            for (String child : keys) {
                                if (child.equals("classType")) {
                                    referenceClasses.child(key).child(child).
                                            setValue(classTypesMap.get(fields.get(child))).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(InstructorEditClasses.this, child +" updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else if (child.equals("capacity")) {
                                    referenceClasses.child(key).child(child).
                                            setValue(Integer.parseInt(Objects.requireNonNull(fields.get(child)))).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(InstructorEditClasses.this, child +" updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    referenceClasses.child(key).child(child).
                                            setValue(fields.get(child)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(InstructorEditClasses.this, child +" updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            Toast.makeText(InstructorEditClasses.this, "Class: " + name + " updated", Toast.LENGTH_SHORT).show();
                            editTextEditName.setText("");
                            editTextEditCapacity.setText("");
                            classTypesList.clear();
                            classTypesList.add(0, "Select a class type");
                            classTypesMap.clear();
                            classesDescList.clear();
                            classesDescList.add(0, "Select a class to edit");
                            classesList.clear();
                            classesMap.clear();
                            classesObjMap.clear();
                            pullClassTypeData();
                            pullClassesData();
                            initializeAllSpinnerDropdowns();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(InstructorEditClasses.this, "Database Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Toast.makeText(InstructorEditClasses.this, "Select at least one field to edit", Toast.LENGTH_SHORT).show();
            }
        } else {
                Toast.makeText(InstructorEditClasses.this, "Select class to edit", Toast.LENGTH_LONG).show();
        }
    }
}
