package com.daoyou.interaction.event;

public record ReactionCreatedEvent(Long articleId, String articleTitle, String articleAuthorUuid,
 String actorUuid, String type) {
}
