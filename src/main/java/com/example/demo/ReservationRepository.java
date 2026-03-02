package com.example.demo; //處理預約資料存取

import org.springframework.data.jpa.repository.JpaRepository;//// 匯入 Spring Data JPA 的核心倉庫介面
import org.springframework.stereotype.Repository;//// 標記這是一個資料存取組件

@Repository //告訴 Spring 框架：這是一個負責與資料庫溝通的「倉庫」。
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
/*
 * 繼承 JpaRepository<Reservation, Long> 的含義：
 * 1. Reservation：告訴系統，這個倉庫是專門用來存「預約資料 (Reservation 物件)」的。
 * 2. Long：告訴系統，預約資料的主鍵 (ID) 型態是「Long」。
 * * 繼承後，這個介面會自動獲得以下功能：
 * - save(reservation): 儲存或更新預約。
 * - findAll(): 抓出資料庫所有的預約。
 * - findById(id): 根據編號找預約。
 * - deleteById(id): 根據編號刪除預約（這就是你剛才修好功能時所使用的底層方法）。
 */