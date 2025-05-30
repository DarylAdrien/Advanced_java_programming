// src/main/java/com/votingsystem/model/Constituency.java
package com.votingsystem.model;

import java.sql.Timestamp;

/**
 * Represents a Constituency in the voting system.
 * Corresponds to the CONSTITUENCIES table in the database.
 */
public class Constituency {
    private int constituencyId;
    private String name;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Constituency() {
    }

    // Constructor for creating a new constituency (without ID, timestamps)
    public Constituency(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Full constructor
    public Constituency(int constituencyId, String name, String description, Timestamp createdAt, Timestamp updatedAt) {
        this.constituencyId = constituencyId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(int constituencyId) {
        this.constituencyId = constituencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Constituency{" +
               "constituencyId=" + constituencyId +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
