package com.example.demo; //查詢窗口


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // 告訴 Spring 這是一個資料存取元件（Bean）
public interface UserRepository extends JpaRepository<User, String> {
	//繼承 JpaRepository<User, String>：
    // User：代表這個倉庫是存 User 物件。
    User findByEmail(String email);
}
