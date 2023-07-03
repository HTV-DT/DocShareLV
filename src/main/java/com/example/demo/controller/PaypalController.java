package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.pdfbox.contentstream.operator.state.Save;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.request.PackageForm;
import com.example.demo.dto.request.PayForm;
import com.example.demo.dto.request.SignUpForm;
import com.example.demo.dto.response.FileResponse;
import com.example.demo.dto.response.PayReponse;
import com.example.demo.dto.response.ResponseMessage;
import com.example.demo.model.Access;
import com.example.demo.model.File;
import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.model.Package;
import com.example.demo.model.Role;
import com.example.demo.model.RoleName;
import com.example.demo.model.Users;
import com.example.demo.service.AccessService;
import com.example.demo.service.OrderService;
import com.example.demo.service.OrderdetailService;
import com.example.demo.service.PackageService;
import com.example.demo.service.PaypalService;
import com.example.demo.service.impl.RoleServiceImpl;
import com.example.demo.service.impl.UserServiceImpl;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@CrossOrigin(origins = "*")
@Controller
public class PaypalController {

	@Autowired
	PaypalService service;

	@Autowired
	UserServiceImpl userService;

	@Autowired
	PackageService packageService;

	@Autowired
	RoleServiceImpl roleService;

	@Autowired
	OrderdetailService orderdetailService;

	@Autowired
	OrderService orderService;

	@Autowired
	AccessService accessService;

	public static final String SUCCESS_URL = "pay/success";
	public static final String CANCEL_URL = "pay/cancel";

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@PostMapping("/pay")
	public ResponseEntity<String> payment(@RequestBody PayForm payReponse) {
		try {

			Optional<Users> optionalUser = userService.findById(payReponse.getUser_id());
			Users user = optionalUser.isPresent() ? optionalUser.get() : null;
			Optional<Package> optionalPackage = packageService.findById(payReponse.getPackage_id());
			Package package1 = optionalPackage.isPresent() ? optionalPackage.get() : null;
			if (package1 == null) {
				throw new NotFoundException("package not found");
			}
			if (user == null) {
				throw new NotFoundException("User not found");
			}
			Payment payment = service.createPayment(package1.getPrice(), "USD", "paypal",
					"sale", "test",
					"http://localhost:8080/" + CANCEL_URL + "?id=" + payReponse.getFile_id() + "&name="
							+ payReponse.getName(),
					"http://localhost:8080/" + SUCCESS_URL + "?id=" + payReponse.getFile_id() + "&name="
							+ payReponse.getName());

			Set<Access> userAccesses = user.getAccesses();
			List<Access> filteredAccesses = userAccesses.stream()
					.filter(access -> access.getPackages().getId() == package1.getId())
					.collect(Collectors.toList());
			List<Access> sortedAccesses = filteredAccesses.stream()
					.sorted(Comparator.comparing(Access::getCreatedAt))
					.collect(Collectors.toList());
			LocalDateTime startDate = null;
			LocalDateTime oldestAccessDate = null;
			if (!sortedAccesses.isEmpty() && package1.getId() != 1) {
				Access oldestAccess = sortedAccesses.get(sortedAccesses.size() - 1);
				oldestAccessDate = oldestAccess.getCreatedAt();
				startDate = oldestAccessDate;
			} else {
				startDate = LocalDateTime.now();
			}

			LocalDateTime endDate = startDate.plus(Duration.ofDays(package1.getDuration()));
			OrderDetail orderDetail = new OrderDetail(startDate, endDate,
					(new Double(package1.getPrice())).floatValue(), 0);
			Order order = new Order(package1, user, orderDetail, false, payment.getId());
			orderdetailService.save(orderDetail);
			orderService.save(order);
			for (Links link : payment.getLinks()) {
				if (link.getRel().equals("approval_url")) {
					return ResponseEntity.ok(link.getHref());

				}
			}

		} catch (PayPalRESTException e) {

			e.printStackTrace();
		}
		return ResponseEntity.ok("error create pay");
	}

	@GetMapping(value = CANCEL_URL)
	public String cancelPay(@RequestParam("id") String id, @RequestParam("name") String name) {

		if (id == null) {
			return "redirect:http://localhost:3000/" + name + "/order?status=false";
		} else {
			return "redirect:http://localhost:3000/fileDetail/" + id + "?status=false";
		}

	}

	@GetMapping(value = SUCCESS_URL)
	public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId,
			@RequestParam("name") String name, @RequestParam("id") String id) {
		try {
			Payment payment = service.executePayment(paymentId, payerId);
			System.out.println(payment.toJSON());

			Optional<Order> optionalOrder = orderService.findByOrderCode(payment.getId());
			Order order1 = optionalOrder.isPresent() ? optionalOrder.get() : null;
			if (order1 == null) {
				throw new NotFoundException("package not found");
			}
			order1.setOrderStatus(true);
			orderService.save(order1);

			Users user = order1.getUser();
			Access access = new Access();

			access.setUser(user);
			access.setPackages(order1.getPackages());
			access.setNumOfAccess(order1.getPackages().getDowloads());
			access.setCreatedAt(order1.getOrderDetail().getEnd_date());

			accessService.save(access);

			// Check if the user has the admin role
			boolean isUser = user.getRoles().contains(RoleName.USER);

			if (!isUser) {
				Set<Role> roles = new HashSet<>();
				Role userRole = roleService.findByName(RoleName.USER)
						.orElseThrow(() -> new RuntimeException("Role not found"));
				roles.add(userRole);
				user.setRoles(roles);
				user = userService.save(user);

			}

			if (payment.getState().equals("approved")) {
				// Redirect to the file detail page with the file_id parameter
				if ("null".equals(id)) {
					return "redirect:http://localhost:3000/" + name + "/order?status=true";
				}
				else{
					return "redirect:http://localhost:3000/fileDetail/" + id + "?status=true";
				}
			}
		} catch (PayPalRESTException e) {
			System.out.println(e.getMessage());
		}
		return "redirect:/";
	}

	@GetMapping("/packages")
	public ResponseEntity<List<Package>> getAllPackage() {
		List<Package> packages = packageService.getAllActivePackages();
		return new ResponseEntity<>(packages, HttpStatus.OK);
	}

	@GetMapping("/packages/admin")
	public ResponseEntity<List<Package>> getAllPackageAdmin() {
		List<Package> packages = packageService.getAllPackages();
		return new ResponseEntity<>(packages, HttpStatus.OK);
	}

	@PostMapping("/package/add")
	public ResponseEntity<Package> savePackage(@RequestBody PackageForm packageForm, HttpServletRequest request)
			throws MessagingException, UnsupportedEncodingException {
		Package package1 = new Package(packageForm.getName(), packageForm.getDuration(), packageForm.getPrice(),
				packageForm.getDowloads(), packageForm.getStorageSize());
		return new ResponseEntity<>(packageService.save(package1), HttpStatus.OK);
	}

	@PutMapping("/package/update")
	public ResponseEntity<Package> updatePackage(@RequestBody PackageForm packageForm) {
		Optional<Package> optionalPackage = packageService.findById(packageForm.getId());
		if (!optionalPackage.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Package package1 = optionalPackage.get();
		package1.setName(packageForm.getName());
		package1.setDowloads(packageForm.getDowloads());
		package1.setDuration(packageForm.getDuration());
		package1.setPrice(packageForm.getPrice());
		package1.setStorageSize(packageForm.getStorageSize());
		Package savedPackage = packageService.save(package1);
		return ResponseEntity.ok(savedPackage);
	}

	@PutMapping("package/active")
	public ResponseEntity<Package> setActive(@RequestBody PackageForm packageForm) {
		Optional<Package> optionalPackage = packageService.findById(packageForm.getId());
		if (!optionalPackage.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Package package1 = optionalPackage.get();
		package1.setActive(packageForm.isActive());
		Package savedPackage = packageService.save(package1);
		return ResponseEntity.ok(savedPackage);
	}

}