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
import com.example.demo.enums.CarStatus;

@Controller
public class CarController {
	
	
	
	@Autowired                                              
    private ReservationRepository reservationRepository;    
	
	@Autowired
    private CarRepository carRepository;                    
	
	@Autowired
    private UserRepository userRepository;                

	
	@GetMapping("/sell")       
	public String showSellPage() {
	   
	    return "sell"; 
	}                                                  

	@PostMapping("/submit-car")      
	public String handleSellForm(
	        @RequestParam String name, 
	        @RequestParam String year, 
	        @RequestParam String price, 
	        @RequestParam String phone, 
	        @RequestParam String sellerName,
	        RedirectAttributes redirectAttributes) { 

	    System.out.println("收到賣車申請：" + name + "，電話：" + phone); 
	    Car newCar = new Car();                                         
	    newCar.setName(name);
	    newCar.setSellerName(sellerName);
	    newCar.setYear(year);
	    newCar.setPrice(price);
	    newCar.setPhone(phone);
	    
	    
	    newCar.setMileage("未知");
	    newCar.setFuelType("未知");
	    newCar.setTransmission("未知");
	    newCar.setImage("default.jpg"); 
	    
	    
	    
	    newCar.setStatus(CarStatus.PENDING_REVIEW);
	    System.out.println("【偵錯】準備存入資料庫，這台車的狀態目前是：" + newCar.getStatus());

	    carRepository.save(newCar); 
	    
	    
	    redirectAttributes.addFlashAttribute("message", "✅ 提交成功！我們已收到您的「" + name + "」賣車申請，專員將盡快聯絡。");
	    
	    
	    return "redirect:/index"; 
	}                                                 
	
	@GetMapping("/reserve")   
	public String showReservePage(Model model, @RequestParam(required = false) String carName) {
	    
	    
	    Reservation reservation = new Reservation(); 
	    
	    
	    if (carName != null) {
	        reservation.setCarName(carName); 
	    }
	    
	   
	    model.addAttribute("reservation", reservation); 
	    model.addAttribute("cars", carRepository.findByStatus(CarStatus.LISTED));
	    return "reserve";
	}                                                
	

	
	@PostMapping("/submit-reserve")
	public String handleReserve(@RequestParam String customerName, 
	                            @RequestParam String carName, 
	                            @RequestParam String date, 
	                            HttpSession session,         
	                            Model model) {
		                                                     
		Reservation res = new Reservation();
	    res.setCustomerName(customerName);
	    res.setCarName(carName);
	    res.setDate(date);
	    
	                                                         
	    reservationRepository.save(res);  
	    
	                                                         
	    String nickname = (String) session.getAttribute("nickname");
	    if (nickname == null) {
	        session.setAttribute("nickname", customerName);
	        nickname = customerName;
	    }
	    
	                                                          
	    model.addAttribute("nickname", nickname); 
	    model.addAttribute("cars", carRepository.findAll()); 
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
	                               
	@PostConstruct
	public void initData() { 
	                               
	    if (carRepository.count() == 0) {
	        
	        
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
	
	
	@GetMapping("/delete-reservation/{id}")        
	public String deleteReservation(@PathVariable Long id) {   
	   
		
	    reservationRepository.deleteById(id);
	   
	   
	    return "redirect:/my-reservations";
	}
	
	
	@GetMapping({"/", "/index"})
	public String showIndex(@RequestParam(name = "keyword", required = false) String keyword,
	                        @RequestParam(name = "priceRange", required = false) String priceRange,
	                        HttpSession session, 
	                        Model model) {
	    
	    
	    String nickname = (String) session.getAttribute("nickname");
	    if (nickname != null) {
	        model.addAttribute("nickname", nickname);
	    }

	   
	    List<Car> allCars;
	    if (keyword != null && !keyword.trim().isEmpty()) {
	        allCars = carRepository.findByNameContainingIgnoreCase(keyword);
	        model.addAttribute("message", "您搜尋的關鍵字是：「" + keyword + "」");
	    } else {
	    	allCars = carRepository.findByStatus(CarStatus.LISTED);
	    }
	        
	    
	    List<Car> finalFilteredCars = new java.util.ArrayList<>();
	    
	    for (Car car : allCars) {
	        
	        if (priceRange == null || priceRange.isEmpty()) {
	            finalFilteredCars.add(car);
	            continue; 
	        }

	        try {
	          
	            String cleanPrice = car.getPrice().replaceAll("[^0-9]", "");
	            
	         
	            if (cleanPrice.isEmpty()) {
	                continue; 
	            }

	            int priceInt = Integer.parseInt(cleanPrice);
	            
	          
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
	           
	            System.out.println("跳過價格無法辨識的車輛：" + car.getName());
	        }
	    }
	    
	    model.addAttribute("cars", finalFilteredCars);
	    return "car"; 
	}
	
	@PostMapping("/car/delete/{id}")
	public String deleteCar(@PathVariable Long id, HttpServletRequest request) {
	    
	   
	    if (request.getUserPrincipal() != null && request.isUserInRole("ADMIN")) {
	        carRepository.deleteById(id);
	        System.out.println("✅ 成功！車輛 ID: " + id + " 已成功下架");
	    } else {
	        System.out.println("❌ 警告：有人試圖非法刪除車輛！");
	    }
	    
	   
	    return "redirect:/index"; 
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
	        
	        model.addAttribute("allCars", carRepository.findAll());
	        return "reserve";
	    }

	    
	    System.out.println("✅ 預約成功：[" + name + "] 預約了 [" + carName + "] 於 " + date);
	    model.addAttribute("message", "🎉 預約成功！我們將會致電與您確認。");
	    return "index";
	}
	
	@PostMapping("/reserve") 
	public String submitReservation(@ModelAttribute("reservation") Reservation reservation, 
            Principal principal, 
            RedirectAttributes redirectAttributes) { 

    
     if (principal == null) { 
     return "redirect:/login";
        }

    
     reservationRepository.save(reservation);

     System.out.println("成功儲存預約！預約車款：" + reservation.getCarName() + "，預約人：" + reservation.getCustomerName());

   
     redirectAttributes.addFlashAttribute("message", "🎉 預約成功！我們將會致電與您確認。");

     return "redirect:/index"; 
       }
	
	@GetMapping("/faq") 
	public String showFaqPage() {
	    return "faq"; 
	}
	
	@GetMapping("/calculator")
	public String showCalculator() {
	    return "calculator";
	}
	
	@GetMapping("/show-cars")
    public String listCars(Model model) {
        
		model.addAttribute("cars", carRepository.findByStatus(CarStatus.LISTED));
        return "car"; 
    }
	
	
	@GetMapping("/car/add")
        public String showAddForm(HttpServletRequest request, Model model) {
	    
	    
	    if (request.getUserPrincipal() == null || !request.isUserInRole("ADMIN")) {
	        model.addAttribute("message", "🔒 權限不足：只有管理員可以上架車輛！");
	        return "login"; 
	    }

	    model.addAttribute("car", new Car());
	    return "add-car"; 
	}

	
	@PostMapping("/car/save")
	public String saveCar(HttpServletRequest request, 
	        @ModelAttribute("car") Car car, 
	        @RequestParam("imageFile") MultipartFile imageFile,
	        @RequestParam(value = "featureList", required = false) List<String> featureList) {

	    if (request.getUserPrincipal() == null || !request.isUserInRole("ADMIN")) {
	        return "redirect:/login";
	    }

	    try {
	        if (imageFile != null && !imageFile.isEmpty()) {
	            String fileName = imageFile.getOriginalFilename();
	            
	            
	            String uploadPath = System.getProperty("user.dir") + "/uploads/";
	            java.io.File uploadDir = new java.io.File(uploadPath);
	            if (!uploadDir.exists()) {
	                uploadDir.mkdirs(); 
	            }
	            
	            
	            Path path = Paths.get(uploadPath + fileName);
	            Files.write(path, imageFile.getBytes());
	            
	            
	            car.setImage(fileName);
	        }

	        if (featureList != null) {
	            car.setFeatures(featureList);
	        }

	    } catch (Exception e) {
	        System.out.println("圖片上傳出錯：" + e.getMessage());
	    }

	    car.setStatus(CarStatus.LISTED);
	    carRepository.save(car);

	    return "redirect:/index"; 
	}
}
