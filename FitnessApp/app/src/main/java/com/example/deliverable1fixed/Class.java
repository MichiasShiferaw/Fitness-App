package com.example.deliverable1fixed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
/**
 * A class representing the components of a CLASS
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class Class {

    public String name;
    public User instructor;
    public ClassType classType;
    public String difficultyLevel;
    public String day;
    public String timeInterval;
    public int capacity;

    public Class() {}

    public Class(String name, User instructor, ClassType classType, String difficultyLevel, String day, String timeInterval, int capacity) {
        this.name = name;
        this.instructor = instructor;
        this.classType = classType;
        this.difficultyLevel = difficultyLevel;
        this.day = day;
        this.timeInterval = timeInterval;
        this.capacity = capacity;
    }


    public String getName(){return name;}
    public User getInstructor() {return instructor;}
    public ClassType getClassType(){return classType;}
    public String getDifficultyLevel() {return difficultyLevel;}
    public String getDay(){return day;}
    public String getTimeInterval(){return timeInterval;}
    public int getCapacity(){return capacity;}

    public static Comparator<Class> capacityAscending = new Comparator<Class>() {
        @Override
        public int compare(Class class01, Class class02) {
            int id1 = Integer.valueOf(class01.getCapacity());
            int id2 = Integer.valueOf(class02.getCapacity());

            return Integer.compare(id1, id2);
        }
    };

    public static Comparator<Class> activtyAscending = new Comparator<Class>(){
        @Override
        public int compare(Class class01, Class class02)
        {
            String name1 = class01.getName();
            String name2 = class02.getName();
            name1 = name1.toLowerCase();
            name2 = name2.toLowerCase();

            return name1.compareTo(name2);
        }
    };

    // displaying in a level-sorted
    public static Comparator<Class> levelAscending = new Comparator<Class>()
    {
        @Override
        public int compare(Class class01, Class class02)
        {
            List<String> level = Arrays.asList("Beginner", "Intermediate", "Advanced");//Level options
            int level1 = level.indexOf(class01.getDifficultyLevel());
            int level2 = level.indexOf(class02.getDifficultyLevel());

            return Integer.compare(level1, level2);
        }
    };

    public static Comparator<Class> dayAscending = new Comparator<Class>()
    {
        @Override
        public int compare(Class class01, Class class02)
        {
            List<String> daysOfTheWeek = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

            int day1 = daysOfTheWeek.indexOf(class01.getDay());
            int day2 = daysOfTheWeek.indexOf(class02.getDay());

            return Integer.compare(day1, day2);
        }
    };


    public boolean equalsClass(Class o) {
        if (this.getName().equals(o.getName())&&this.getDay().equals(o.getDay())){
            if(this.getTimeInterval().equals(o.getTimeInterval())&&this.getInstructor().getEmail().equals(o.getInstructor().getEmail())){
                return true;
            }
        }
        return false;
    }
    //Could add timeIntervals
    /*public static Comparator<Class> timeAscending = new Comparator<Class>()
    {
        @Override
        public int compare(Class class01, Class class02)
        {


            int day1 = daysOfTheWeek.indexOf(class01.getDay());
            int day2 = daysOfTheWeek.indexOf(class02.getDay());

            return Integer.compare(day1, day2);
        }
    };*/

}
