package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // 告訴 Spring Boot 這是一個資料庫表格
public class Reservation {

    @Id // 設定主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 讓資料庫自動跳號 (1, 2, 3...)
    private Long id;

    private String customerName;
    private String carName;
    private String date;

    // ✨ 重要：JPA 要求必須有一個「無參數」的空建構子
    public Reservation() {
    }

    // 你原本的建構子（保留）
    public Reservation(String customerName, String carName, String date) {
        this.customerName = customerName;
        this.carName = carName;
        this.date = date;
    }

    // --- Getter 和 Setter (Thymeleaf 需要它們) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}