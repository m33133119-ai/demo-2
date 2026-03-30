package com.example.demo; //處理預約資料存取

import org.springframework.data.jpa.repository.JpaRepository;//// 匯入 Spring Data JPA 的核心倉庫介面
import org.springframework.stereotype.Repository;//// 標記這是一個資料存取組件

@Repository //告訴 Spring 框架：這是一個負責與資料庫溝通的「倉庫」。
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}

