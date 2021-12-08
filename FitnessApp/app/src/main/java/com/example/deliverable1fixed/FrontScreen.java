package com.example.deliverable1fixed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Hashtable;
import java.util.Objects;
/**
 * A class representing the FrontScreen page functionality and behavior
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class FrontScreen extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextIdentifier, editTextPassword;
    private ProgressBar progressBar;

    private DatabaseReference reference;
    private Hashtable<String, String> emailUidMap;
    private Hashtable<String, String> usernameUidMap;
final Handler handler = new Handler();
    private Hashtable<String, String> emailAuthMap;
    private Hashtable<String, String> usernameAuthMap;

    //private static int TIME_OUT = 4; //Time to launch the another activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen);
        LoadingScreen load=new LoadingScreen();
        //load.starter("FrontScreen");
        FirebaseApp.initializeApp(this);

        reference = FirebaseDatabase.getInstance().getReference("Users");

        Button signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        TextView register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        editTextIdentifier = (EditText) findViewById(R.id.identifier);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        emailAuthMap = new Hashtable<String, String>();
        emailUidMap = new Hashtable<String, String>();
        usernameAuthMap = new Hashtable<String, String>();
        usernameUidMap = new Hashtable<String, String>();
        pullUserData(true); // instantiate data pull
        pullUserData(false); // instantiate data pull
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.register:
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.signIn:
                userLogin();
                break;
        }
    }

    /** Validates the user account registration form fields for Instructors and Members
     * @param identifier email or username of the user as a String
     * @param password password of the user's account as a String
     * @return Boolean true if all registration fields are successfully validated. Boolean false otherwise.*/
    public boolean validateLoginFormFields(String identifier, String password) {
        if(identifier.isEmpty()){
            String estring = "Email/username is required";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(getResources().getColor(R.color.white));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            editTextIdentifier.setError(ssbuilder);
            editTextIdentifier.requestFocus();

            return false;
        } else if(password.isEmpty()){
            String estring = "Enter a valid password";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(getResources().getColor(R.color.white));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            editTextPassword.setError(ssbuilder);
            editTextPassword.requestFocus();
            return false;
        } else if(password.length() < 6){
            String estring = "Password must be longer than 6 characters";
            ForegroundColorSpan fgcspan = new ForegroundColorSpan(getResources().getColor(R.color.white));
            SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            editTextPassword.setError(ssbuilder);
            editTextPassword.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    /** Checks if identifier is an email or a username
     * @param identifier email or password as a String
     * @return Boolean true if email. False if username */
    private boolean checkIdentifierType(String identifier) {
        return identifier.contains("@");
    }

    /** Pulls user data from realtime database
     * @param identifierType Boolean true if identifierType is an email. False if a username */
    private void pullUserData(boolean identifierType) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (identifierType) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String email = snapshot.child("email").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class);
                        String uID = snapshot.getKey();
                        emailAuthMap.put(email, password);
                        emailUidMap.put(email, uID);
                    }
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String username = snapshot.child("username").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class);
                        String uID = snapshot.getKey();
                        usernameAuthMap.put(username, password);
                        usernameUidMap.put(username, uID);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FrontScreen.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Validates the credentials entered by the user.
     * @param type Boolean true if email. False if username
     * @param identifier email or username of the user as a String
     * @param password password of the user's account as a String
     * @return Boolean true if all the user's entered credentials are valid. Boolean false otherwise.*/
    public boolean validateCredentials(boolean type, String identifier, String password) {
        if (type) {
            if (emailAuthMap == null) {
                Toast.makeText(FrontScreen.this, "Login failed: service unavailable", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return false;
            } else if (!(emailAuthMap.containsKey(identifier))) {
                Toast.makeText(getBaseContext(), "Login failed: invalid email", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return false;
            } else if (!(Objects.equals(emailAuthMap.get(identifier), password))) {
                Toast.makeText(FrontScreen.this, "Login failed: invalid password", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return false;
            } else {
                return true;
            }
        } else {
            if (usernameAuthMap == null) {
                Toast.makeText(FrontScreen.this, "Login failed: service unavailable", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return false;
            } else if (!(usernameAuthMap.containsKey(identifier))) {
                Toast.makeText(FrontScreen.this, "Login failed: invalid username", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return false;
            } else if (!(Objects.equals(usernameAuthMap.get(identifier), password))) {
                Toast.makeText(FrontScreen.this, "Login failed: invalid password", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return false;
            } else {
                return true;
            }
        }
    }

    /** Initiate the user's new session the credentials entered by the user.
     * @param identifier email or username of the user as a String. */
    private void initiateUserSession(String identifier) {
        if (checkIdentifierType(identifier)) {
            Intent intent = new Intent(FrontScreen.this, HomeScreen.class);
            intent.putExtra("arg", emailUidMap.get(identifier)); // sends the user ID as arguments to the next page.
            startActivity(intent);
        } else {
            Intent intent = new Intent(FrontScreen.this, HomeScreen.class);
            intent.putExtra("arg", usernameUidMap.get(identifier)); // sends the user ID as arguments to the next page.
            startActivity(intent);
        }
    }

    /** initiate user login procedure. */
    private void userLogin() {
        String identifier = editTextIdentifier.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if(validateLoginFormFields(identifier, password)) {
            progressBar.setVisibility(View.VISIBLE);
            boolean type = checkIdentifierType(identifier);
            pullUserData(type);
            if (validateCredentials(type, identifier, password)) {
                initiateUserSession(identifier);
            }
        }
    }
}