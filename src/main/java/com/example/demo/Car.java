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
    private int mileage;
    
    @ElementCollection
    private List<String> features;

    // ✨ 1. 新增：無參數建構子 (Spring Data JPA 強制要求)
    public Car() {}

    // 2. 原有的參數化建構子 (保留沒關係)
    public Car(Long id, String name, String price, String year, String image, 
               String fuelType, String transmission, int mileage, List<String> features) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.year = year;
        this.image = image;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.mileage = mileage;
        this.features = features;
    }
    
    // ✨ 3. 新增：所有欄位的 Setter 方法 (解決 DataInitializer 的紅字)
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setYear(String year) { this.year = year; }
    public void setImage(String image) { this.image = image; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public void setFeatures(List<String> features) { this.features = features; }
    
    // 原有的 Getter
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getYear() { return year; }
    public String getImage() { return image; }
    public String getFuelType() { return fuelType; }
    public String getTransmission() { return transmission; }
    public int getMileage() { return mileage; }
    public List<String> getFeatures() { return features; }
}

  