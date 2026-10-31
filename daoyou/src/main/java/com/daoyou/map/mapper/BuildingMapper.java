package com.daoyou.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daoyou.map.entity.Building;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BuildingMapper extends BaseMapper<Building> {

 List<Building> selectOpen();

 Building selectOpenById(@Param("id") Long id);

 int countByName(@Param("name") String name, @Param("excludeId") Long excludeId);

 int logicalDelete(@Param("id") Long id);
}
