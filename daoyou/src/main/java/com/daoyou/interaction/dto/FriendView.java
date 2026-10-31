package com.daoyou.interaction.dto;

import java.time.LocalDateTime;

public record FriendView(String uuid, String username, String avatar,
 String introduction, LocalDateTime friendSince) {
}
