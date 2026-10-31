package com.daoyou.rewrite.dto;

import java.time.LocalDateTime;

public record RelicPoolView(Long id, Long articleId, Integer generation, Integer poolRound,
 String userUuid, String username, String content, Integer likeCount, String status,
 LocalDateTime mergeDeadline, LocalDateTime createTime) {
}
