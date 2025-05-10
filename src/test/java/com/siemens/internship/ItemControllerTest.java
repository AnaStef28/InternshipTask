package com.siemens.internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        item = new Item(1L, "Test Item", "Description", "Active", "Test@test.com");
    }

    @Test
    public void testGetAllItems() {
        when(itemService.findAll()).thenReturn(List.of(item));

        ResponseEntity<List<Item>> response = itemController.getAllItems();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testCreateItem() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(itemService.save(any(Item.class))).thenReturn(item);

        ResponseEntity<?> response = itemController.createItem(item, bindingResult);

        assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(item, response.getBody());
    }

    @Test
    public void testCreateItemInvalid() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError()).thenReturn(new FieldError("item", "email", "Invalid email"));

        ResponseEntity<?> response = itemController.createItem(item, bindingResult);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error: Invalid email"));
    }

    @Test
    public void testGetItemById() {

        when(itemService.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<?> response = itemController.getItemById(1L);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(item, response.getBody());

    }

    @Test
    public void testGetItemByIdInvalid() {

        when(itemService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = itemController.getItemById(1L);

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No such item exists."));

    }


    @Test
    public void testUpdateItem() {

        Item newItem = new Item(2L, "Updated Item", "Updated Description", "Active", "valid@mail.com");

        when(itemService.findById(1L)).thenReturn(Optional.of(item));
        when(itemService.save(any(Item.class))).thenReturn(newItem);

        ResponseEntity<?> response = itemController.updateItem(1L, newItem);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newItem, response.getBody());

    }

    @Test
    public void testUpdateItemInvalid() {

        Item newItem = new Item(2L, "Updated Item", "Updated Description", "Active", "valid@mail.com");

        when(itemService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = itemController.updateItem(1L, newItem);
        System.out.printf(response.getStatusCode().toString());
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No such item exists."));

    }

    @Test
    public void testDeleteItem() {

        when(itemService.findById(1L)).thenReturn(Optional.of(item));
        ResponseEntity<?> response = itemController.deleteItem(1L);
        assertEquals(200, response.getStatusCodeValue());

    }

    @Test
    public void testDeleteItemInvalid() {
        when(itemService.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = itemController.deleteItem(1L);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No such item exist."));
    }

    @Test
    public void testProcessItems() {
        List<Item> itemList = List.of(item);
        CompletableFuture<List<Item>> futureItems = CompletableFuture.completedFuture(itemList);
        when(itemService.processItemsAsync()).thenReturn(futureItems);

        CompletableFuture<ResponseEntity<List<Item>>> responseFuture = itemController.processItems();
        try{
        ResponseEntity<List<Item>> response = responseFuture.get();
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(itemList, response.getBody());
        }
        catch (InterruptedException | ExecutionException ignored) {}

    }

    @Test
    public void testProcessItemsInvalid() {
        CompletableFuture<List<Item>> fail = new CompletableFuture<>();
        fail.completeExceptionally(new RuntimeException());

        when(itemService.processItemsAsync()).thenReturn(fail);

        CompletableFuture<ResponseEntity<List<Item>>> responseFuture = itemController.processItems();
        try{
            ResponseEntity<List<Item>> response = responseFuture.get();
            assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
            assertNull(response.getBody());
        }
        catch (InterruptedException | ExecutionException ignored) {}

    }

}
