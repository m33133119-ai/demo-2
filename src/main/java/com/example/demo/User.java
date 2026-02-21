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

    public User() {} // 必要空建構子

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    // --- 以下是 Getter 和 Setter (可用右鍵 -> Source -> Generate 生成) ---
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
