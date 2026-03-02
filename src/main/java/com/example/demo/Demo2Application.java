package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  
//這是 Spring Boot 專案。
//開啟自動配置（自動幫你設定好 Tomcat 伺服器、H2 資料庫連線等）。
//自動掃描（掃描同級資料夾下的 @Controller, @Service, @Component 等標籤）。

public class Demo2Application {
    public static void main(String[] args) {
        SpringApplication.run(Demo2Application.class, args);
    }
}
//這行指令會啟動 Spring 框架：
// 1. 啟動內建的 Tomcat 伺服器（讓你能用 localhost:8080 看到網頁）。
// 2. 建立 Spring 容器（把你的 Repository, Controller 通通載入進去）。
// 3. 執行你寫的 DataInitializer（把那 4 台車存進 H2）。