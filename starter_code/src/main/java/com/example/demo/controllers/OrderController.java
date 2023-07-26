package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);


	public OrderController(
			UserRepository userRepository,
			OrderRepository orderRepository) {
		this.userRepository = userRepository;
		this.orderRepository = orderRepository;
	}
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			LOGGER.warn("No user with username {} could be found", username);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		LOGGER.info("Order for user {} was successfully submitted", username);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			LOGGER.warn("No user with username {} could be found", username);
			return ResponseEntity.notFound().build();
		}

		LOGGER.info("Order history for user {} was successfully fetched", username);
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
