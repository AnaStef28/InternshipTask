package com.siemens.internship;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final AtomicInteger processedCount = new AtomicInteger(0);

    @Setter
    @Autowired
    @Qualifier("myExecutor")
    public Executor executor;


    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     * <p>
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */
    @Async("myExecutor")
    public CompletableFuture<List<Item>> processItemsAsync() {
        //fixed: Made it asynchronous

        List<Long> itemIds = itemRepository.findAllIds();

        //Added futures list to ensure completion
        List<CompletableFuture<Item>> futures = new ArrayList<>();

        for (Long id : itemIds) {

            // Use CompletableFuture for asynchronicity
            CompletableFuture<Item> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(100);

                    Optional<Item> optionalItem = itemRepository.findById(id);
                    if (optionalItem.isEmpty()) return null;

                    processedCount.incrementAndGet();

                    Item item = optionalItem.get();
                    item.setStatus("PROCESSED");
                    itemRepository.save(item);

                    return item;


                } catch (InterruptedException e) {
                    //If there is an error, we interrupt the current thread
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during processing", e);
                }
            }, executor);

            //Adding the future to the list
            futures.add(future);

        }

        //Wait for completion
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        //Return all processed items
        return allOf.thenApply(v ->
                futures.stream().
                        map(CompletableFuture::join).
                        filter(Objects::nonNull).
                        toList()
        );
    }

}

