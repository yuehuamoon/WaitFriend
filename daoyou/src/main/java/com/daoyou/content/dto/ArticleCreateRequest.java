package com.daoyou.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ArticleCreateRequest(
 @NotNull(message = "板块不能为空") Long buildingId,
 @NotBlank(message = "标题不能为空") @Size(max =128, message = "标题不能超过128字") String title,
 @NotBlank(message = "正文不能为空") String body,
 List<String> imgUrls,
 @Pattern(regexp = "^(normal|relic)?$", message = "文章类型无效") String type
) {
}
