package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    // Spring 會自動幫你實作透過 Email 找人的功能
    User findByEmail(String email);
}
