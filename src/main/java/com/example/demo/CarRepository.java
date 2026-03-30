package com.example.demo;  //定義車輛資料庫的搜尋規則

import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository; 
import java.util.List;

@Repository // 告訴 Spring 框架：這是一個負責讀寫資料庫的元件，請自動建立它的執行個體（Bean）
public interface CarRepository extends JpaRepository<Car, Long> { 
	// 定義一個介面並繼承 JpaRepository
	//<Car, Long> 表示：這個倉庫是存「Car」物件，且該物件的 ID 型態是「Long」
	
    List<Car> findByNameContainingIgnoreCase(String name);
    List<Car> findByStatus(String status);
}  


