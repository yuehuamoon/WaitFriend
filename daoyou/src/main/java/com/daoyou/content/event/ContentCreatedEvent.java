package com.daoyou.content.event;

public record ContentCreatedEvent(Long articleId, String articleUuid, String authorUuid,
 String title) {
}
