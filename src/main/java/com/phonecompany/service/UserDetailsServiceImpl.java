package com.phonecompany.service;

import com.phonecompany.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        LOG.debug("Try login: " + login);
        com.phonecompany.model.User user = userService.findByUsername(login);
        LOG.debug("User found by email: {}", user);
        if (user == null) {
            throw new UsernameNotFoundException(login + " not found");
        }

        Set<GrantedAuthority> roles = new HashSet<>();

        LOG.debug("User role: {}", user.getRole());
        LOG.debug("user.getRole().name().equals(\"ADMIN\"): {}",
                user.getRole().name().equals("ADMIN"));
        if(user.getRole().name().equals("ADMIN")) {
            roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            roles.add(new SimpleGrantedAuthority("ROLE_CSR"));
            roles.add(new SimpleGrantedAuthority("ROLE_PMG"));
        }

        return new User(user.getEmail(), user.getPassword(), roles);
    }
}