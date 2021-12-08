package com.example.deliverable1fixed;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class FrontScreenTest {
    @Test
    public void validateLoginFormFieldsEmailFalse() throws Exception {
        String identifier = "";
        String password = "test";

        Boolean output;
        Boolean expected = false;

        FrontScreen test = new FrontScreen();

        output = test.validateLoginFormFields(identifier, password);

        assertEquals(expected, output);
    }

    @Test
    public void validateLoginFormFieldsPasswordFalse() throws Exception {
        String identifier = "test@gmail.com";
        String password = "test";

        Boolean output;
        Boolean expected = false;

        FrontScreen test = new FrontScreen();

        output = test.validateLoginFormFields(identifier, password);

        assertEquals(expected, output);
    }

    @Test
    public void validateLoginFormFieldsPasswordFalseTrue() throws Exception {
        String identifier = "test@gmail.com";
        String password = "test12234";

        Boolean output;
        Boolean expected = true;

        FrontScreen test = new FrontScreen();

        output = test.validateLoginFormFields(identifier, password);

        assertEquals(expected, output);
    }

    @Test
    public void validateCredentialsTrue() throws Exception {
        Boolean type = true;
        String password = "test123";
        String identifier = "test@gmail.com";

        Boolean output;
        Boolean expected = true;

        FrontScreen test = new FrontScreen();

        output = test.validateCredentials(type, identifier, password);

        assertEquals(expected, output);
    }
    @Test
    public void validateCredentialsEmailFalse() throws Exception {
        Boolean type = true;
        String password = "test123";
        String identifier = "";

        Boolean output;
        Boolean expected = false;

        FrontScreen test = new FrontScreen();

        output = test.validateCredentials(type, identifier, password);

        assertEquals(expected, output);
    }
    @Test
    public void validateCredentialsPasswordFalse() throws Exception {
        Boolean type = true;
        String password = "";
        String identifier = "test@gmail.com";

        Boolean output;
        Boolean expected = false;

        FrontScreen test = new FrontScreen();

        output = test.validateCredentials(type, identifier, password);

        assertEquals(expected, output);
    }
    @Test
    public void validateCredentialsTypeFalse() throws Exception {
        Boolean type = false;
        String password = "test123";
        String identifier = "test@gmail.com";

        Boolean output;
        Boolean expected = false;

        FrontScreen test = new FrontScreen();

        output = test.validateCredentials(type, identifier, password);

        assertEquals(expected, output);
    }
}