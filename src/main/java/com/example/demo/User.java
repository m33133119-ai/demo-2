package com.example.demo; //使用者帳號

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity // 告訴 JPA 這是一個對應資料庫表格的類別
@Table(name = "users") // 明確指定資料庫裡的表格名稱叫做 "users" (避免與資料庫內建關鍵字 USER 衝突)
public class User {
    @Id // 標記 email 為主鍵 (Primary Key)
    private String email; 
    private String name;
    private String password;

    public User() {} // 必要空建構子

    public User(String email, String name, String password) { //有參數建構子：
        this.email = email;
        this.name = name;
        this.password = password;
    }

    //Getter & Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
