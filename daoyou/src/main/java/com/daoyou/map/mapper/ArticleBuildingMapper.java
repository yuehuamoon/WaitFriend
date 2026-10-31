package com.daoyou.map.mapper;

import org.apache.ibatis.annotations.Param;

public interface ArticleBuildingMapper {

 int countByBuildingId(@Param("buildingId") Long buildingId);
}
