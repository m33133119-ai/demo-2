package com.example.demo; //處理預約資料存取

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}

