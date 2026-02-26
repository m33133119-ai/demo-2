package com.example.demo;         //處理程序

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
import javax.annotation.PostConstruct;


@Controller
public class CarController {
	
	private static List<Reservation> reservationList = new ArrayList<>(); //建立一個名為 reservationList 的清單
	
	@Autowired       //依賴注入
    private ReservationRepository reservationRepository;    //連結預約資料表RESERVATION的專屬窗口。
	
	@Autowired
    private CarRepository carRepository;                    //連結汽車資料表 (CARS)的窗口。
	
	@Autowired
    private UserRepository userRepository;                  //連結使用者資料表 (USERS)的窗口

	
	@GetMapping("/sell")
	public String showSellPage(HttpSession session, Model model) {
	    
	    if (session.getAttribute("user") == null) {
	        
	        model.addAttribute("message", "🔒 請先登入帳號，才能提交賣車申請！");
	        return "login"; 
	    }                                                   //登入管理:必須先登入才能賣車

	   
	    String nickname = (String) session.getAttribute("nickname");
	    model.addAttribute("nickname", nickname);
	    
	    return "sell"; 
	}                                                       //登入管理:登入後跳轉到賣車業面

	@PostMapping("/submit-car")
	public String handleSellForm(
	        @RequestParam String name, 
	        @RequestParam String year, 
	        @RequestParam String price, 
	        @RequestParam String phone, 
	        Model model) { 

	    
	    System.out.println("收到賣車申請：" + name + "，電話：" + phone);
	    Car newCar = new Car();                      //後台確認表單存入
        newCar.setName(name);
        newCar.setYear(year);
        newCar.setPrice(price);
        carRepository.save(newCar);                  //保存資料
	   
        model.addAttribute("cars", carRepository.findAll());
        //重新去資料庫撈出「目前所有車輛」的最新清單
        
        model.addAttribute("message", "✅ 提交成功！我們已收到您的「" + name + "」賣車申請，專員將盡快聯絡。");
	    
	    
	    return "car"; 
	}                                          //賣車表單處理
	
	@GetMapping("/reserve")   
	public String showReservePage(@RequestParam(name = "carName", required = false) String carName, 
	                             HttpSession session, // ✨ 注入 Session 進行檢查
	                             Model model) {
	    
	    
	    if (session.getAttribute("user") == null) {
	       
	        model.addAttribute("message", "🔒 請先登入帳號，才能預約賞車！");
	        return "login"; 
	    } //登入成功才能預約

	    
	    List<Car> allCars = carRepository.findAll();
	    String nickname = (String) session.getAttribute("nickname"); //預約右上角顯示名字
	    
	    model.addAttribute("allCars", allCars);
	    model.addAttribute("nickname", nickname); // 讓預約頁面的導覽列也能顯示名字
	    model.addAttribute("selectedCar", carName);
	    model.addAttribute("reservation", new Reservation());//預建一個空的預約單物件
	    return "reserve"; 
	}   //預約車輛功能(準備表單)
	

	
	@PostMapping("/submit-reserve")
	public String handleReserve(@RequestParam String customerName, 
	                            @RequestParam String carName, 
	                            @RequestParam String date, 
	                            HttpSession session, // ✨ 注入 Session
	                            Model model) {
		//正式改用 Repository 存入資料庫
		Reservation res = new Reservation();
	    res.setCustomerName(customerName);
	    res.setCarName(carName);
	    res.setDate(date);
	    
	    // 呼叫資料庫管理員進行存檔
	    reservationRepository.save(res);
	    
	    // 2. 處理 Session 暱稱邏輯
	    String nickname = (String) session.getAttribute("nickname");
	    if (nickname == null) {
	        session.setAttribute("nickname", customerName);
	        nickname = customerName;
	    }
	    
	    // 3. 準備回傳給首頁的資料
	    model.addAttribute("nickname", nickname); 
	    model.addAttribute("cars", carRepository.findAll()); // 建議改用 Repository 抓取最新車單
	    model.addAttribute("message", "✅ 預約成功！");
	    return "car"; 
	}    //預約功能(處存資料)


	@GetMapping("/login")               // 當使用者在網頁網址輸入 /login 或點擊「登入」連結時觸發
	public String showLoginPage() {     // 單純回傳 "login"，告訴 Spring Boot 去 templates 資料夾找 login.html 檔案顯示出來
        return "login"; 
    }
	
	@GetMapping("/register")            // 當使用者在網址列輸入 /register，或點擊「加入會員」時觸發
	public String showRegisterPage() {  // 告訴 Spring Boot 去 templates 資料夾找到名為 register.html 的檔案並呈現給使用者
	    return "register";
	}

	@PostMapping("/register")          //// 處理註冊表單提交 (POST)
	public String handleRegister(
	        @RequestParam String name, 				// 接收使用者姓名/暱稱
	        @RequestParam String email,             // 接收 Email
	        @RequestParam String password,          //接收密碼
	        @RequestParam String confirmPassword,   // 接收第二次確認密碼
	        Model model) {
	    
	    //密碼一致性
	    if (!password.equals(confirmPassword)) {
	        model.addAttribute("message", "❌ 註冊失敗：兩次輸入的密碼不一致！");
	        return "register"; 
	    }

	    //密碼格式檢查
	    if (!password.matches("\\d{8}")) {
	        model.addAttribute("message", "❌ 註冊失敗：密碼必須是剛好 8 位數字！");
	        return "register";
	    }

	    //存入資料庫
	    User newUser = new User();
	    newUser.setName(name);
	    newUser.setEmail(email);
	    newUser.setPassword(password);
	    
	    userRepository.save(newUser);       // 呼叫 User 資料庫管理員，將新會員存檔
	    
	    model.addAttribute("message", "🎉 註冊成功！密碼格式正確。請登入。");     // 註冊成功後，帶著成功訊息導向「登入頁面」
	    return "login"; 
	}

    
	@PostMapping("/login")         //處理登入驗證
	public String handleLogin(@RequestParam String username,    // 接收表單傳來的 Email
	                          @RequestParam String password,    // 接收表單傳來的密碼
	                          HttpSession session,              // 用於存儲使用者狀態
	                          Model model) {
	    
	    
	    if (!password.matches("\\d{8}")) {
	        model.addAttribute("message", "❌ 格式錯誤：請輸入 8 位數字密碼！");
	        return "login";
	    }

	    //使用 Repository 透過 Email 查詢資料庫中的使用者
	    User user = userRepository.findByEmail(username);

	   //如果找到了人 (user != null) 且 資料庫裡的密碼跟輸入的一模一樣
	    if (user != null && user.getPassword().equals(password)) {
	        session.setAttribute("nickname", user.getName());
	        session.setAttribute("user", user);
	        
	        model.addAttribute("cars", carRepository.findAll());  // 準備首頁要看的車子清單
	        return "car"; 
	    } else {
	    	// 失敗：帳號不存在或密碼比對失敗
	        model.addAttribute("message", "❌ 帳號不存在或密碼錯誤！");
	        return "login";
	    }
	}

	
	
	// 在 Controller 啟動時檢查資料庫是否為空
	@PostConstruct
	public void initData() {
	    // 1. 檢查資料庫是否已經有資料，避免重複新增
	    if (carRepository.count() == 0) {
	        
	        // 2. 建立資料並直接存入 Repository
	        carRepository.save(new Car(null, "BMW 3-Series Sedan", "$1,480,000", "2021 年份", "/images/Bmw3.jpg", 
	                    "2.0L 汽油", "手自排", 25000, Arrays.asList("天窗", "感應尾門", "ACC自適應巡航")));
	        
	        carRepository.save(new Car(null, "Mercedes-Benz C-Class", "$1,280,000", "2019 年份", "/images/Benz.jpg", 
	                    "1.5L 汽油", "九速手自排", 48000, Arrays.asList("倒車顯影", "盲點偵測", "電動座椅")));
	        
	        carRepository.save(new Car(null, "Toyota RAV4 Hybrid", "$980,000", "2022 年份", "/images/Rav4.jpeg", 
	                    "2.5L 油電", "E-CVT", 12000, Arrays.asList("通風座椅", "360環景", "電動尾門")));
	        
	        carRepository.save(new Car(null, "Porsche 911", "$5,200,000", "2023 年份", "/images/porche.jpg", 
	                    "3.0L 汽油", "PDK雙離合", 3500, Arrays.asList("跑車排氣", "PDLS頭燈", "真皮內裝")));
	        
	        System.out.println("✅ 已成功初始化 H2 資料庫的汽車範例資料！");
	    }
	}
	
	// 顯示「我的預約」清單頁面
	@GetMapping("/my-reservations")     // 當使用者存取 /my-reservations 網址時觸發
	public String showMyReservations(HttpSession session, Model model) {
		
		// 1. 從 Session 中嘗試取得使用者的暱稱
	    String nickname = (String) session.getAttribute("nickname");
	    
	    // 如果暱稱存在（代表使用者已登入），則傳遞給 Model，讓網頁導覽列能顯示「歡迎，某某某」
	    if (nickname != null) {
	        model.addAttribute("nickname", nickname);
	    }
	    // 2. 從資料庫中取出「所有的」預約紀錄
	    List<Reservation> dbReservations = reservationRepository.findAll();
	    
	    // 3. 將撈出來的預約清單放入 Model，名稱為 "reservations"
	    model.addAttribute("reservations", dbReservations);
	    
	    return "my-reservations"; 
	}
	
	// 刪除特定一筆預約紀錄
	@GetMapping("/delete-reservation/{id}")        // 確保這裡是接收資料庫的 ID
	public String deleteReservation(@PathVariable Long id) {   // @PathVariable 會自動把網址後方的數字抓下來，傳進變數 index 中
	   
		// 直接呼叫 repository 從資料庫中刪除這筆 ID 對應的資料
	    reservationRepository.deleteById(id);
	   
	    // 刪除後重新導向，這時 showMyReservations 會重新從資料庫撈取「最新」清單
	    return "redirect:/my-reservations";
	}
	
	// 顯示首頁 (支援搜尋功能)
	@GetMapping({"/", "/index"})     // 支援兩個路徑，無論輸入首頁網址或 /index 都會進入此方法
	public String showIndex(@RequestParam(name = "keyword", required = false) String keyword, 
	                        HttpSession session, 
	                        Model model) {
	    
		// 1. 檢查 Session：確認使用者是否已登入，若有則拿到暱稱
	    String nickname = (String) session.getAttribute("nickname");
	    if (nickname != null) {
	        model.addAttribute("nickname", nickname);
	    }

	    // 2. 宣告一個清單來存放準備顯示的車輛
	    List<Car> filteredCars;
	    
	    // --- 搜尋模式 ---
        // 如果使用者有輸入關鍵字 (不為空且不只有空格)
        // 使用 Repository 進行「模糊查詢」且「忽略大小寫
	    if (keyword != null && !keyword.trim().isEmpty()) {
	        filteredCars = carRepository.findByNameContainingIgnoreCase(keyword);
	        model.addAttribute("message", "您搜尋的關鍵字是：「" + keyword + "」");
	    } else {
	    	// --- 一般模式 ---
	        // 如果沒有搜尋，就從資料庫撈出「所有」車輛
	        filteredCars = carRepository.findAll();
	        
	     // 根據是否登入，給予不同的歡迎訊息
	        if (nickname == null) {
	            model.addAttribute("message", "歡迎來到卓越中古車，現有 " + filteredCars.size() + " 台精選好車！");
	        } else {
	            model.addAttribute("message", "👋 歡迎回來，" + nickname + "！");
	        }
	    }
	    // 4. 將最後篩選出的車輛清單傳給前端
	    model.addAttribute("cars", filteredCars);
	    return "car";
	}
	
	
	@GetMapping("/logout")   // 當使用者點擊「登出」按鈕時觸發
	public String logout(HttpSession session) {
	    session.invalidate(); 
	    return "redirect:/index";
	}
	
	@GetMapping("/forgot-password")     // 當使用者在登入頁點擊「忘記密碼」時觸發        
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
	        
	        model.addAttribute("allCars", carRepository.findAll());
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
