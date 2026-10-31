package com.daoyou.rewrite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RelicContentUpdateRequest(@NotBlank(message = "内容不能为空")
 @Size(max =500, message = "内容不能超过500字") String content) {
}
