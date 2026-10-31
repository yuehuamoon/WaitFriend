package com.daoyou.interaction.dto;

import com.daoyou.interaction.entity.Chat;
import com.daoyou.user.dto.UserBrief;
import java.time.LocalDateTime;

public record ChatView(Long id, String userUuid, String username, String avatar,
 String content, LocalDateTime createTime) {
 public static ChatView from(Chat chat, UserBrief user) {
 return new ChatView(chat.getId(), chat.getUserUuid(), user == null ? "无名道友" : user.username(),
 user == null ? null : user.avatar(), chat.getContent(), chat.getCreateTime());
 }
}
