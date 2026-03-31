package com.example.demo;         //處理程序


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
import org.springframework.security.access.prepost.PreAuthorize;
import java.security.Principal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;

@Controller
public class CarController {
	
	
	
	@Autowired                                              //依賴注入
    private ReservationRepository reservationRepository;    //連結預約資料表RESERVATION的專屬窗口。
	
	@Autowired
    private CarRepository carRepository;                    //連結汽車資料表 (CARS)的窗口。
	
	@Autowired
    private UserRepository userRepository;                  //連結使用者資料表 (USERS)的窗口

	
	@GetMapping("/sell")       //登入管理:必須先登入才能賣車
	public String showSellPage() {
	    // 交給 Spring Security 保護，這裡只要單純回傳 HTML 檔名即可
	    return "sell"; 
	}                                                  

	@PostMapping("/submit-car")      //登入管理:登入後跳轉到賣車業面
	public String handleSellForm(
	        @RequestParam String name, 
	        @RequestParam String year, 
	        @RequestParam String price, 
	        @RequestParam String phone, 
	        @RequestParam String sellerName,
	        RedirectAttributes redirectAttributes) { // 🚨 改用 RedirectAttributes 來傳遞訊息

	    System.out.println("收到賣車申請：" + name + "，電話：" + phone); 
	    Car newCar = new Car();                                         
	    newCar.setName(name);
	    newCar.setSellerName(sellerName);
	    newCar.setYear(year);
	    newCar.setPrice(price);
	    newCar.setPhone(phone);
	    
	    // 💡 救援行動：幫沒有填寫的欄位塞入「預設值」，避免資料庫因為 null 而報錯
	    newCar.setMileage("未知");
	    newCar.setFuelType("未知");
	    newCar.setTransmission("未知");
	    newCar.setImage("default.jpg"); // 隨便給個預設圖片名稱避免破圖
	    
	    
	    // 標記為待審核
	    newCar.setStatus("待審核");
	    System.out.println("【偵錯】準備存入資料庫，這台車的狀態目前是：" + newCar.getStatus());

	    carRepository.save(newCar); // 存入資料庫，現在不會報錯了！
	    
	    // 🚨 使用 flash 屬性傳遞訊息，這樣跳轉後訊息還會保留一次
	    redirectAttributes.addFlashAttribute("message", "✅ 提交成功！我們已收到您的「" + name + "」賣車申請，專員將盡快聯絡。");
	    
	    // 🚨 改為 Redirect，讓網址乾淨地回到首頁
	    return "redirect:/index"; 
	}                                                 
	
	@GetMapping("/reserve")   //登入成功才能預約
	public String showReservePage(Model model, @RequestParam(required = false) String carName) {
	    
	    // 1. 準備一個空的預約物件，給 Thymeleaf 表單裝資料用
	    Reservation reservation = new Reservation(); 
	    
	    // 2. 如果網址有傳遞車名過來 (例如從首頁點擊預約賞車)，就自動填入車名
	    if (carName != null) {
	        reservation.setCarName(carName); 
	    }
	    
	    // 3. 把這個物件命名為 "reservation" 傳給前端網頁，解決 500 錯誤！
	    model.addAttribute("reservation", reservation); 
	    model.addAttribute("cars", carRepository.findByStatus("已上架"));
	    return "reserve";
	}                                                
	

	
	@PostMapping("/submit-reserve")//預約功能(處存資料)
	public String handleReserve(@RequestParam String customerName, 
	                            @RequestParam String carName, 
	                            @RequestParam String date, 
	                            HttpSession session,         // ✨ 注入 Session
	                            Model model) {
		                                                     
		Reservation res = new Reservation();//正式改用 Repository 存入資料庫
	    res.setCustomerName(customerName);
	    res.setCarName(carName);
	    res.setDate(date);
	    
	                                                         
	    reservationRepository.save(res);  // 呼叫資料庫管理員進行存檔
	    
	                                                         
	    String nickname = (String) session.getAttribute("nickname");// 2. 處理 Session 暱稱邏輯
	    if (nickname == null) {
	        session.setAttribute("nickname", customerName);
	        nickname = customerName;
	    }
	    
	                                                          
	    model.addAttribute("nickname", nickname); // 3. 準備回傳給首頁的資料
	    model.addAttribute("cars", carRepository.findAll()); // 建議改用 Repository 抓取最新車單
	    model.addAttribute("message", "✅ 預約成功！");
	    return "car"; 
	}                                   


	@GetMapping("/login")               // 當使用者在網頁網址輸入 /login 或點擊「登入」連結時觸發
	public String showLoginPage() {     // 單純回傳 "login"，告訴 Spring Boot 去 templates 資料夾找 login.html 檔案顯示出來
        return "login"; 
    }
	
	@GetMapping("/register")            // 當使用者在網址列輸入 /register，或點擊「加入會員」時觸發
	public String showRegisterPage() {  // 告訴 Spring Boot 去 templates 資料夾找到名為 register.html 的檔案並呈現給使用者
	    return "register";
	}

	@PostMapping("/register")                       // 處理註冊表單提交 (POST)
	public String handleRegister(
	        @RequestParam String name, 				// 接收使用者姓名/暱稱
	        @RequestParam String email,             // 接收 Email
	        @RequestParam String password,          //接收密碼
	        @RequestParam String confirmPassword,   // 接收第二次確認密碼
	        Model model) {
	    
	                                               
	    if (!password.equals(confirmPassword)) { //密碼一致性
	        model.addAttribute("message", "❌ 註冊失敗：兩次輸入的密碼不一致！");
	        return "register"; 
	    }

	                                                 
	    if (!password.matches("\\d{8}")) {//密碼格式檢查
	        model.addAttribute("message", "❌ 註冊失敗：密碼必須是剛好 8 位數字！");
	        return "register";
	    }

	                                        
	    User newUser = new User();//存入資料庫
	    newUser.setName(name);
	    newUser.setEmail(email);
	    newUser.setPassword(password);
	    
	    userRepository.save(newUser);       // 呼叫 User 資料庫管理員，將新會員存檔
	    
	    model.addAttribute("message", "🎉 註冊成功！密碼格式正確。請登入。");     // 註冊成功後，帶著成功訊息導向「登入頁面」
	    return "login"; 
	}
	                               
	@PostConstruct
	public void initData() { // 在 Controller 啟動時檢查資料庫是否為空
	                               
	    if (carRepository.count() == 0) {// 1. 檢查資料庫是否已經有資料，避免重複新增
	        
	        // 2. 建立資料並直接存入 Repository
	        carRepository.save(new Car(null, "BMW 3-Series Sedan", "$1,480,000", "2021 年份", "/images/Bmw3.jpg", 
	                    "2.0L 汽油", "手自排", "25000", Arrays.asList("天窗", "感應尾門", "ACC自適應巡航")));
	        
	        carRepository.save(new Car(null, "Mercedes-Benz C-Class", "$1,280,000", "2019 年份", "/images/Benz.jpg", 
	                    "1.5L 汽油", "九速手自排", "48000", Arrays.asList("倒車顯影", "盲點偵測", "電動座椅")));
	        
	        carRepository.save(new Car(null, "Toyota RAV4 Hybrid", "$980,000", "2022 年份", "/images/Rav4.jpeg", 
	                    "2.5L 油電", "E-CVT", "12000", Arrays.asList("通風座椅", "360環景", "電動尾門")));
	        
	        carRepository.save(new Car(null, "Porsche 911", "$5,200,000", "2023 年份", "/images/porche.jpg", 
	                    "3.0L 汽油", "PDK雙離合", "3500", Arrays.asList("跑車排氣", "PDLS頭燈", "真皮內裝")));
	        
	        System.out.println("✅ 已成功初始化 H2 資料庫的汽車範例資料！");
	    }
	}
	
	                                    // 顯示「我的預約」清單頁面
	@GetMapping("/my-reservations")     // 當使用者存取 /my-reservations 網址時觸發
	public String showMyReservations(HttpSession session, Model model) {
		
		                                
	    String nickname = (String) session.getAttribute("nickname");// 1. 從 Session 中嘗試取得使用者的暱稱
	    
	                             
	    if (nickname != null) { // 如果暱稱存在（代表使用者已登入），則傳遞給 Model，讓網頁導覽列能顯示「歡迎，某某某」
	        model.addAttribute("nickname", nickname);
	    }
	                  
	    List<Reservation> dbReservations = reservationRepository.findAll(); // 2. 從資料庫中取出「所有的」預約紀錄
	    
	    
	    model.addAttribute("reservations", dbReservations);// 3. 將撈出來的預約清單放入 Model，名稱為 "reservations"
	    
	    return "my-reservations"; 
	}
	
	// 刪除特定一筆預約紀錄
	@GetMapping("/delete-reservation/{id}")        // 確保這裡是接收資料庫的 ID
	public String deleteReservation(@PathVariable Long id) {   // @PathVariable 會自動把網址後方的數字抓下來，傳進變數 index 中
	   
		
	    reservationRepository.deleteById(id);// 直接呼叫 repository 從資料庫中刪除這筆 ID 對應的資料
	   
	   
	    return "redirect:/my-reservations";// 刪除後重新導向，這時 showMyReservations 會重新從資料庫撈取「最新」清單
	}
	
	// 顯示首頁 (支援搜尋功能)
	@GetMapping({"/", "/index"})
	public String showIndex(@RequestParam(name = "keyword", required = false) String keyword,
	                        @RequestParam(name = "priceRange", required = false) String priceRange,
	                        HttpSession session, 
	                        Model model) {
	    
	    // 1. 檢查 Session 暱稱
	    String nickname = (String) session.getAttribute("nickname");
	    if (nickname != null) {
	        model.addAttribute("nickname", nickname);
	    }

	    // 2. 先根據關鍵字或狀態撈出初步清單
	    List<Car> allCars;
	    if (keyword != null && !keyword.trim().isEmpty()) {
	        allCars = carRepository.findByNameContainingIgnoreCase(keyword);
	        model.addAttribute("message", "您搜尋的關鍵字是：「" + keyword + "」");
	    } else {
	        allCars = carRepository.findByStatus("已上架");
	    }
	        
	    // 3. 核心修正：安全地進行區間過濾
	    List<Car> finalFilteredCars = new java.util.ArrayList<>();
	    
	    for (Car car : allCars) {
	        // 如果使用者沒有選擇任何價格區間，全部都加進來
	        if (priceRange == null || priceRange.isEmpty()) {
	            finalFilteredCars.add(car);
	            continue; // 直接跑下一台車
	        }

	        try {
	            // 💡 安全轉化：只提取數字，過濾掉 $ , 萬 或是 KM 等文字
	            // 例如 "110萬" 會變成 "110"，"$1,480,000" 會變成 "1480000"
	            String cleanPrice = car.getPrice().replaceAll("[^0-9]", "");
	            
	            // 如果過濾後是空的（例如內容是純文字「面議」），這台車在選區間時就先隱藏
	            if (cleanPrice.isEmpty()) {
	                continue; 
	            }

	            int priceInt = Integer.parseInt(cleanPrice);
	            
	            // 💡 邏輯校正：如果使用者輸入的是 "110萬"，轉出來是 110
	            // 為了對應你的區間 (500000)，這裡可能需要判斷是否要乘上一萬，或者統一輸入格式
	            // 以下維持你原本的區間判斷：
	            if (priceRange.equals("under50") && priceInt < 500000) {
	                finalFilteredCars.add(car);
	            } 
	            else if (priceRange.equals("50-100") && priceInt >= 500000 && priceInt <= 1000000) {
	                finalFilteredCars.add(car);
	            } 
	            else if (priceRange.equals("100over") && priceInt > 1000000) {
	                finalFilteredCars.add(car);
	            }
	        } catch (Exception e) {
	            // 即使出錯（例如真的轉不動），也只是這台車不顯示，系統不會崩潰 500
	            System.out.println("跳過價格無法辨識的車輛：" + car.getName());
	        }
	    }
	    
	    model.addAttribute("cars", finalFilteredCars);
	    return "car"; 
	}
	
	@PostMapping("/car/delete/{id}")
	public String deleteCar(@PathVariable Long id, HttpServletRequest request) {
	    
	    // 1. 安全檢查：確保只有登入且身分為 ADMIN 的人可以刪除
	    if (request.getUserPrincipal() != null && request.isUserInRole("ADMIN")) {
	        carRepository.deleteById(id);
	        System.out.println("✅ 成功！車輛 ID: " + id + " 已成功下架");
	    } else {
	        System.out.println("❌ 警告：有人試圖非法刪除車輛！");
	    }
	    
	    // 2. 刪除完成後，重新導向回首頁
	    return "redirect:/index"; 
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
	
	//處理重設密碼的 POST 請求當使用者在忘記密碼頁面填寫完資料並按下「送出」時，會觸發此方法//
	@PostMapping("/reset-password")
	public String handleResetPassword(@RequestParam String name,        // 接收前端表單中 name 欄位輸入的值（使用者名稱）
	                                  @RequestParam String email,       // 接收前端表單中 email 欄位輸入的值（電子信箱）
	                                  @RequestParam String newPassword, // 接收前端表單中 newPassword 欄位輸入的值（新設定的密碼）
	                                  Model model) {                    // Model 物件用於將資料傳回前端頁面
	    
	    
	    if (newPassword == null || !newPassword.matches("\\d{8}")) {    // 檢查新密碼是否為空，或是否「不符合」 8 位數字的正規表示式 (Regex)
	        System.out.println("❌ 格式檢查失敗：新密碼不符合 8 位數字規則。");
	        model.addAttribute("message", "❌ 重設失敗：新密碼必須是 8 位數字！");// 將錯誤訊息帶回前端頁面顯示
	        return "forgot-password"; // 格式不對，停留在原頁面
	    }

	    User user = userRepository.findByEmail(email);   // 使用 Email 作為唯一鍵值去查詢該使用者
	    
	    System.out.println("=== 重設密碼偵錯中 ===");
	    System.out.println("表單輸入的名字: [" + name + "]");
	    System.out.println("表單輸入的 Email: [" + email + "]");
	    
	    if (user != null) {   // 4. 進行身份驗證比對
	        System.out.println("資料庫裡的人名: [" + user.getName() + "]");
	        
	        
	        if (user.getName().trim().equals(name.trim())) { // 檢查資料庫中的名字與表單輸入的名字是否一致，
	            user.setPassword(newPassword);               //使用 .trim() 移除前後空白，避免使用者不小心多打了空白鍵
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
	
	@PostMapping("/submit-reservation") //處理賞車預約的 POST 請求，當使用者在預約頁面填寫姓名、車款、日期並送出時觸發
	public String handleReservation(@RequestParam String name, 
	                                @RequestParam String carName, 
	                                @RequestParam String date, 
	                                Model model) {
	    
	    
	    LocalDate selectedDate = LocalDate.parse(date);
	    LocalDate today = LocalDate.now();   // 取得執行程式當下的系統日期

	    
	    if (selectedDate.isBefore(today)) {   // 如果選擇的日期「早於」今天
	        model.addAttribute("message", "❌ 預約失敗：日期不能選擇過去的時間！");
	        
	        model.addAttribute("allCars", carRepository.findAll());//因為要返回原頁面 (reserve)，必須重新撈取車輛清單，否則下拉選單會變空白
	        return "reserve";
	    }

	    
	    System.out.println("✅ 預約成功：[" + name + "] 預約了 [" + carName + "] 於 " + date);
	    model.addAttribute("message", "🎉 預約成功！我們將會致電與您確認。");
	    return "index";
	}
	
	@PostMapping("/reserve") //處理預約表單提交並存入資料庫
	public String submitReservation(@ModelAttribute("reservation") Reservation reservation, 
            Principal principal, // 🚨 改用 Spring Security 的 Principal 憑證
            RedirectAttributes redirectAttributes) { // 用來跨網頁傳遞成功訊息

    // 1. 檢查是否登入 (如果 principal 是空的，代表 Spring Security 說他沒登入)
     if (principal == null) { 
     return "redirect:/login";
        }

    // 2. 資料持久化 (存入資料庫)
     reservationRepository.save(reservation);

     System.out.println("成功儲存預約！預約車款：" + reservation.getCarName() + "，預約人：" + reservation.getCustomerName());

    // 3. 帶著成功提示訊息跳轉回首頁
     redirectAttributes.addFlashAttribute("message", "🎉 預約成功！我們將會致電與您確認。");

     return "redirect:/index"; 
       }
	
	@GetMapping("/faq") //常見問題功能
	public String showFaqPage() {
	    return "faq"; 
	}
	
	@GetMapping("/calculator")
	public String showCalculator() {
	    return "calculator"; // 這裡對應calculator.html 檔名
	}
	
	@GetMapping("/show-cars")
    public String listCars(Model model) {
        // 從 MariaDB 抓取所有汽車
		model.addAttribute("cars", carRepository.findByStatus("已上架"));
        return "car"; // 對應到 car-list.html
    }
	
	// 顯示上架頁面
	@GetMapping("/car/add")
        public String showAddForm(HttpServletRequest request, Model model) {
	    
	    // 1. 改用 Spring Security 的方式檢查：是否未登入？ 或者 角色不是 ADMIN？
	    if (request.getUserPrincipal() == null || !request.isUserInRole("ADMIN")) {
	        model.addAttribute("message", "🔒 權限不足：只有管理員可以上架車輛！");
	        return "login"; 
	    }

	    model.addAttribute("car", new Car());
	    return "add-car"; 
	}

	// 執行上架儲存
	@PostMapping("/car/save")
	public String saveCar(HttpServletRequest request, 
            @ModelAttribute("car") Car car, 
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "featureList", required = false) List<String> featureList) {

    // 1. 權限檢查：只有 ADMIN 能上架
    if (request.getUserPrincipal() == null || !request.isUserInRole("ADMIN")) {
    return "redirect:/login";
  }

    try {
    // 2. 處理圖片上傳邏輯
    if (imageFile != null && !imageFile.isEmpty()) {
    String fileName = imageFile.getOriginalFilename();
    // 🚨 注意：這會把圖片存到你專案的 static/images 資料夾下
    Path path = Paths.get("src/main/resources/static/images/" + fileName);
    Files.write(path, imageFile.getBytes());
  
    // 將圖片檔名存入資料庫
    car.setImage(fileName);
  }

    // 3. 處理特色清單 (Features)
    if (featureList != null) {
    car.setFeatures(featureList);
  }

    } catch (Exception e) {
    System.out.println("圖片上傳出錯：" + e.getMessage());
  }

    // 4. 設定狀態並存檔
    car.setStatus("已上架"); 
    carRepository.save(car);

    return "redirect:/index"; // 成功後跳回首頁看新卡片
  }
}
