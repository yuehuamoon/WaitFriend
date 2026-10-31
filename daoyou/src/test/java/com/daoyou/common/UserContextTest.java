package com.daoyou.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class UserContextTest {

 @AfterEach
 void clear() {
 UserContext.clear();
 }

 @Test
 void requireUserIdDoesNotAllowAnonymousContext() {
 assertThrows(BizException.class, UserContext::requireUserId);
 UserContext.set("user-1");
 assertEquals("user-1", UserContext.requireUserId());
 }
}
