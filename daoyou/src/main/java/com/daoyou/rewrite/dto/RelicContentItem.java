package com.daoyou.rewrite.dto;

public record RelicContentItem(String kind, Long segmentId, Integer seq, String userUuid,
 String username, String content, boolean editable) {
}
