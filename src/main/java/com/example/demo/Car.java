package com.example.demo;              //定義一張名為 cars 的資料表，以及操作這張表資料的所有規則。

import java.util.List;                 //匯入list 指令
import javax.persistence.*;            //資料持久化

@Entity                                //宣告類別為實體物件
@Table(name = "cars")                  //將此類別映射到資料庫的 cars 表
public class Car {
    @Id                                                     //主鍵標記
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //自動生成值
    private Long id;                                        //建立一個私有的、長整數類型的識別碼
    
    private String name;
    private String price;
    @Column(name = "car_year")         //強制將 Java 的 year 變數對應到資料庫中名為 car_year 的欄位
    private String year;
    private String image;
    private String fuelType;
    private String transmission;
    private int mileage;
    
    @ElementCollection                  //建立一個額外的子表來儲存清單資料
    private List<String> features;

    
    public Car() {}                     //JPA 規定必須要有無參數建構子，否則從資料庫撈資料會噴錯

    
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
    }                                     //參數化建構子
    
    
    
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setYear(String year) { this.year = year; }
    public void setImage(String image) { this.image = image; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public void setFeatures(List<String> features) { this.features = features; }
    //Setter 可修改的資料
    
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
    //Getter 可讀取的資料

  