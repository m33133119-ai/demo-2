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
                
                .antMatchers("/car/add", "/car/save", "/delete-car/**").hasRole("ADMIN")
                
                
                .antMatchers("/sell", "/reserve", "/my-reservations").authenticated()
                
                
                .anyRequest().permitAll() 
            .and()
            .formLogin()
                .loginPage("/login")           
                .loginProcessingUrl("/login")  
                .usernameParameter("email")    
                .passwordParameter("password") 
                .defaultSuccessUrl("/", true)  
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout")          
                .logoutSuccessUrl("/")         
                .permitAll()
            .and()
            .csrf().disable();
    }
}