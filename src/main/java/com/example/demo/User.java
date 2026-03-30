package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name = "users") 
public class User {
    @Id 
    private String email; 
    private String name;
    private String password;
    private String role; // 👈 必須補上這一行，才能讀到資料庫裡的 ADMIN 字樣

    public User() {} 

    public User(String email, String name, String password, String role) { // 👈 建構子同步更新
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    // Getter & Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    // 👈 補上 role 的 Getter 和 Setter
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
