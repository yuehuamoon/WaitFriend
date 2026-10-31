package com.daoyou.map.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import com.daoyou.common.Result;
import com.daoyou.map.dto.BuildingRequest;
import com.daoyou.map.dto.BuildingView;
import com.daoyou.map.service.BuildingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map/buildings")
@RequiredArgsConstructor
public class BuildingController {

 private final BuildingService buildingService;

 @GetMapping
 @SaIgnore
 public Result<List<BuildingView>> list() {
 return Result.success(buildingService.listOpen());
 }

 @GetMapping("/{id}")
 @SaIgnore
 public Result<BuildingView> get(@PathVariable Long id) {
 return Result.success(buildingService.get(id));
 }

 @PostMapping
 @SaCheckRole("admin")
 public Result<BuildingView> create(@Valid @RequestBody BuildingRequest request) {
 return Result.success(buildingService.create(request));
 }

 @PutMapping("/{id}")
 @SaCheckRole("admin")
 public Result<BuildingView> update(@PathVariable Long id, @Valid @RequestBody BuildingRequest request) {
 return Result.success(buildingService.update(id, request));
 }

 @DeleteMapping("/{id}")
 @SaCheckRole("admin")
 public Result<Void> delete(@PathVariable Long id) {
 buildingService.delete(id);
 return Result.success(null);
 }
}
