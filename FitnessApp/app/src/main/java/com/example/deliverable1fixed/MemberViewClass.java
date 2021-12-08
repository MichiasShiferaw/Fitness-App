package com.example.deliverable1fixed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Locale;

public class MemberViewClass extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference referenceClasses;
    private String userID;
    private User user;

    private int currentNumberOfMembers;

    private Spinner classesSpinner;
    private ArrayList<String> classesList;
    private String selectedClass;

    //private ListView listView;
    private TextView details;

    private Hashtable<String, Class> classesMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_member_view_class);

        currentNumberOfMembers = 0;

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
                Toast.makeText(MemberViewClass.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });

        classesSpinner = (Spinner) findViewById(R.id.mycmSpinner);
        classesList = new ArrayList<String>();
        classesList.add(0, "Select a class");
        classesMap = new Hashtable<String, Class>();
        details = (TextView) findViewById(R.id.classDetails);
        pullClassesData();
        initializeClassesSpinnerDropdown();
    }

    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.viewClassDetailBtn:
                viewClassDetails();
                break;
            case R.id.homeBtn:
                Intent intentView = new Intent(MemberViewClass.this, MemberMain.class);
                intentView.putExtra("arg", userID);
                startActivity(intentView);
                break;
            case R.id.myenrollmentbtn:
                enroll();
                break;
        }
    }


    /**
     * Pulls Classes data from realtime database
     */
    public void pullClassesData() {
        referenceClasses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Class classObject = snapshot.getValue(Class.class);
                    ArrayList<Class> myClasses = user.getMyClasses();
                    if (classObject != null && myClasses != null) {
                        String testIfCancelled = classObject.day;
                        if (!(testIfCancelled.equals("N/A"))) {
                            if (!(filterOutAlreadyEnrolledClasses(myClasses, classObject))) {
                                String classDescription = classObject.name + " - " + classObject.day + "'s : " + classObject.timeInterval;
                                if (!(classesList.contains(classDescription))) {
                                    classesList.add(classDescription);
                                    classesMap.put(classDescription, classObject);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MemberViewClass.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Filters out classes that the user is already enrolled in. Prevents enrolling duplication
     *
     * @param enrolledClasses List of classes the user is already enrolled in.
     * @param c               Class selected from database to check.
     * @return Boolean true if user is already enrolled in selected Class c. False otherwise.
     */
    public boolean filterOutAlreadyEnrolledClasses(ArrayList<Class> enrolledClasses, Class c) {
        for (Class v : enrolledClasses) {
            if (v != null) {
                if (v.name.equals(c.name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Initializes classes spinner dropdown adapter. And instantiates OnClick item listener.
     */
    private void initializeClassesSpinnerDropdown() {
        ArrayAdapter<String> classesAdapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, classesList);
        classesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classesSpinner.setAdapter(classesAdapter);
        classesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position).equals("Select a class"))) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
                    details.setVisibility(View.GONE);
                    selectedClass = item;
                } else {
                    selectedClass = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MemberViewClass.this, "Select a class to view its members", Toast.LENGTH_LONG).show();
                selectedClass = "";
            }
        });
    }

    /**
     * Initializes the members list of the selected last for the ListView ArrayAdapter.
     *
     * @param classSelected the class selected in the Spinner dropdown
     * @return ArrayList<String < /> of member descriptions for the ListView.
     */
    private void pullClassData(String classSelected) {
        ArrayList<String> membersDesc = new ArrayList<String>();
        String output;
        Class key = classesMap.get(classSelected);
        if (key != null) {
            if (key.getInstructor() == (null)) {
                details.setText(key.getDifficultyLevel() + "-" + key.getName() + "\n" + key.getDay() + "'s at " + key.getTimeInterval() + "\n (" + key.getCapacity() + " spots left)");
            } else {
                details.setText(key.getDifficultyLevel() + "-" + key.getName() + "\n" + key.getDay() + "'s at " + key.getTimeInterval() + "\nTaught by " + key.getInstructor().getFullName() + "\n (" + key.getCapacity() + " spots left)");
                details.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(MemberViewClass.this, "This class currently has no published details", Toast.LENGTH_LONG).show();
            details.setVisibility(View.GONE);

        }
        // return membersDesc;
    }

    /**
     * Displays ListView of members enrolled in the selected class.
     */
    private void viewClassDetails() {
        if (!(selectedClass.equals(""))) {
            pullClassData(selectedClass);
            //setUpList(pullMembersData(selectedClass));
        } else {
            Toast.makeText(MemberViewClass.this, "Please select a class to view its description", Toast.LENGTH_LONG).show();
            details.setVisibility(View.GONE);
        }
    }

    /**
     * Displays ListView of members enrolled in the selected class.
     */
    private void enroll() {
        if (!(selectedClass.equals(""))) {
            pullCapacityFromCurrentMembers();
        } else {
            Toast.makeText(MemberViewClass.this, "Please select a class to view its description", Toast.LENGTH_LONG).show();
            details.setVisibility(View.GONE);
        }
    }

    private void enrolling(String selectedClass1) {
        if (!(selectedClass1.equals(""))) {
            Class key = classesMap.get(selectedClass1);
            if (key != null) {
                if ((currentNumberOfMembers < key.capacity)) {
                    currentNumberOfMembers = 0;
                    if (!checkTime(key.timeInterval, key.day)) {
                        user.addClass(key);
                        Toast.makeText(MemberViewClass.this, "New Class Added", Toast.LENGTH_LONG).show();
                        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                        referenceUsers.child("myClasses").child(String.valueOf(user.getMyClasses().size() - 1)).setValue(key);

                        // resets key page elements and hides previously shown info
                        classesList.clear();
                        classesList.add(0, "Select a class");
                        classesMap.clear();
                        details.setVisibility(View.GONE);
                        pullClassesData();
                        initializeClassesSpinnerDropdown();
                        return;
                    } else {
                        Toast.makeText(MemberViewClass.this, "Time conflicts with a class you are currently enrolled in", Toast.LENGTH_LONG).show();
                        details.setVisibility(View.GONE);
                        return;
                    }
                } else {
                    Toast.makeText(MemberViewClass.this, "Class is full", Toast.LENGTH_LONG).show();
                    details.setVisibility(View.GONE);
                    return;
                }
            }
        }
    }

    /**
     * @param time1 the time interval of the selected class
     * @return boolean stating if there is a conflict or not
     */
    private boolean checkTime(String time1, String day) {
        //check if there are no classes

        if (user.getMyClasses().size() == 0) {
            return false;
        }

        //create comparable times
        double[] newClassTime = timeComparable(time1);

        //compares with all of the users current class times
        for (int i = 0; i < user.getMyClasses().size(); i++) {
            if (user.getMyClasses().get(i) != null) {
                if (user.getMyClasses().get(i).getTimeInterval() != null) {
                    String compareClassDay = user.getMyClasses().get(i).getDay().trim().toLowerCase(Locale.ROOT);
                    String compareDay = day.trim().toLowerCase(Locale.ROOT);
                    if(compareClassDay.equals(compareDay)) {
                        double[] currentClassTimes = timeComparable(user.getMyClasses().get(i).getTimeInterval());
                        if (newClassTime[1] >= currentClassTimes[0] && currentClassTimes[0] >= newClassTime[0]) {
                            return true;
                        }

                        if (currentClassTimes[1] >= newClassTime[0] && currentClassTimes[1] <= newClassTime[1]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Converts the string format of the time to a double array to make it easier to compare times
     *
     * @param times
     * @return
     */
    private double[] timeComparable(String times) {
        String[] timeIntervals = times.trim().split("-");
        double[] timesComparables = new double[2];

        for (int i = 0; i < timeIntervals.length; i++) {
            if (timeIntervals[i].contains("pm") && timeIntervals[i].substring(0, timeIntervals[i].indexOf(':')) != "12") {
                timesComparables[i] = Double.parseDouble(timeIntervals[i].substring(0, timeIntervals[i].indexOf(':'))) + 12;
            } else {
                timesComparables[i] = Double.parseDouble(timeIntervals[i].substring(0, timeIntervals[i].indexOf(':')));
            }
            timesComparables[i] += Double.parseDouble(timeIntervals[i].substring(timeIntervals[i].indexOf(':') + 1, timeIntervals[i].length() - 3)) * 0.01;
        }

        return timesComparables;
    }


    /**
     * Initializes the members list of the selected last for the ListView ArrayAdapter.
     */
    private void pullCapacityFromCurrentMembers() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String testIfCancelled;
                String classDescription;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User tempUser = snapshot.getValue(User.class);
                    if (tempUser != null && (!(tempUser.getType().equals("Instructor")))) {
                        ArrayList<Class> userClasses = tempUser.getMyClasses();
                        for (Class c : userClasses) {
                            if (c != null) {
                                if (!(c.name.equals("Onboarding"))) {
                                    testIfCancelled = c.day;
                                    if (!(testIfCancelled.equals("N/A"))) {
                                        classDescription = c.name + " - " + c.day + "'s : " + c.timeInterval;
                                        if (classDescription.equals(selectedClass)) {
                                            currentNumberOfMembers++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                enrolling(selectedClass);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MemberViewClass.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}



