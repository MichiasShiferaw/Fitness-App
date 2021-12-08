package com.example.deliverable1fixed;

import java.util.ArrayList;

public class Member extends User {
    private String fullName;
    private String age;
    private String email;
    private String username;
    private String type;
    private String password;
    private ArrayList myclasses;

    public Member () {
        myclasses= new ArrayList<>();
    }

    /*public Member(String fullName, String age, String email, String username, String type, String password){
        this.fullName = fullName;
        this.age=age;
        this.email=email;
        this.username=username;
        this.type=type;
        this.password=password;

    }*/

    public String getPassword(){return password;}

    public String getFullName(){return fullName;}

    public String getAge(){return age;}

    public String getUsername(){return username;}

    public String getType(){return type;}

    public String getEmail(){return email;}

}
