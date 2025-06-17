// src/com/votingsystem/model/Constituency.java
package com.votingsystem.model;

import java.io.Serializable;

public class Constituency implements Serializable {
    private static final long serialVersionUID = 1L;

    private int constituencyId;
    private String name;
    private String description;

    public Constituency() {
    }

    public Constituency(int constituencyId, String name, String description) {
        this.constituencyId = constituencyId;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public int getConstituencyId() { return constituencyId; }
    public void setConstituencyId(int constituencyId) { this.constituencyId = constituencyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Constituency{" +
                "constituencyId=" + constituencyId +
                ", name='" + name + '\'' +
                '}';
    }
}