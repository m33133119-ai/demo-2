package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    
    // 這是之後驗證最重要的方法：使用者點連結回來時，我們要用 token 找到這筆資料
    VerificationToken findByToken(String token);
    
    // 也可以根據使用者找 token (選用)
    VerificationToken findByUser(User user);
}