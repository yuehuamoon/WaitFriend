package com.daoyou.interaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FriendRequestCreate(@NotBlank(message = "对方用户不能为空") String toUuid,
 @Size(max =128, message = "附言不能超过128字") String message) {
}
