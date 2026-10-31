package com.daoyou.user.dto;

import java.math.BigDecimal;

public record UserRankItem(String uuid, String username, String avatar, BigDecimal cultivation) {
}
