package com.daoyou.user.dto;

public record AuthView(String token, long expireAt) {
}
