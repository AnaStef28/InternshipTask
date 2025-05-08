package com.siemens.internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemService itemService;

    private Item item;

    @BeforeEach
    public void setUp() {
        item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setStatus("Active");
        item.setEmail("Test@test.com");
    }

    @Test
    public void testGetAllItems() {
        when(itemService.findAll()).thenReturn(List.of(item));

        ResponseEntity<List<Item>> response = itemController.getAllItems();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testCreateItem() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(itemService.save(any(Item.class))).thenReturn(item);

        ResponseEntity<?> response = itemController.createItem(item, bindingResult);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(item, response.getBody());
    }

    @Test
    public void testCreateItemInvalid() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError()).thenReturn(new FieldError("item", "email", "Invalid email"));

        ResponseEntity<?> response = itemController.createItem(item, bindingResult);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error: Invalid email"));
    }

    @Test
    public void testGetItemById() {

        item.setId(1L);
        when(itemService.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<?> response = itemController.getItemById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(item, response.getBody());

    }

    @Test
    public void testGetItemByIdInvalid() {

        when(itemService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = itemController.getItemById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("No such item exists"));

    }


    @Test
    public void testUpdateItem() {

        when(itemService.save(any(Item.class))).thenReturn(item);

    }


}
