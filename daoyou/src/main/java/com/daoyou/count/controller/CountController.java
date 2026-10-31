package com.daoyou.count.controller;

import com.daoyou.common.Result;
import cn.dev33.satoken.annotation.SaIgnore;
import com.daoyou.count.dto.PlatformStats;
import com.daoyou.count.service.StatsService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/count")
@RequiredArgsConstructor
public class CountController {
 private final StatsService statsService;

 @GetMapping("/daily")
 @SaIgnore
 public Result<List<Map<String, Object>>> daily(
 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
 return Result.success(statsService.daily(date));
 }

 @GetMapping("/platform")
 @SaIgnore
 public Result<PlatformStats> platform() {
 return Result.success(statsService.platform());
 }
}
