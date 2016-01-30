package com.andreasogeirik.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * Created by eirikstadheim on 28/01/16.
 */
@Entity
@Table(name = "users")
public class User {

    private int id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private Set<UserRole> userRole = new HashSet<UserRole>(0);

    public User() {
    }

    public User(String username, String password, String email, boolean enabled) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
    }

    public User(String username, String password, String email,
                boolean enabled, Set<UserRole> userRole) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.userRole = userRole;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true,
            nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "username", unique = true,
            nullable = false, length = 45)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password",
            nullable = false, length = 60)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "email", unique = true,
            nullable = false, length = 60)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "enabled", nullable = false)
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    public Set<UserRole> getUserRole() {
        return this.userRole;
    }

    public void setUserRole(Set<UserRole> userRole) {
        this.userRole = userRole;
    }

}