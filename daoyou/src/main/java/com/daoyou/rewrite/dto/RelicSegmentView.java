package com.daoyou.rewrite.dto;

import java.time.LocalDateTime;

public record RelicSegmentView(Long id, Long articleId, Integer generation, Integer seq, Integer poolRound,
 String userUuid, String username, String content, LocalDateTime mergedAt) {
}
