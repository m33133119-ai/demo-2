package com.example.demo;  //定義車輛資料庫的搜尋規則

import org.springframework.data.jpa.repository.JpaRepository; // 匯入 Spring Data JPA 的基礎介面，提供增刪改查功能
import org.springframework.stereotype.Repository; // 匯入 Repository 註解，標記此類別為資料存取元件
import java.util.List;

@Repository // 告訴 Spring 框架：這是一個負責讀寫資料庫的元件，請自動建立它的執行個體（Bean）
public interface CarRepository extends JpaRepository<Car, Long> { 
	// 定義一個介面並繼承 JpaRepository
	//<Car, Long> 表示：這個倉庫是存「Car」物件，且該物件的 ID 型態是「Long」
	
    List<Car> findByNameContainingIgnoreCase(String name);
}  

//Spring 會根據方法名稱自動產生 SQL 指令
//1. findBy：代表執行查詢 (SELECT)
// 2. Name：指定查詢 Car 裡面的「name」欄位
// 3. Containing：代表模糊搜尋（相當於 SQL 的 LIKE %關鍵字%）
// 4. IgnoreCase：代表忽略英文字母大小寫（搜尋 abc 也能找到 ABC）
// 5. (String name)：傳入你要搜尋的文字
// 6. List<Car>：將所有符合條件的車子裝成一個清單回傳
