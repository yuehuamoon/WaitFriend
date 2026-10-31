package com.daoyou.common;

public final class RedisKeys {

 public static final String LOGIN_FAIL = "login:fail:";
 public static final String LEADERBOARD = "cache:leaderboard";
 public static final String USER = "cache:user:";
 public static final String CULTIVATION_DAILY = "cult:daily:";
 public static final String CULTIVATION_CHAT_DAILY = "cult:source:chat:";
 public static final String CHAT_COOLDOWN = "chat:cd:";

 private RedisKeys() {
 }
}
