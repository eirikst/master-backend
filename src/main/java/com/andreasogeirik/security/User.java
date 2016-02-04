package com.andreasogeirik.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by eirikstadheim on 04/02/16.
 */
public class User extends org.springframework.security.core.userdetails.User {
    private int userId;

    public User(int userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public User(int userId, String username, String password, boolean enabled, boolean accountNonExpired,
                boolean credentialsNonExpired, boolean accountNonLocked,
                Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
