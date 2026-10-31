package com.daoyou.map.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BuildingRequest(
 @NotBlank(message = "板块名称不能为空")
 @Size(max =64, message = "板块名称不能超过64字") String name,
 @Size(max =64, message = "区域名称不能超过64字") String region,
 @Size(max =255, message = "板块简介不能超过255字") String description,
 @Size(max =255, message = "封面地址过长") String coverUrl,
 Integer sortOrder
) {
}
