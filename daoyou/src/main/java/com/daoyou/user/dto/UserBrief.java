package com.daoyou.user.dto;

import java.math.BigDecimal;

public record UserBrief(String uuid, String username, String avatar,
 String introduction, BigDecimal cultivation) {
}
