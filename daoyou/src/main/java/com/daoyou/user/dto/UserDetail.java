package com.daoyou.user.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserDetail(String uuid, String username, String avatar,
 String introduction, String background, BigDecimal cultivation,
 LocalDateTime createTime) {
}
