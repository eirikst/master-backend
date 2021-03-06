package com.andreasogeirik.service;

import com.andreasogeirik.model.entities.User;
import com.andreasogeirik.model.entities.UserRole;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by eirikstadheim on 28/01/16.
 */
@Service("userDetailsService")
public class HibernateUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    /*
     * Retrieves the specified user from entities object and creates a UserDetails object(needed for http authentication)
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDao.findByEmail(email);
        if(user != null) {
            return buildUser(user);
        }
        else {
            throw new UsernameNotFoundException("Username not found.");

        }
    }

    /*
     * Converts a com.andreasogeirik.model.entities.User to a org.springframework.security.core.userdetails.User
     */
    private org.springframework.security.core.userdetails.User buildUser(User user) {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        if(user.getUserRole() != null) {
            Iterator it = user.getUserRole().iterator();

            while (it.hasNext()) {
                authorities.add(new SimpleGrantedAuthority(((UserRole) it.next()).getRole()));
            }
        }
        return new com.andreasogeirik.security.User(user.getId(), user.getEmail(), user.getPassword(),
                user.isEnabled(), true, true, true, authorities);
    }
}
