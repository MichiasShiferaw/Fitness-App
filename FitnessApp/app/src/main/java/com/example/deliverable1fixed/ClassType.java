package com.example.deliverable1fixed;

/**
 * A class representing the ClassType
 *  @author Michias Shiferaw, Simon Brunet, Joseph Champeau, Charlie Haldane
 *  @version 2.0
 *  @since 2021-11-17
 */
public class ClassType {

    private String name;
    private String description;

    public ClassType() {
    }

    public ClassType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() { return description;}

    public String getName() {return name; }
}
