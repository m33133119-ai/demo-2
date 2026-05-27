package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository; // 假設你有這個用來存 User 的 Repository

    @Autowired
    private JavaMailSender mailSender; // Spring Boot 內建的寄信工具

   
    public VerificationToken createTokenForUser(User user) {
        VerificationToken token = new VerificationToken(user);
        return tokenRepository.save(token); // 存進資料庫
    }

   
    public void sendVerificationEmail(User user, String tokenString) {
       
        String verifyUrl = "http://localhost:8080/verifyEmail?token=" + tokenString;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail()); 
        message.setSubject("【中古車媒合平台】請驗證您的 Email 帳號"); 
        message.setText("親愛的 " + user.getName() + " 您好：\n\n"
                + "感謝您註冊本平台！請點擊下方連結以啟用您的帳號：\n"
                + verifyUrl + "\n\n"
                + "(此連結將於 24 小時後失效)");

        mailSender.send(message); 
    }

    
    public String verifyUserAccount(String tokenString) {
        VerificationToken token = tokenRepository.findByToken(tokenString);

        if (token == null) {
            return "無效的驗證碼！";
        }
        if (token.isExpired()) {
            return "驗證碼已過期，請重新申請！";
        }

        
        User user = token.getUser();
        user.setEnabled(true);
        userRepository.save(user); 

       
        tokenRepository.delete(token);

        return "帳號啟用成功！歡迎開始使用中古車平台。";
    }
}
