package com.example.demo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

// 繼承 Spring Security 的預設 User，幫它擴充裝備
public class CustomUserDetails extends User {
    
    // 新增一個屬性來裝真實姓名
    private String fullName;

    // 建構子：把原本需要的帳密權限傳給父類別，並把真實姓名存起來
    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String fullName) {
        super(username, password, authorities); // 呼叫父類別 (Spring) 的建構子
        this.fullName = fullName;               // 儲存我們的真實姓名
    }

    // 讓前端 Thymeleaf 可以讀取這個名字
    public String getFullName() {
        return fullName;
    }
}