package com.daoyou.config;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.daoyou.common.UserContext;
import org.junit.jupiter.api.Test;

class UserContextFilterTest {

 @Test
 void contextIsThreadLocalAndCleared() {
 UserContext.set("alice");
 assertSame("alice", UserContext.get());
 UserContext.clear();
 assertNull(UserContext.get());
 }
}
