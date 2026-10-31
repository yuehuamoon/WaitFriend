package com.daoyou.rewrite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RelicSegmentRequest(@NotBlank(message = "续写内容不能为空")
 @Size(max =500, message = "续写内容不能超过500字") String content) {
}
