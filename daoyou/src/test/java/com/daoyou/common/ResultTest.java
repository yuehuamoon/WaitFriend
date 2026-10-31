package com.daoyou.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ResultTest {

 @Test
 void successIsExplicit() {
 Result<String> result = Result.success("data");
 assertTrue(result.isSuccess());
 assertEquals("data", result.getData());
 }

 @Test
 void defaultFailureHasRequiredShape() {
 Result<Void> result = Result.fail("失败");
 assertFalse(result.isSuccess());
 assertEquals("500", result.getErrorCode());
 assertEquals("失败", result.getMessage());
 }
}
