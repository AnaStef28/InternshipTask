package com.siemens.internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    private Executor executor = Executors.newFixedThreadPool(2);
    @BeforeEach
    public void setup() {
        service.setExecutor(executor);
    }

    @InjectMocks
    private ItemService service;

    @Test
    public void testConstructor() {
        assertNotNull(service);
    }

    @Test
    public void testFindAllEmpty() {
        when(service.findAll()).thenReturn(List.of());
        List<Item> items = service.findAll();
        assertEquals(0, items.size());
    }

    @Test
    public void testFindAll() {
        Item item= new Item();
        when(service.findAll()).thenReturn(List.of(item));
        List<Item> items = service.findAll();
        assertEquals(1, items.size());
    }

    @Test
    public void testFindById() {
        Item item = new Item(1L, "Test Item", "This is a test item", "NEW", "test@test.com");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Optional<Item> opItem = service.findById(1L);
        assertTrue(opItem.isPresent());
        assertEquals(1L, opItem.get().getId());
        assertEquals("This is a test item", opItem.get().getDescription());
        assertEquals("NEW", opItem.get().getStatus());
        assertEquals("test@test.com", opItem.get().getEmail());
        assertEquals("Test Item", opItem.get().getName());
    }

    @Test
    public void testFindByIdInvalid() {
        ItemService service = new ItemService(itemRepository);
        Optional<Item> opItem = service.findById(1L);
        assertFalse(opItem.isPresent());
    }


    @Test
    public void testSaveItem() {
        Item item = new Item();
        when(itemRepository.save(item)).thenReturn(item);
        Item saved = service.save(item);
        assertEquals(item, saved);
    }

    @Test
    public void testDeleteItem() {
        Long itemId = 1L;
        service.deleteById(itemId);
        verify(itemRepository).deleteById(itemId);

    }

    @Test
    public void testProcessItemsAsync() throws Exception {
        Item item1 = new Item(1L, "Test Item", "This is a test item", "NEW", "test@test.com");
        Item item2 = new Item(2L, "Test Item 2", "This is a test item2", "NEW", "test2@test.com");

        when(itemRepository.findAllIds()).thenReturn(List.of(1L, 2L));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setStatus("PROCESSED");
            return item;
        });

        CompletableFuture<List<Item>> future = service.processItemsAsync();
        List<Item> result = future.get(3, TimeUnit.SECONDS);

        assertEquals(2, result.size());
        assertEquals("PROCESSED", result.get(0).getStatus());
        assertEquals("PROCESSED", result.get(1).getStatus());

        verify(itemRepository).save(item1);
        verify(itemRepository).save(item2);
    }

}


class AsyncTester{
    private Thread thread;
    private volatile Error error;
    private volatile RuntimeException runtimeExc;

    public AsyncTester(final Runnable runnable) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Error e) {
                    error = e;
                } catch (RuntimeException e) {
                    runtimeExc = e;
                }
            }
        });
    }

    public void start() {
        thread.start();
    }

    public void test() throws InterruptedException {
        thread.join();
        if (error != null)
            throw error;
        if (runtimeExc != null)
            throw runtimeExc;
    }
}

