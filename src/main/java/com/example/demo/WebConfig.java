package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

// @Configuration 標籤非常重要！它會告訴 Spring Boot 這是一個設定檔，啟動時要先讀取它。
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // 1. 取得專案根目錄下 "uploads" 資料夾的絕對路徑
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        
        // 2. 設定映射規則：
        // 當網址請求 "/uploads/**" 時 (兩個星號代表包含該目錄下所有檔案)
        // 就去實體電腦的這個絕對路徑 "file:..." 找檔案
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
