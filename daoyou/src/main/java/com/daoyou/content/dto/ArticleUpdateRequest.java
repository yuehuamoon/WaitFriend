package com.daoyou.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ArticleUpdateRequest(
 @NotBlank(message = "标题不能为空") @Size(max =128, message = "标题不能超过128字") String title,
 @NotBlank(message = "正文不能为空") String body,
 List<String> imgUrls
) {
}
