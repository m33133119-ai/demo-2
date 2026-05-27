package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;


import javax.servlet.http.HttpSession; // ★ 新增這個 Import：用來管理 Session (VIP 通行證)

@RestController // 告訴 Spring Boot 這是一個負責處理網址請求的接待員，且回傳格式為純文字或 JSON
public class UserController {

    @Autowired
    private EmailVerificationService emailService; // 把我們寫好的總技師請過來幫忙

    @Autowired
    private UserRepository userRepository; // 用來操作 User 資料表的庫管員

    /**
     * API 1：處理買家送出的註冊資料
     * 前端呼叫的網址：(POST) http://localhost:8080/api/users/register
     */
    @PostMapping("/api/users/register")
    public ResponseEntity<String> registerNewUser(@RequestBody User user) { // ★ 升級成 ResponseEntity
        
        // 1. 基本防呆：先去資料庫查查看這個 Email 是不是已經註冊過了
        if(userRepository.findById(user.getEmail()).isPresent()){
            // ★ 回傳 400 錯誤狀態，觸發前端顯示紅色錯誤框
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("此信箱已經被註冊過囉！請更換信箱或直接登入。");
        }

        // 2. 儲存基本資料：先把新會員存進資料庫 (這時候 enabled 還是 false 未啟用)
        userRepository.save(user);

        // 3. 建立驗證碼：請總技師幫這名新會員生一張專屬的號碼牌 (Token)
        VerificationToken token = emailService.createTokenForUser(user);

        // 4. 寄送 Email：請總技師叫背景小精靈去發送 Email 
        emailService.sendVerificationEmail(user, token.getToken());

        // 5. 立刻回覆前端：回傳 200 OK 綠燈，觸發前端顯示綠色成功框
        return ResponseEntity.ok("註冊成功！系統已發送驗證信，請前往您的信箱收信並點擊連結來啟用帳號。");
    }

    /**
     * API 2：處理買家從 Email 點擊回來的驗證網址
     * 網址範例：(GET) http://localhost:8080/verifyEmail?token=一長串亂碼
     */
    @GetMapping("/verifyEmail")
    public String verifyAccount(@RequestParam("token") String token) {
        // 把網址上的亂碼 (Token) 交給總技師去資料庫查驗
        return emailService.verifyUserAccount(token);
    }
    
    /**
     * API 3：買家登入
     * 測試網址：(POST) http://localhost:8080/api/users/login
     */
    @PostMapping("/api/users/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginRequest, HttpSession session) { 
        
        User user = userRepository.findById(loginRequest.getEmail()).orElse(null);

        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            // 回傳 401 Unauthorized (未經授權)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登入失敗：帳號或密碼錯誤！");
        }

        if (!user.isEnabled()) {
            // 回傳 403 Forbidden (禁止訪問)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("登入失敗：您的帳號尚未啟用，請先前往信箱點擊驗證連結！");
        }

        // 1. 發放櫃台名牌 (給 Thymeleaf 首頁看的)
        session.setAttribute("loggedInUser", user);

        // ★★★ 2. 關鍵升級：發放官方晶片卡 (給 Spring Security 警衛看的) ★★★
        // 判斷使用者的身分，如果資料庫的 role 欄位是空的，就預設為一般買家 (USER)
        String role = (user.getRole() != null && !user.getRole().isEmpty()) ? "ROLE_" + user.getRole() : "ROLE_USER";
        
        // 製作晶片卡 (包含 Email 與權限)
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user.getEmail(),  
                null,             
                Collections.singletonList(new SimpleGrantedAuthority(role)) 
        );
        
        // 把晶片卡塞進警衛的系統裡 (SecurityContext)
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 回傳 200 OK (成功)
        return ResponseEntity.ok("登入成功！歡迎回來，" + user.getName());
    }

    /**
     * API 4：買家登出
     * 前端呼叫的網址：(GET) http://localhost:8080/api/users/logout
     */
    @GetMapping("/api/users/logout")
    public ResponseEntity<String> logoutUser(HttpSession session) {
        // 1. 撕毀櫃台名牌 (清空 Session)
        session.invalidate(); 
        
        // ★★★ 2. 註銷官方晶片卡 (清空 Spring Security 狀態) ★★★
        SecurityContextHolder.clearContext();
        
        return ResponseEntity.ok("已成功登出！");
    }
}