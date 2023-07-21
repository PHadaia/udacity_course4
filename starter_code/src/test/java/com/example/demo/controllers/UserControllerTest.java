package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.example.demo.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private static UserController userController;
    private static final UserRepository userRepositoryMock = mock(UserRepository.class);
    private static final CartRepository cartRepositoryMock = mock(CartRepository.class);
    private static final BCryptPasswordEncoder bCryptPasswordEncoderMock = mock(BCryptPasswordEncoder.class);
    private static User user;

    @Before
    public void init() {
        userController = new UserController(
                userRepositoryMock,
                cartRepositoryMock,
                bCryptPasswordEncoderMock
        );

        Cart cart = new Cart();
        user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(USER_PASSWORD);
        user.setSalt("testSalt");
        user.setCart(cart);

        when(userRepositoryMock.findByUsername("testUsername")).thenReturn(user);
        when(userRepositoryMock.findById(0L)).thenReturn(Optional.of(user));
    }

    @Test
    public void testCreateUser() {
        // Arrange
        CreateUserRequest createUserRequest = createUserRequest("test", "testPassword", "testPassword");

        // Act
        ResponseEntity<User> actualResponseEntity = userController.createUser(createUserRequest);

        // Assert
        assertNotNull(actualResponseEntity);
        assertTrue(actualResponseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testCreateUserIncorrectPassword() {
        // Arrange
        CreateUserRequest createUserRequest = createUserRequest("test", "test", "test");

        // Act
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }

    @Test
    public void testFindById() {
        // Arrange
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<User> responseEntity = userController.findById(USER_ID);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(user, responseEntity.getBody());
    }

    @Test
    public void testFindByUsername() {
        // Arrange
        when(userRepositoryMock.findByUsername(USERNAME)).thenReturn(user);

        // Act
        ResponseEntity<User> responseEntity = userController.findByUserName(USERNAME);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals(user, responseEntity.getBody());
    }

    private CreateUserRequest createUserRequest(String username, String password, String confirmPassword) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(confirmPassword);

        return createUserRequest;
    }
}