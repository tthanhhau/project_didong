package StoreApp.StoreApp.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import StoreApp.StoreApp.entity.Category;
import StoreApp.StoreApp.entity.Order;
import StoreApp.StoreApp.entity.Order_Item;
import StoreApp.StoreApp.entity.Product;
import StoreApp.StoreApp.entity.ProductImage;
import StoreApp.StoreApp.entity.User;
import StoreApp.StoreApp.model.Mail;
import StoreApp.StoreApp.service.CategoryService;
import StoreApp.StoreApp.service.CloudinaryService;
import StoreApp.StoreApp.service.MailService;
import StoreApp.StoreApp.service.OrderService;
import StoreApp.StoreApp.service.Order_ItemService;
import StoreApp.StoreApp.service.ProductImageService;
import StoreApp.StoreApp.service.ProductService;
import StoreApp.StoreApp.service.UserService;

@Controller
public class AdminController {
	@Autowired
	OrderService orderService;

	@Autowired
	UserService userService;

	@Autowired
	ProductService productService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	Order_ItemService order_ItemService;

	@Autowired
	CloudinaryService cloudinaryService;

	@Autowired
	MailService mailService;

	@Autowired
	HttpSession session;

	@Autowired
	ProductImageService productImageService;

	@GetMapping("/signin-admin")
	public String SignInAdminView(Model model) {
		String err_sign_admin = (String) session.getAttribute("err_sign_admin");
		model.addAttribute("err_sign_admin", err_sign_admin);
		session.setAttribute("err_sign_admin", null);
		return "signin-admin";
	}

//	@PostMapping("/signin-admin")
//	public String SignInAdminHandel(@ModelAttribute("login-name") String login_name,
//			@ModelAttribute("pass") String pass, Model model) throws Exception {
//		User admin = userService.findByIdAndRole(login_name, "admin");
//		System.out.println(admin);
//		if (admin == null) {
//			session.setAttribute("err_sign_admin", "Username or Password is not correct!");
//			return "redirect:/signin-admin";
//		} else {
//			String decodedValue = new String(Base64.getDecoder().decode(admin.getPassword()));
//			if (!decodedValue.equals(pass)) {
//				session.setAttribute("err_sign_admin", "Username or Password is not correct!");
//				return "redirect:/signin-admin";
//			} else {
//				System.out.println(admin);
//				session.setAttribute("admin", admin);
//				return "redirect:/dashboard";
//			}
//		}
//	}

	@GetMapping("/logout-admin")
	public String LogOutAdmin(Model model) {
		session.setAttribute("admin", null);
		return "redirect:/signin-admin";
	}

	@GetMapping("/dashboard")
	public String DashboardView(Model model) {
		User admin = (User) session.getAttribute("admin");
		System.out.println("======");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			List<Order> listOrder = orderService.findAll();
			List<Product> listProduct = productService.getAllProduct();
			List<User> listUser = userService.findAll();
			List<Category> listCategory = categoryService.findAll();

			model.addAttribute("Total_Order", listOrder.size());
			model.addAttribute("Total_Product", listProduct.size());
			model.addAttribute("Total_User", listUser.size());
			model.addAttribute("Total_Category", listCategory.size());
			return "dashboard";
		}
	}

	@GetMapping("/dashboard-invoice/{id}")
	public String InvoiceView(@PathVariable int id, Model model, HttpServletRequest request) {
		Order order = orderService.findById(id);
		List<Order_Item> listOrder_Item = order_ItemService.getAllByOrder_Id(order.getId());
		model.addAttribute("listOrder_Item", listOrder_Item);
		model.addAttribute("order", order);
		return "dashboard-invoice";
	}

	@GetMapping("/dashboard-orders")
	public String DashboardOrderView(Model model) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			Pageable pageable = PageRequest.of(0, 3);
			Page<Order> pageOrder = orderService.findAll(pageable);
			model.addAttribute("pageOrder", pageOrder);
			return "dashboard-orders";
		}
	}

	@GetMapping("/dashboard-orders/{page}")
	public String DashboardOrderPageView(@PathVariable int page, Model model) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			Pageable pageable = PageRequest.of(page, 3);
			Page<Order> pageOrder = orderService.findAll(pageable);
			model.addAttribute("pageOrder", pageOrder);
			return "dashboard-orders";
		}
	}

	@PostMapping("/send-message")
	public String SendMessage(Model model, @ModelAttribute("message") String message,
			@ModelAttribute("email") String email, HttpServletRequest request) throws Exception {
		String referer = request.getHeader("Referer");
		System.out.println(message);
		System.out.println(email);
		Mail mail = new Mail();
		mail.setMailFrom("haovo1512@gmail.com");
		mail.setMailTo(email);
		mail.setMailSubject("This is message from Male fashion.");
		mail.setMailContent(message);
		mailService.sendEmail(mail);
		return "redirect:" + referer;
	}

	@GetMapping("/delete-order/{id}")
	public String DeleteOrder(@PathVariable int id, Model model, HttpServletRequest request) throws Exception {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			String referer = request.getHeader("Referer");
			Order order = orderService.findById(id);
			System.out.println(order);
			if (order != null) {
				for (Order_Item y : order.getOrder_Item()) {
					order_ItemService.deleteById(y.getId());
				}
				orderService.deleteById(id);
			}
			return "redirect:" + referer;
		}
	}

	@GetMapping("dashboard-wallet")
	public String DashboardWalletView(Model model) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			List<Order> listOrder = orderService.findAll();
			List<Order> listPaymentWithMomo = orderService.findAllByPayment_Method("Pay with ZaloPay");
			List<Order> listPaymentOnDelivery = orderService.findAllByPayment_Method("Pay on Delivery");
			int TotalMomo = 0;
			int TotalDelivery = 0;
			for (Order y : listPaymentWithMomo) {
				TotalMomo = TotalMomo + y.getTotal();
			}
			for (Order y : listPaymentOnDelivery) {
				TotalDelivery = TotalDelivery + y.getTotal();
			}
			model.addAttribute("TotalMomo", TotalMomo);
			model.addAttribute("TotalDelivery", TotalDelivery);
			model.addAttribute("TotalOrder", listOrder.size());
			return "dashboard-wallet";
		}
	}
	
	@GetMapping("/redirect")
	public String Redirect(Model model, HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

}
