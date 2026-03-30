package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(NoOpPasswordEncoder.getInstance()); 
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // 1. 只有管理員 (ADMIN) 才能操作的路徑
                .antMatchers("/car/add", "/car/save", "/delete-car/**").hasRole("ADMIN")
                
                // 2. 需要登入 (一般會員或管理員) 才能訪問的頁面
                .antMatchers("/sell", "/reserve", "/my-reservations").authenticated()
                
                // 3. 其他所有頁面 (包含首頁、登入頁、圖片等) 都開放所有人看
                .anyRequest().permitAll() 
            .and()
            .formLogin()
                .loginPage("/login")           // 指定使用你自己的登入頁面路徑
                .loginProcessingUrl("/login")  // 告訴 Security 處理登入 POST 請求的網址
                .usernameParameter("email")    // 重要：告訴系統你的帳號欄位名稱叫做 email
                .passwordParameter("password") // 告訴系統你的密碼欄位名稱叫做 password
                .defaultSuccessUrl("/", true)  // 登入成功跳轉首頁
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout")          // 指定登出路徑
                .logoutSuccessUrl("/")         // 登出後回首頁
                .permitAll()
            .and()
            .csrf().disable();
    }
}