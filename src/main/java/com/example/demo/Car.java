package com.example.demo;

import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String price;
    
    @Column(name = "car_year")
    private String year;
    
    private String image;
    private String fuelType;
    private String transmission;
    private String mileage;
    private String phone;
    private String sellerName;
    // 預設給予 "已上架"，這樣管理員新增時就不用手動填
    private String status = "已上架";

    @ElementCollection
    @CollectionTable(name = "car_features", joinColumns = @JoinColumn(name = "car_id"))
    @Column(name = "features")
    private List<String> features;

    // 1. 無參數建構子 (JPA 必備)
    public Car() {}

 // 在 Car.java 裡面，請把這段貼上去替換舊的
    public Car(Long id, String name, String price, String year, String image, 
               String fuelType, String transmission, String mileage, String status, List<String> features) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.year = year;
        this.image = image;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.mileage = mileage;   // 這裡確保是 String
        this.status = status;    // 這裡確保有 status
        this.features = features;
    }
    

    // --- Getter & Setter ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getMileage() { return mileage; }
    public void setMileage(String mileage) { this.mileage = mileage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getSellerName() {
        return sellerName;
    }
    
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    // 🚨 補上這個，網頁才讀得到配備清單
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    
 // 💡 這是「相容模式」建構子：即使沒傳 status，我們也預設它是 "已上架"
    public Car(Long id, String name, String price, String year, String image, 
               String fuelType, String transmission, String mileage, List<String> features) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.year = year;
        this.image = image;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.mileage = mileage;
        this.features = features;
        this.status = "已上架"; // 預設值
    }
}


  