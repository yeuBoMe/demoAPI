package com.jobHunter.demoAPI.config;

import com.jobHunter.demoAPI.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.jobHunter.demoAPI.domain.entity.User userGetByEmail = this.userService.getUserByEmail(username);

        if (userGetByEmail == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        return new User(
                userGetByEmail.getEmail(),
                userGetByEmail.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
