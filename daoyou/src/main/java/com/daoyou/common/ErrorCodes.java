package com.daoyou.common;

public final class ErrorCodes {

 public static final String LOGIN_FAILED = "1001";
 public static final String LOGIN_LOCKED = "1002";
 public static final String USERNAME_EXISTS = "1003";
 public static final String OLD_PASSWORD_MISMATCH = "1004";
 public static final String DAILY_CULTIVATION_LIMIT = "1005";
 public static final String USER_NOT_FOUND = "1006";
 public static final String BUILDING_NAME_EXISTS = "2001";
 public static final String BUILDING_HAS_ARTICLES = "2002";
 public static final String BUILDING_NOT_FOUND = "2003";
 public static final String CHAT_COOLDOWN = "4001";
 public static final String FRIEND_REQUEST_INVALID = "5001";
 public static final String BAD_REQUEST = "400";
 public static final String UNAUTHORIZED = "401";
 public static final String FORBIDDEN = "403";
 public static final String VALIDATION_ERROR = "422";
 public static final String DUPLICATE_DATA = "409";
 public static final String SERVER_ERROR = "500";

 private ErrorCodes() {
 }
}
