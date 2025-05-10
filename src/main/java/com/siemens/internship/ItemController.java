package com.siemens.internship;

import jakarta.validation.Valid;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return new ResponseEntity<>(itemService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody Item item, BindingResult result) {
        //fixed: Changed HttpStatus due to wrong logic. Added error handling.

        if (result.hasErrors()) {
            String error = result.getFieldError().getDefaultMessage();
            //before: HttpStatus was "CREATED"
            return new ResponseEntity<>("Error: " + error, HttpStatus.BAD_REQUEST);
        }
        //before: HttpStatus was "BAD_REQUEST"
        return new ResponseEntity<>(itemService.save(item), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        //Changed the function a bit for better error handling

        Optional<Item> item = itemService.findById(id);
        if (item.isPresent()) {
            return new ResponseEntity<>(item.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("No such item exists.", HttpStatus.NOT_FOUND);


    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody Item item) {
        //fixed: Changed HttpStatus due to wrong/weird logic

        Optional<Item> existingItem = itemService.findById(id);
        if (existingItem.isPresent()) {
            item.setId(id);
            //before: HttpStatus was "CREATED"
            return new ResponseEntity<>(itemService.save(item), HttpStatus.OK);
        }
        return new ResponseEntity<>("No such item exists.", HttpStatus.NOT_FOUND);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        //fixed: Added existence check and fixed logic.

        Optional<Item> existingItem = itemService.findById(id);
        if (existingItem.isPresent()) {
            itemService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("No such item exist.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/process")
    public CompletableFuture<ResponseEntity<List<Item>>> processItems() {
        // The async method now returns a CompletableFuture<ResponseEntity<List<Item>>>

        return itemService.processItemsAsync().thenApply(items -> {
            return new ResponseEntity<>(items, HttpStatus.OK);
        }).exceptionally(e -> {
            System.out.println("Error processing items asynchronously " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        });
    }

}
