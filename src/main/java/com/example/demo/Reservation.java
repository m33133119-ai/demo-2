package com.example.demo; //在資料庫（H2）裡面幫我建立一張名為 Reservation 的表格，並按照我定義的欄位來存資料。

import javax.persistence.Entity; // 匯入實體註解，用來對應資料庫表格
import javax.persistence.GeneratedValue;// 匯入自動產生值的註解
import javax.persistence.GenerationType;// 匯入自動產生策略的列舉
import javax.persistence.Id;// 匯入主鍵註解

@Entity // 告訴 Spring Boot：這是一個實體類別，請在資料庫中自動建立一個名為 RESERVATION 的表格
public class Reservation {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)//讓資料庫自動填寫 ID
    private Long id;
    private String customerName;
    private String carName;
    private String date;

    
    public Reservation() { //空建構子
    }

    //有參數的建構子
    public Reservation(String customerName, String carName, String date) {
        this.customerName = customerName;
        this.carName = carName;
        this.date = date;
    }

    //Getter & Setter(取出&存入)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}