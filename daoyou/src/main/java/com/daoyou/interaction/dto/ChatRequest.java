package com.daoyou.interaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(@NotBlank(message = "聊天内容不能为空")
 @Size(max =200, message = "聊天内容不能超过200字") String content) {
}
