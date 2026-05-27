package com.example.demo; // 確保您的 package 路徑正確

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// 🌟 最容易忘記的一行：把您的 Enum 引入進來！
import com.example.demo.enums.CarStatus; 

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    
    
    List<Car> findByStatus(CarStatus status);

   
    List<Car> findByNameContainingIgnoreCase(String keyword);
}


