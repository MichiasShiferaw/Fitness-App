package com.example.deliverable1fixed;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
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

/**
 * A class representing the searching classes
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class MemberSearchClasses extends AppCompatActivity implements View.OnClickListener {
    private static class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

    private String userID;
    private User user;
    private ListView listView;
    private DatabaseReference referenceClassTypes;
    private DatabaseReference referenceClasses;

    public static ArrayList<Class> classesList;
    private Button sortButton;
    private Button filterButton;
    private LinearLayout row1;
    private LinearLayout row2;
    private LinearLayout row3;
    private LinearLayout row4;
    private LinearLayout sortView;

    boolean sortHidden = true;
    boolean filterHidden = true;

    private String filterSel = "all";
    private String currentSearchText = "";
    private SearchView searchView;
    private Button enroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_member_search_classes);
        userID = getIntent().getExtras().getString("arg"); // passed from previous page
        referenceClassTypes = FirebaseDatabase.getInstance().getReference("ClassTypes");
        referenceClasses = FirebaseDatabase.getInstance().getReference("Classes");
        classesList = new ArrayList<>();
        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        referenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MemberSearchClasses.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });

        Resources res = getResources();
        Button home = (Button) findViewById(R.id.myhomeBtn);
        home.setOnClickListener(this);
        Button enroll = (Button) findViewById(R.id.myviewEnrolledbtn);
        enroll.setOnClickListener(this);
        Button unenroll = (Button) findViewById(R.id.myUnenrolledbtn);
        unenroll.setOnClickListener(this);

        searching();
        setupLayout();
        setupData();
        setUpList();
        setAdapter(classesList);

        /*Organize initial layout*/
        row1.setVisibility(View.GONE);
        row2.setVisibility(View.GONE);
        row3.setVisibility(View.GONE);
        row4.setVisibility(View.VISIBLE);
        sortView.setVisibility(View.GONE);

    }
    @Override
    public void onClick(View v) {
        Intent intentView;
        switch (v.getId()) {
            case R.id.myUnenrolledbtn:
                intentView = new Intent(MemberSearchClasses.this, MemberUnenrollClasses.class);
                intentView.putExtra("arg", userID);
                startActivity(intentView);
                break;
            case R.id.myviewEnrolledbtn:
                intentView = new Intent(MemberSearchClasses.this, MemberViewClass.class);
                intentView.putExtra("arg", userID);
                startActivity(intentView);
                break;
            case R.id.myhomeBtn:
                intentView = new Intent(MemberSearchClasses.this, MemberMain.class);
                intentView.putExtra("arg", userID);
                startActivity(intentView);
                break;
        }
    }

    private void setupLayout() {
        //enroll = (Button) findViewById(R.id.myaddMoreClassesbtn);
        sortButton = (Button) findViewById(R.id.mysortButton);
        filterButton = (Button) findViewById(R.id.myfilterButton);
        row1 = (LinearLayout) findViewById(R.id.myfilterTabsLayout);
        row2 = (LinearLayout) findViewById(R.id.myfilterTabsLayout2);
        row3= (LinearLayout) findViewById(R.id.myfilterTabsLayout3);
        row4 = (LinearLayout) findViewById(R.id.myfilterTabsLayout4);
        sortView = (LinearLayout) findViewById(R.id.mysortTabsLayout2);
    }

    private void searching() {
        searchView = (SearchView) findViewById(R.id.myClassListSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                currentSearchText = s;
                ArrayList<Class> filteredClass = new ArrayList<Class>();
                for(Class session: classesList) {
                    //Sort by instructor
                    if(session.getDay().toLowerCase().contains(s.toLowerCase())||session.getName().toLowerCase().contains(s.toLowerCase())||session.classType.getName().toLowerCase().contains(filterSel)){
                        if(filterSel.equals("all")) { filteredClass.add(session); }
                        else {
                            if(session.getDay().toLowerCase().contains(filterSel.toLowerCase())||session.getName().toLowerCase().contains(filterSel)||session.classType.getName().toLowerCase().contains(filterSel)){
                            //||session.classType.name.toLowerCase().contains(filterSel)
                                filteredClass.add(session);
                            }
                        }
                    }
                }
                setAdapter(filteredClass);

                return false;
            }
        });
    }
    private void setupData() {
        referenceClasses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Class classObject = snapshot.getValue(Class.class);
                    if(classObject != null) {
                       classesList.add(classObject);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MemberSearchClasses.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpList() {
        listView = (ListView) findViewById(R.id.myclassListView);
        setAdapter(classesList);
    }


    private void setAdapter(ArrayList<Class> ClassList) {
        ClassAdapter adapter = new ClassAdapter(getApplicationContext(), 0, ClassList);
        listView.setAdapter(adapter);
    }
    public void allFilterTapped(View view) {
        filterSel = "all";
        setAdapter(classesList);
    }
    
    private void strainer(String status) {
        filterSel = status;
        ArrayList<Class> filteredsessions = new ArrayList<Class>();
        for(Class session: classesList) {
            if(session.getDay().toLowerCase().contains(status.toLowerCase())|| session.getName().toLowerCase().contains(status.toLowerCase())) {
                if(currentSearchText == "") { filteredsessions.add(session);
                } else {
                    if(session.instructor.getFullName().toLowerCase().contains(currentSearchText.toLowerCase())) { filteredsessions.add(session); }
                }
            }
        }

        setAdapter(filteredsessions);
    }
    /*-------Set of Classes Available at the Gym ---------*/
    public void yogaFilterTapped(View view) { strainer("Yoga"); }
    public void cyclingFilterTapped(View view) { strainer("Cycling"); }
    public void zumbaFilterTapped(View view) { strainer("Zumba"); }
    public void aquaFilterTapped(View view) { strainer("Aqua "); }
    public void hiitFilterTapped(View view) { strainer("HIIT"); }
    public void danceFilterTapped(View view) { strainer("Dance"); }
    public void cardioFilterTapped(View view) { strainer("Cardio"); }
    public void pilatesFilterTapped(View view) {strainer("Pilates");}


    public void showFilterClicked(View view) {
        if(filterHidden == true) {
            filterHidden = false;
            showFilter();
        }
        else {
            filterHidden = true;
            closeFilter();
        }
    }

    public void showSortClick(View view) {
        if(sortHidden == true) {
            sortHidden = false;
            showSort();
        }
        else {
            sortHidden = true;
            closeSort();
        }
    }
    /* Filter Button Checked or Unchecked Methods*/
    private void closeFilter() {
        searchView.setVisibility(View.GONE);
        row1.setVisibility(View.GONE);
        row2.setVisibility(View.GONE);
        row3.setVisibility(View.GONE);
        row4.setVisibility(View.VISIBLE);
        filterButton.setText("FILTER");
    }

    private void showFilter() {
        searchView.setVisibility(View.VISIBLE);
        row1.setVisibility(View.VISIBLE);
        row2.setVisibility(View.VISIBLE);
        row3.setVisibility(View.VISIBLE);
        row4.setVisibility(View.GONE);
        filterButton.setText("HIDE");
    }
    /* Sort Button Checked or Unchecked Mehtods*/
    private void closeSort() {
        sortView.setVisibility(View.GONE);
        sortButton.setText("SORT");
    }

    private void showSort() {
        sortView.setVisibility(View.VISIBLE);
        sortButton.setText("HIDE");
    }

    /*---------------------------Sorting Methods---------------------*/
    public void capacityTapped(View view) {
        Collections.sort(classesList, Class.capacityAscending);
        Collections.reverse(classesList);
        filterChecker();
    }

    public void capacityDESCTapped(View view) {
        Collections.sort(classesList, Class.capacityAscending);
        filterChecker();
    }

    public void InsensityTapped(View view) {
        Collections.sort(classesList, Class.levelAscending);
        filterChecker();
    }

    public void activtynameSort(View view) {
        Collections.sort(classesList, Class.activtyAscending);
        //Collections.reverse(classesList);
        filterChecker();
    }
    /*----------------Checker for any Filter Changes----------*/
    private void filterChecker() {
        if(filterSel.equals("all")) {
            if(currentSearchText.equals("")) {
                setAdapter(classesList);
            } else {
                ArrayList<Class> filteredsessions = new ArrayList<Class>();
                for(Class session: classesList) {
                    if(session.getName().toLowerCase().contains(currentSearchText)) { filteredsessions.add(session); }
                }
                setAdapter(filteredsessions);
            }
        } else { strainer(filterSel); }
    }
}
