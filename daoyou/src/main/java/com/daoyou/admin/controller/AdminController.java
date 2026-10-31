package com.daoyou.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.daoyou.admin.dto.AdminCultivationRequest;
import com.daoyou.admin.entity.AdminOperationLog;
import com.daoyou.admin.service.AdminService;
import com.daoyou.common.Result;
import com.daoyou.common.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@SaCheckRole("admin")
@RequiredArgsConstructor
public class AdminController {

 private final AdminService adminService;

 @PostMapping("/user/{uuid}/ban")
 public Result<Void> ban(@PathVariable String uuid) {
 adminService.ban(UserContext.requireUserId(), uuid);
 return Result.success(null);
 }

 @PostMapping("/user/{uuid}/unban")
 public Result<Void> unban(@PathVariable String uuid) {
 adminService.unban(UserContext.requireUserId(), uuid);
 return Result.success(null);
 }

 @PostMapping("/user/{uuid}/cultivation")
 public Result<Integer> cultivation(@PathVariable String uuid,
 @Valid @RequestBody AdminCultivationRequest request) {
 return Result.success(adminService.adjustCultivation(UserContext.requireUserId(), uuid, request.delta()));
 }

 @DeleteMapping("/article/{id}")
 public Result<Void> deleteArticle(@PathVariable Long id) {
 adminService.deleteArticle(UserContext.requireUserId(), id);
 return Result.success(null);
 }

 @GetMapping("/logs")
 public Result<IPage<AdminOperationLog>> logs(@RequestParam(defaultValue = "1") long page,
 @RequestParam(defaultValue = "20") long size) {
 return Result.success(adminService.logs(page, size));
 }
}
