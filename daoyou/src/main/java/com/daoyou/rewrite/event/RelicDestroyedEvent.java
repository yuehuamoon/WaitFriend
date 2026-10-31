package com.daoyou.rewrite.event;

import java.util.List;

public record RelicDestroyedEvent(Long articleId, Integer generation, List<String> participantUuids) {
}
