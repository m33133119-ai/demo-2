package com.example.demo; //自動初始化資料庫H2

import org.springframework.boot.CommandLineRunner; // 匯入啟動後執行的介面
import org.springframework.stereotype.Component; // 匯入組件註解
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;

@Component // 告訴 Spring：這是一個功能組件，請在啟動時自動掃描並載入它
public class DataInitializer implements CommandLineRunner {
	// 繼承 CommandLineRunner，這代表程式「一啟動完成」就會立刻執行裡面的內容
	
    @Autowired // 自動注入：讓 Spring 幫你把剛剛寫好的 CarRepository 實例抓過來使用
    private CarRepository carRepository;

    @Override
    public void run(String... args) throws Exception {
        // 先清空資料庫，確保每次重啟都是乾淨的狀態
        carRepository.deleteAll();

        //新增預設資料
        carRepository.save(new Car(null, "BMW 3-Series Sedan", "$1,480,000", "2021", "/images/Bmw3.jpg", 
                "2.0L 汽油", "手自排", 25000, Arrays.asList("天窗", "感應尾門", "ACC自適應巡航")));

        carRepository.save(new Car(null, "Mercedes-Benz C-Class", "$1,280,000", "2019", "/images/Benz.jpg", 
                "1.5L 汽油", "九速手自排", 48000, Arrays.asList("倒車顯影", "盲點偵測", "電動座椅")));

        carRepository.save(new Car(null, "Toyota RAV4 Hybrid", "$980,000", "2022", "/images/Rav4.jpeg", 
                "2.5L 油電", "E-CVT", 12000, Arrays.asList("通風座椅", "360環景", "電動尾門")));

        carRepository.save(new Car(null, "Porsche 911", "$5,200,000", "2023", "/images/porche.jpg", 
                "3.0L 汽油", "PDK雙離合", 3500, Arrays.asList("跑車排氣", "PDLS頭燈", "真皮內裝")));

        System.out.println("--- H2 資料庫初始化完成，已更新為 4 台車 ---");
    }
}
