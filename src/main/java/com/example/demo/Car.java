package com.example.demo;    //Java物件與MariaDB資料表連接

import java.util.List;
import javax.persistence.*;
import com.example.demo.enums.CarStatus;

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

    //負責把 Java的Enum翻譯給資料庫
    @Enumerated(EnumType.STRING)   
    private CarStatus status = CarStatus.LISTED;
    
    //啟動分表機制
    @ElementCollection            
    @CollectionTable(name = "car_features", joinColumns = @JoinColumn(name = "car_id"))  //設定新表格
    @Column(name = "features") //設定資料欄位
    private List<String> features;

    
    public Car() {}

    //建構子
    public Car(Long id, String name, String price, String year, String image, 
               String fuelType, String transmission, String mileage, CarStatus status, List<String> features) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.year = year;
        this.image = image;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.mileage = mileage;   
        this.status = status;    
        this.features = features;
    }
    
    //Getter&Setter
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

    
    public CarStatus getStatus() { return status; }
    public void setStatus(CarStatus status) { this.status = status; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    
    //建構子(全新上架)
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
        this.status = CarStatus.LISTED; 
    }
}


  