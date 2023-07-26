package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	public UserController(
			UserRepository userRepository,
			CartRepository cartRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		try {
			User user = new User();
			user.setUsername(createUserRequest.getUsername());

			if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
				LOGGER.error("User Controller: User could not be created due to mismatched password");
				return ResponseEntity.badRequest().build();
			}

			if (createUserRequest.getPassword().length() < 8) {
				LOGGER.error("User Controller: Provided password is shorter than 8 characters");
				return ResponseEntity.badRequest().build();
			}

			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			userRepository.save(user);

			LOGGER.info("User Controller: User {} was successfully created", user.getUsername());
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			LOGGER.error("There was an unexpected error while creating user {}", createUserRequest.getUsername(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
