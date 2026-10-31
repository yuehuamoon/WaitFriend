package com.daoyou.user.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InternalCultivationRequest(
 @NotBlank(message = "用户标识不能为空") String uuid,
 @NotNull(message = "修为增量不能为空")
 @DecimalMin(value = "0.01", message = "修为增量必须大于0") BigDecimal delta
) {
}
