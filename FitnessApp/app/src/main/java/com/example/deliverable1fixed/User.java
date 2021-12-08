package com.example.deliverable1fixed;

import java.util.ArrayList;

/**
 * A class representing the editing classes
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */

public class User {

    private String fullName;
    private String age;
    private String email;
    private String username;
    private String type;
    private String password;
    private ArrayList<Class>myClasses;

    public User () {
    }

    public User(String fullName, String age, String email, String username, String type, String password) {
        this.fullName = fullName;
        this.age=age;
        this.email=email;
        this.username=username;
        this.type=type;
        this.password=password;
        this.myClasses=new ArrayList<Class>();
    }

    public ArrayList<Class> getMyClasses() {
        return myClasses;
    }

    public void addClass(Class classy){

        myClasses.add(classy);
        myClasses=myClasses;
        myClasses.size();
    }

    public int removeClass(Class classy){
        int val= this.getMyClasses().indexOf(classy);
        for (int i=0; i<myClasses.size();i++){
            Class var1 = myClasses.get(i);
            if (var1==null) continue;
            if (var1.equalsClass(classy)){
                this.getMyClasses().remove(i);
                return i;
            }
        }
        //if (val!=-1){
        //    this.getMyClasses().remove(val);
        ///    return true;
        //}
        return -1;
    }

    public String getPassword(){return password;}

    public String getFullName(){return fullName;}

    public String getAge(){return age;}

    public String getUsername(){return username;}

    public String getType(){return type;}

    public String getEmail(){return email;}


}