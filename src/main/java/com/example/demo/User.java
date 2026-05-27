package com.example.demo;

import javax.persistence.Column; 
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
    private String role; 

    
    @Column(name = "enabled")
    private boolean enabled = false; 

    public User() {} 

    public User(String email, String name, String password, String role) { 
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        
        this.enabled = false; 
    }

    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    
    public boolean isEnabled() { return enabled; } // 注意：boolean 類型的 getter 習慣命名為 isXXX
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
