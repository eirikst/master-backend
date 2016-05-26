package com.andreasogeirik.model.entities;

import javax.persistence.*;

/**
 * Created by andrena on 12.05.2016.
 */
@Entity
@Table(name = "misc")
public class Misc {
    private int id;
    private String name;
    private String email;

    public Misc() {
    }

    public Misc(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "misc_id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
