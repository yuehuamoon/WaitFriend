package com.daoyou.rewrite.event;

public record RelicMergedEvent(Long articleId, String authorUuid, Integer generation,
 Integer poolRound) {
}
