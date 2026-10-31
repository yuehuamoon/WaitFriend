package com.daoyou.count.service;

import com.daoyou.count.dto.PlatformStats;
import com.daoyou.count.mapper.StatsMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatsService {

 private final StatsMapper statsMapper;

 @Transactional
 public void increment(String metric, long delta) {
 LocalDate today = LocalDate.now();
 statsMapper.incrementDaily(today, metric, delta);
 statsMapper.incrementTotal(metric, delta);
 }

 public List<Map<String, Object>> daily(LocalDate date) {
 return statsMapper.selectDaily(date == null ? LocalDate.now() : date);
 }

 public PlatformStats platform() {
 return new PlatformStats(statsMapper.countUsers(), statsMapper.countArticles(), statsMapper.countAgrees());
 }
}
