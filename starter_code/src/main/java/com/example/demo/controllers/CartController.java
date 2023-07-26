package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final ItemRepository itemRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(CartController.class);

	public CartController(
			UserRepository userRepository,
			CartRepository cartRepository,
			ItemRepository itemRepository) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.itemRepository = itemRepository;
	}

	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		try {
			User user = userRepository.findByUsername(request.getUsername());
			if (user == null) {
				LOGGER.warn("No user with username {} could be found", request.getUsername());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Optional<Item> item = itemRepository.findById(request.getItemId());
			if (!item.isPresent()) {
				LOGGER.warn("No item with id {} could be found", request.getItemId());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity())
					.forEach(i -> cart.addItem(item.get()));
			cartRepository.save(cart);

			LOGGER.info("Item with id {} was added to {}'s cart {} times", request.getItemId(), request.getUsername(), request.getQuantity());
			return ResponseEntity.ok(cart);
		} catch (Exception e) {
			LOGGER.error("There was an unexpected error while adding item with id {} to user {}s cart", request.getItemId(), request.getUsername(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		try {
			User user = userRepository.findByUsername(request.getUsername());
			if (user == null) {
				LOGGER.warn("No user with username {} could be found", request.getUsername());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Optional<Item> item = itemRepository.findById(request.getItemId());
			if (!item.isPresent()) {
				LOGGER.warn("No item with id {} could be found", request.getItemId());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Cart cart = user.getCart();
			IntStream.range(0, request.getQuantity())
					.forEach(i -> cart.removeItem(item.get()));
			cartRepository.save(cart);

			LOGGER.info("Item with id {} was removed from {}'s cart {} times", request.getItemId(), request.getUsername(), request.getQuantity());
			return ResponseEntity.ok(cart);
		} catch (Exception e) {
			LOGGER.error("There was an unexpected error while removing item with id {} from user {}s cart", request.getItemId(), request.getUsername(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
		
}
