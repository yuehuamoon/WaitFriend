package com.daoyou.interaction.event;

public record FriendRequestedEvent(Long requestId, String fromUuid, String toUuid) {
}
