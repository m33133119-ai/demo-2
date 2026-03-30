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
        // 1. 去資料庫找人
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            throw new UsernameNotFoundException("找不到此帳號: " + email);
        }

        // 2. 處理角色權限 (Spring Security 判斷 hasRole('ADMIN') 時，通常需要加上 ROLE_ 前綴)
        String roleWithPrefix = "ROLE_" + user.getRole(); 

        // 3. 回傳 Spring Security 看得懂的使用者物件
        return new CustomUserDetails(
        	    user.getEmail(),
        	    user.getPassword(),
        	    Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix)),
        	    user.getName() // 👈 重點！把資料庫裡的真實姓名 (例如：林紀為) 傳進去！
        	);
    }
}