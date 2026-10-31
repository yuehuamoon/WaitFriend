package com.daoyou.interaction.dto;

import com.daoyou.interaction.entity.FriendRequest;
import java.time.LocalDateTime;

public record FriendRequestView(Long id, String fromUuid, String fromUsername,
 String toUuid, String message, String status, LocalDateTime createTime) {
 public static FriendRequestView from(FriendRequest request, String username) {
 return new FriendRequestView(request.getId(), request.getFromUuid(), username,
 request.getToUuid(), request.getMessage(), request.getStatus(), request.getCreateTime());
 }
}
