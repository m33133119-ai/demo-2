
package com.example.demo;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 隨機產生的權杖字串
    @Column(nullable = false, unique = true)
    private String token;

    // 👈 關鍵：關聯到你的 User 類別
    // 因為你的 User ID 是 String 類型的 email，這裡會自動對應
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_email")
    private User user;

    // 過期時間
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // 預設建構子 (JPA 必要)
    public VerificationToken() {}

    // 方便使用的建構子
    public VerificationToken(User user) {
        this.user = user;
        this.token = UUID.randomUUID().toString(); // 自動產生隨機亂碼
        this.expiryDate = LocalDateTime.now().plusHours(24); // 預設 24 小時後過期
    }

    // --- Getter & Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    // 輔助方法：檢查是否過期
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}