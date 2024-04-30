package com.neuma573.autoboard.post.validation.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidContentValidatorTest {

    private final ValidContentValidator validator = new ValidContentValidator();

    @Test
    void testIsValid_withInvisibleCharOnly() {
        String inputWithInvisibleChars = "\u00AD";
        assertFalse(validator.isValid(inputWithInvisibleChars, null));
    }

    @Test
    void testIsValid_withInvisibleCharWithString() {
        String inputWithInvisibleCharAndString = "this is soft hypen:\u00AD";
        assertTrue(validator.isValid(inputWithInvisibleCharAndString, null));
    }

    @Test
    void testIsValid_withSpaces() {
        String inputWithSpaces = "   ";
        assertFalse(validator.isValid(inputWithSpaces, null));
    }

}