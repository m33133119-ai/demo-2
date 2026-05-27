package com.example.demo; //查詢窗口


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface UserRepository extends JpaRepository<User, String> {
	
    User findByEmail(String email);
}
