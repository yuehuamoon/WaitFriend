package com.daoyou.content.dto;

import java.util.List;

public record ArticleDraft(Long buildingId, String title, String body,
 List<String> imgUrls, String type) {
}
