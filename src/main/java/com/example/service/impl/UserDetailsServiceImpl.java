package com.example.service.impl;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/9 20:45
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null){
            return (UserDetails) new UsernameNotFoundException("Username not found " + username);
        } else {
            Set<Role> roles = user.getRoles();
            Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
            for(Role role : roles){
                authorities.add(new SimpleGrantedAuthority(role.getRole()));
            }
            com.example.service.UserDetailsService userDetailsService = new com.example.service.UserDetailsService();
            userDetailsService.setUsername(username);
            userDetailsService.setPassword(user.getPassword());
            userDetailsService.setAuthorities(authorities);

            return userDetailsService;
        }
    }
}
