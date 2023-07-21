package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


public class CartControllerTest {
    private static CartController cartController;
    private static final UserRepository userRepositoryMock = mock(UserRepository.class);
    private static final CartRepository cartRepositoryMock = mock(CartRepository.class);
    private static final ItemRepository itemRepositoryMock = mock(ItemRepository.class);
    private static final long ID = 0L;
    private static final String USER_NAME = "testUsername";
    private static User user;


    @Before
    public void init() {
        cartController = new CartController(
                userRepositoryMock,
                cartRepositoryMock,
                itemRepositoryMock
        );

        Cart cart = new Cart();
        user = new User();
        user.setId(ID);
        user.setUsername(USER_NAME);
        user.setPassword("testPassword");
        user.setSalt("testSalt");
        user.setCart(cart);

        Item item = new Item();
        item.setId(ID);
        item.setName("testItemName");
        item.setDescription("testItemDescription");
        item.setPrice(BigDecimal.ONE);

        when(userRepositoryMock.findByUsername(USER_NAME)).thenReturn(user);
        when(itemRepositoryMock.findById(ID)).thenReturn(Optional.of(item));
    }

    @Test
    public void testAddToCart() {
        // Arrange
        ModifyCartRequest modifyCartRequest = createModifyCartRequest(USER_NAME, ID, 1);

        // Act
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        Cart expectedCart = responseEntity.getBody();

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        assertNotNull(expectedCart);
        assertEquals(1, expectedCart.getItems().size());
        assertEquals(BigDecimal.ONE, expectedCart.getTotal());
    }

    @Test
    public void testAddToCartInvalidUser() {
        // Arrange
        ModifyCartRequest modifyCartRequest = createModifyCartRequest("invalidUserName", ID, 1);

        // Act
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testAddToCartInvalidItem() {
        // Arrange
        ModifyCartRequest modifyCartRequest = createModifyCartRequest(USER_NAME, 2L, 1);

        // Act
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testRemoveFromCart() {
        // Arrange
        ModifyCartRequest modifyCartRequest = createModifyCartRequest(USER_NAME, ID, 1);

        // Act
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        Cart expectedCart = responseEntity.getBody();

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        assertNotNull(expectedCart);
        assertEquals(0, expectedCart.getItems().size());
    }

    @Test
    public void testRemoveFromCartInvalidUser() {
        // Arrange
        ModifyCartRequest modifyCartRequest = createModifyCartRequest("invalidUserName", ID, 1);

        // Act
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testRemoveFromCartInvalidItem() {
        // Arrange
        ModifyCartRequest modifyCartRequest = createModifyCartRequest(USER_NAME, 2L, 1);

        // Act
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    private ModifyCartRequest createModifyCartRequest(String username, long itemId, int quantity) {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(username);
        modifyCartRequest.setItemId(itemId);
        modifyCartRequest.setQuantity(quantity);

        return modifyCartRequest;
    }
}