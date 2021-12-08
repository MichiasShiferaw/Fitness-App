package com.example.deliverable1fixed;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RegisterUserTest {
    @Test
    public void validateRegistrationEmailAndUsernameTrue() throws Exception {
        String email = "test@gmail.com";
        String username = "test";

        Boolean output;
        Boolean expected = true;

        RegisterUser test = new RegisterUser();

        output = test.validateRegistrationEmailAndUsername(email, username);

        assertEquals(expected, output);
    }
    @Test
    public void validateRegistrationEmailAndUsernameEmailFalse() throws Exception {
        String email = "admin@gmail.com";
        String username = "test";

        Boolean output;
        Boolean expected = false;

        RegisterUser test = new RegisterUser();

        output = test.validateRegistrationEmailAndUsername(email, username);

        assertEquals(expected, output);
    }
    @Test
    public void validateRegistrationEmailAndUsernameUsernameFalse() throws Exception {
        String email = "test@gmail.com";
        String username = "admin";

        Boolean output;
        Boolean expected = false;

        RegisterUser test = new RegisterUser();

        output = test.validateRegistrationEmailAndUsername(email, username);

        assertEquals(expected, output);
    }

    @Test
    public void validateRegistrationFormFieldsTrue() throws Exception {
        String email = "test@gmail.com";
        String password = "test123";
        String username = "test";
        String fullName = "Bob Builder";
        String age = "49";

        Boolean output;
        Boolean expected = true;

        RegisterUser test = new RegisterUser();

        output = test.validateRegistrationFormFields(email, password, fullName, age, username);

        assertEquals(expected, output);
    }
    @Test
    public void validateRegistrationFormFieldsEmailFalse() throws Exception {
        String email = "test";
        String password = "test123";
        String username = "test";
        String fullName = "Bob Builder";
        String age = "49";

        Boolean output;
        Boolean expected = true;

        RegisterUser test = new RegisterUser();

        output = test.validateRegistrationFormFields(email, password, fullName, age, username);

        assertEquals(expected, output);
    }
    @Test
    public void validateRegistrationFormFieldsPasswordFalse() throws Exception {
        String email = "test@gmail.com";
        String password = "tes";
        String username = "test";
        String fullName = "Bob Builder";
        String age = "49";

        Boolean output;
        Boolean expected = true;

        RegisterUser test = new RegisterUser();

        output = test.validateRegistrationFormFields(email, password, fullName, age, username);

        assertEquals(expected, output);
    }
    @Test
    public void validateRegistrationFormFieldsUsernameFalse() throws Exception {
        String email = "test@gmail.com";
        String password = "test123";
        String username = "";
        String fullName = "Bob Builder";
        String age = "49";

        Boolean output;
        Boolean expected = true;

        RegisterUser test = new RegisterUser();

        output = test.validateRegistrationFormFields(email, password, fullName, age, username);

        assertEquals(expected, output);
    }
    @Test
    public void validateRegistrationFormFieldsFullNameFalse() throws Exception {
        String email = "test@gmail.com";
        String password = "test123";
        String username = "test";
        String fullName = "";
        String age = "49";

        Boolean output;
        Boolean expected = true;

        RegisterUser test = new RegisterUser();

        output = test.validateRegistrationFormFields(email, password, fullName, age, username);

        assertEquals(expected, output);
    }
    @Test
    public void validateRegistrationFormFieldAgeFalse() throws Exception {
        String email = "test@gmail.com";
        String password = "test123";
        String username = "test";
        String fullName = "Bob Builder";
        String age = "";

        Boolean output;
        Boolean expected = true;

        RegisterUser test = new RegisterUser();

        output = test.validateRegistrationFormFields(email, password, fullName, age, username);

        assertEquals(expected, output);
    }
}