package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
public class CarController {
	
	private static List<Reservation> reservationList = new ArrayList<>();
	
	@Autowired
    private ReservationRepository reservationRepository;
	
	@Autowired
    private CarRepository carRepository;
	
	@Autowired
    private UserRepository userRepository;

	@GetMapping("/sell")
	public String showSellPage(HttpSession session, Model model) {
	    
	    if (session.getAttribute("user") == null) {
	        
	        model.addAttribute("message", "🔒 請先登入帳號，才能提交賣車申請！");
	        return "login"; 
	    }

	   
	    String nickname = (String) session.getAttribute("nickname");
	    model.addAttribute("nickname", nickname);
	    
	    return "sell"; 
	}

	@PostMapping("/submit-car")
	public String handleSellForm(
	        @RequestParam String name, 
	        @RequestParam Integer year, 
	        @RequestParam String price, 
	        @RequestParam String phone, 
	        Model model) { 

	    
	    System.out.println("收到賣車申請：" + name + "，電話：" + phone);
	    
	   
	    model.addAttribute("cars", getFullCarList());
	    model.addAttribute("message", "✅ 提交成功！我們已收到您的「" + name + "」賣車申請，專員將盡快聯絡。");
	    
	    
	    return "car"; 
	}
	
	@GetMapping("/reserve")
	public String showReservePage(@RequestParam(name = "carName", required = false) String carName, 
	                             HttpSession session, // ✨ 注入 Session 進行檢查
	                             Model model) {
	    
	    
	    if (session.getAttribute("user") == null) {
	       
	        model.addAttribute("message", "🔒 請先登入帳號，才能預約賞車！");
	        return "login"; 
	    }

	    
	    List<Car> allCars = getFullCarList();
	    String nickname = (String) session.getAttribute("nickname");
	    
	    model.addAttribute("allCars", allCars);
	    model.addAttribute("nickname", nickname); // 讓預約頁面的導覽列也能顯示名字
	    model.addAttribute("selectedCar", carName);
	    model.addAttribute("reservation", new Reservation());
	    return "reserve"; 
	}

	
	@PostMapping("/submit-reserve")
	public String handleReserve(@RequestParam String customerName, 
	                            @RequestParam String carName, 
	                            @RequestParam String date, 
	                            HttpSession session, // ✨ 注入 Session
	                            Model model) {

	    reservationList.add(new Reservation(customerName, carName, date));

	    
	    String nickname = (String) session.getAttribute("nickname");
	    if (nickname == null) {
	        session.setAttribute("nickname", customerName);
	        nickname = customerName;
	    }
	    
	    model.addAttribute("nickname", nickname); 
	    model.addAttribute("cars", getFullCarList());
	    model.addAttribute("message", "✅ 預約成功！");
	    return "car"; 
	}


	@GetMapping("/login")
	public String showLoginPage() {
        return "login"; 
    }
	
	@GetMapping("/register")
	public String showRegisterPage() {
	    return "register";
	}

	
	@PostMapping("/register")
	public String handleRegister(
	        @RequestParam String name, 
	        @RequestParam String email,
	        @RequestParam String password,
	        @RequestParam String confirmPassword,
	        Model model) {
	    
	    
	    if (!password.equals(confirmPassword)) {
	        model.addAttribute("message", "❌ 註冊失敗：兩次輸入的密碼不一致！");
	        return "register"; 
	    }

	    
	    if (!password.matches("\\d{8}")) {
	        model.addAttribute("message", "❌ 註冊失敗：密碼必須是剛好 8 位數字！");
	        return "register";
	    }

	    
	    User newUser = new User();
	    newUser.setName(name);
	    newUser.setEmail(email);
	    newUser.setPassword(password);
	    
	    userRepository.save(newUser);
	    
	    model.addAttribute("message", "🎉 註冊成功！密碼格式正確。請登入。");
	    return "login"; 
	}

    
	@PostMapping("/login")
	public String handleLogin(@RequestParam String username, 
	                          @RequestParam String password, 
	                          HttpSession session, 
	                          Model model) {
	    
	    
	    if (!password.matches("\\d{8}")) {
	        model.addAttribute("message", "❌ 格式錯誤：請輸入 8 位數字密碼！");
	        return "login";
	    }

	    
	    User user = userRepository.findByEmail(username);

	   
	    if (user != null && user.getPassword().equals(password)) {
	        session.setAttribute("nickname", user.getName());
	        session.setAttribute("user", user);
	        
	        model.addAttribute("cars", getFullCarList());
	        return "car"; 
	    } else {
	        model.addAttribute("message", "❌ 帳號不存在或密碼錯誤！");
	        return "login";
	    }
	}

	
	private List<Car> getFullCarList() {
	    List<Car> carList = new ArrayList<>();
	    
	    
	    carList.add(new Car(1L, "BMW 3-Series Sedan", "$1,480,000", "2021 年份", "/images/Bmw3.jpg", 
	                "2.0L 汽油", "手自排", 25000, Arrays.asList("天窗", "感應尾門", "ACC自適應巡航")));
	    
	    carList.add(new Car(2L, "Mercedes-Benz C-Class", "$1,280,000", "2019 年份", "/images/Benz.jpg", 
	                "1.5L 汽油", "九速手自排", 48000, Arrays.asList("倒車顯影", "盲點偵測", "電動座椅")));
	    
	    carList.add(new Car(3L, "Toyota RAV4 Hybrid", "$980,000", "2022 年份", "/images/Rav4.jpeg", 
	                "2.5L 油電", "E-CVT", 12000, Arrays.asList("通風座椅", "360環景", "電動尾門")));
	    
	    carList.add(new Car(4L, "Porsche 911", "$5,200,000", "2023 年份", "/images/porche.jpg", 
	                "3.0L 汽油", "PDK雙離合", 3500, Arrays.asList("跑車排氣", "PDLS頭燈", "真皮內裝")));
	    
	    return carList;
	}
	
	@GetMapping("/my-reservations")
	public String showMyReservations(HttpSession session, Model model) {
	    
	    String nickname = (String) session.getAttribute("nickname");
	    if (nickname != null) {
	        model.addAttribute("nickname", nickname);
	    }
	    List<Reservation> dbReservations = reservationRepository.findAll();
	    model.addAttribute("reservations", dbReservations);
	    return "my-reservations"; 
	}
	
	@GetMapping("/delete-reservation/{index}")
	public String deleteReservation(@PathVariable int index) {
	   
	    if (index >= 0 && index < reservationList.size()) {
	        reservationList.remove(index);
	    }
	   
	    return "redirect:/my-reservations";
	}
	
	@GetMapping({"/", "/index"})
	public String showIndex(@RequestParam(name = "keyword", required = false) String keyword, 
	                        HttpSession session, 
	                        Model model) {
	    
	   
	    String nickname = (String) session.getAttribute("nickname");
	    if (nickname != null) {
	        model.addAttribute("nickname", nickname);
	    }

	    
	    List<Car> filteredCars;

	    if (keyword != null && !keyword.trim().isEmpty()) {
	       
	        filteredCars = carRepository.findByNameContainingIgnoreCase(keyword);
	        model.addAttribute("message", "您搜尋的關鍵字是：「" + keyword + "」");
	    } else {
	     
	        filteredCars = carRepository.findAll();
	        
	        if (nickname == null) {
	            model.addAttribute("message", "歡迎來到卓越中古車，現有 " + filteredCars.size() + " 台精選好車！");
	        } else {
	            model.addAttribute("message", "👋 歡迎回來，" + nickname + "！");
	        }
	    }

	    model.addAttribute("cars", filteredCars);
	    return "car";
	}
	
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
	    session.invalidate(); 
	    return "redirect:/index";
	}
	
	@GetMapping("/forgot-password")
	public String showForgotPasswordPage() {
	    return "forgot-password"; 
	}
	
	@PostMapping("/reset-password")
	public String handleResetPassword(@RequestParam String name, 
	                                  @RequestParam String email, 
	                                  @RequestParam String newPassword, 
	                                  Model model) {
	    
	    
	    if (newPassword == null || !newPassword.matches("\\d{8}")) {
	        System.out.println("❌ 格式檢查失敗：新密碼不符合 8 位數字規則。");
	        model.addAttribute("message", "❌ 重設失敗：新密碼必須是 8 位數字！");
	        return "forgot-password";
	    }

	    User user = userRepository.findByEmail(email);
	    
	    System.out.println("=== 重設密碼偵錯中 ===");
	    System.out.println("表單輸入的名字: [" + name + "]");
	    System.out.println("表單輸入的 Email: [" + email + "]");
	    
	    if (user != null) {
	        System.out.println("資料庫裡的人名: [" + user.getName() + "]");
	        
	        
	        if (user.getName().trim().equals(name.trim())) {
	            user.setPassword(newPassword); 
	            userRepository.save(user); 
	            System.out.println("✅ 比對成功！密碼已存入資料庫。");
	            model.addAttribute("message", "✅ 密碼已成功重設！請使用新密碼登入。");
	            return "login";
	        } else {
	            System.out.println("❌ 名字比對失敗！請檢查空格或大小寫。");
	        }
	    } else {
	        System.out.println("❌ 找不到這個 Email 的使用者。");
	    }
	    
	    model.addAttribute("message", "❌ 驗證失敗：姓名或 Email 不正確！");
	    return "forgot-password";
	}
	
	@PostMapping("/submit-reservation")
	public String handleReservation(@RequestParam String name, 
	                                @RequestParam String carName, 
	                                @RequestParam String date, 
	                                Model model) {
	    
	    
	    LocalDate selectedDate = LocalDate.parse(date);
	    LocalDate today = LocalDate.now();

	    
	    if (selectedDate.isBefore(today)) {
	        model.addAttribute("message", "❌ 預約失敗：日期不能選擇過去的時間！");
	        
	        model.addAttribute("allCars", getFullCarList());
	        return "reserve";
	    }

	    
	    System.out.println("✅ 預約成功：[" + name + "] 預約了 [" + carName + "] 於 " + date);
	    model.addAttribute("message", "🎉 預約成功！我們將會致電與您確認。");
	    return "index";
	}
	
	@PostMapping("/reserve")
	public String submitReservation(@ModelAttribute("reservation") Reservation reservation, 
	                                HttpSession session, 
	                                Model model) {
	    
	    
	    if (session.getAttribute("user") == null) {
	        return "redirect:/login";
	    }

	    
	    reservationRepository.save(reservation);
	    
	  
	    System.out.println("成功儲存預約！預約人：" + reservation.getCustomerName());
	    return "redirect:/index"; 
	}
}
