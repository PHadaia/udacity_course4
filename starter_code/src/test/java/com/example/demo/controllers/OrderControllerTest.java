package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.example.demo.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private static OrderController orderController;
    private static final UserRepository userRepositoryMock = mock(UserRepository.class);
    private static final OrderRepository orderRepositoryMock = mock(OrderRepository.class);

    @Before
    public void init() {
        orderController = new OrderController(userRepositoryMock, orderRepositoryMock);

        Cart cart = new Cart();
        cart.setItems(Collections.singletonList(new Item()));

        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(USER_PASSWORD);
        user.setSalt("testSalt");
        user.setCart(cart);

        UserOrder userOrder = new UserOrder();
        userOrder.setId(ORDER_ID);
        userOrder.setUser(user);
        userOrder.setTotal(BigDecimal.ONE);
        userOrder.setItems(Collections.singletonList(new Item()));

        when(userRepositoryMock.findByUsername(USERNAME)).thenReturn(user);
        when(orderRepositoryMock.findByUser(user)).thenReturn(Collections.singletonList(userOrder));
    }

    @Test
    public void testSubmit() {
        // Act
        ResponseEntity<UserOrder> responseEntity = orderController.submit(USERNAME);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void testSubmitInvalidUser() {
        // Act
        ResponseEntity<UserOrder> responseEntity = orderController.submit("Invalid Username");

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testGetOrdersForUser() {
        // Act
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(USERNAME);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().size());
    }

    @Test
    public void testGetOrdersForInvalidUser() {
        // Act
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("Invalid Username");

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        assertNull(responseEntity.getBody());
    }
}