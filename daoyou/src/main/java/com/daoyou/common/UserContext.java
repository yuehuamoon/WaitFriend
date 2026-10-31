package com.daoyou.common;

public final class UserContext {

 private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

 private UserContext() {
 }

 public static void set(String userId) {
 HOLDER.set(userId);
 }

 public static String get() {
 return HOLDER.get();
 }

 public static String requireUserId() {
 String userId = HOLDER.get();
 if (userId == null || userId.isBlank()) {
 throw new BizException(ErrorCodes.UNAUTHORIZED, "请先登录");
 }
 return userId;
 }

 public static void clear() {
 HOLDER.remove();
 }
}
