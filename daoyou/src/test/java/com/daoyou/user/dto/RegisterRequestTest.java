package com.daoyou.user.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RegisterRequestTest {

 private static ValidatorFactory validatorFactory;
 private static Validator validator;

 @BeforeAll
 static void createValidator() {
 validatorFactory = Validation.buildDefaultValidatorFactory();
 validator = validatorFactory.getValidator();
 }

 @AfterAll
 static void closeValidatorFactory() {
 validatorFactory.close();
 }

 @Test
 void acceptsAnyNonBlankUsername() {
 RegisterRequest request = new RegisterRequest("太白居士-01", "pass1234");

 assertTrue(validator.validate(request).isEmpty());
 }

 @Test
 void rejectsBlankUsername() {
 RegisterRequest request = new RegisterRequest(" ", "pass1234");

 assertEquals(1, validator.validate(request).size());
 }
}
