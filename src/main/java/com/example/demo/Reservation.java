package com.example.demo; //在資料庫裡面幫我建立一張名為 Reservation 的表格，並按照我定義的欄位來存資料。

import javax.persistence.Entity; 
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity 
public class Reservation {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private String carName;
    private String date;
    private String phone;

    
    public Reservation() { 
    }

    
    public Reservation(String customerName, String carName, String date) {
        this.customerName = customerName;
        this.carName = carName;
        this.date = date;
    }

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
}