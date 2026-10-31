package com.daoyou.map.dto;

import com.daoyou.map.entity.Building;
import java.time.LocalDateTime;

public record BuildingView(Long id, String name, String region, String description,
 String coverUrl, Integer sortOrder, Integer status, LocalDateTime createTime) {

 public static BuildingView from(Building building) {
 return new BuildingView(building.getId(), building.getName(), building.getRegion(),
 building.getDescription(), building.getCoverUrl(), building.getSortOrder(),
 building.getStatus(), building.getCreateTime());
 }
}
