package com.siemens.internship;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testValidItem() {
        Item item = new Item();
        item.setName("Test");
        item.setDescription("Test");
        item.setStatus("ACTIVE");
        item.setEmail("test@test.com");

        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertTrue(constraintViolations.isEmpty());
    }


    @Test
    public void testInvalidName() {
        Item item = new Item();
        item.setDescription("Test");
        item.setStatus("ACTIVE");
        item.setEmail("test@test.com");
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testInvalidDescription() {
        Item item = new Item();
        item.setName("Test");
        item.setStatus("ACTIVE");
        item.setEmail("test@test.com");
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testInvalidStatus() {
        Item item = new Item();
        item.setName("Test");
        item.setDescription("Test");
        item.setEmail("test@test.com");
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertFalse(constraintViolations.isEmpty());
    }

    @Test
    public void testInvalidEmail() {
        Item item = new Item();
        item.setName("Test");
        item.setDescription("Test");
        item.setStatus("ACTIVE");
        item.setEmail("notAnEmail");
        Set<ConstraintViolation<Item>> constraintViolations = validator.validate(item);
        assertFalse(constraintViolations.isEmpty());
        assertEquals("Email is not valid.", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testToString() {
        Item item = new Item();
        item.setName("Test");
        item.setDescription("Test");
        item.setStatus("ACTIVE");
        item.setEmail("test@test.com");
        assertEquals("Test Test ACTIVE test@test.com", item.toString());

    }
}
