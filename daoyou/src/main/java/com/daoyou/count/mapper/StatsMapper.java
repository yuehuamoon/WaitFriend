package com.daoyou.count.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface StatsMapper {
 int incrementDaily(@Param("date") LocalDate date, @Param("metric") String metric, @Param("delta") long delta);
 int incrementTotal(@Param("metric") String metric, @Param("delta") long delta);
 List<Map<String, Object>> selectDaily(@Param("date") LocalDate date);
 long countUsers();
 long countArticles();
 long countAgrees();
}
