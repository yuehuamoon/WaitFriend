package com.daoyou.admin.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AdminCultivationRequest(@NotNull(message = "修为增量不能为空") BigDecimal delta) {
}
