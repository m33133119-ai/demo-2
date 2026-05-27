package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            throw new UsernameNotFoundException("找不到此帳號: " + email);
        }

        
        String roleWithPrefix = "ROLE_" + user.getRole(); 

       
        return new CustomUserDetails(
        	    user.getEmail(),
        	    user.getPassword(),
        	    Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix)),
        	    user.getName() 
        	);
    }
}