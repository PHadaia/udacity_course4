package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static com.example.demo.TestConstants.ITEM_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private static ItemController itemController;
    private static final ItemRepository itemRepositoryMock = mock(ItemRepository.class);
    private static final long ID = 0L;
    private static Item item;


    @Before
    public void init() {
        itemController = new ItemController(itemRepositoryMock);

        item = new Item();
        item.setId(ID);
        item.setName(ITEM_NAME);
        item.setDescription("testDescription");
        item.setPrice(BigDecimal.ONE);

        Item item2 = new Item();
        item.setId(1L);
        item.setName("testName2");
        item.setDescription("testDescription2");
        item.setPrice(BigDecimal.ONE);

        when(itemRepositoryMock.findById(ID)).thenReturn(Optional.of(item));
        when(itemRepositoryMock.findAll()).thenReturn(new ArrayList<>(Arrays.asList(item, item2)));
    }

    @Test
    public void testGetItems() {
        // Act
        ResponseEntity<List<Item>> responseEntity = itemController.getItems();

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        List<Item> expectedItems = responseEntity.getBody();
        assertNotNull(expectedItems);
        assertEquals(2, expectedItems.size());
    }

    @Test
    public void testGetItemById() {
        // Act
        ResponseEntity<Item> responseEntity = itemController.getItemById(ID);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        Item expectedItem = responseEntity.getBody();
        assertNotNull(expectedItem);
    }

    @Test
    public void testGetItemsByName() {
        // Arrange
        when(itemRepositoryMock.findByName(ITEM_NAME)).thenReturn(new ArrayList<>(Collections.singletonList(item)));

        // Act
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(ITEM_NAME);

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        List<Item> expectedItems = responseEntity.getBody();
        assertNotNull(expectedItems);
        assertEquals(1, expectedItems.size());
    }

    @Test
    public void testGetItemsByNameWithInvalidName() {
        // Arrange
        when(itemRepositoryMock.findByName("Invalid Name")).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("Invalid Name");

        // Assert
        assertNotNull(responseEntity);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
    }
}