package com.daoyou.user.controller;

import com.daoyou.common.Result;
import cn.dev33.satoken.annotation.SaIgnore;
import com.daoyou.common.UserContext;
import com.daoyou.user.dto.AuthView;
import com.daoyou.user.dto.BatchUserRequest;
import com.daoyou.user.dto.LoginRequest;
import com.daoyou.user.dto.PasswordUpdateRequest;
import com.daoyou.user.dto.ProfileUpdateRequest;
import com.daoyou.user.dto.RegisterRequest;
import com.daoyou.user.dto.UserBrief;
import com.daoyou.user.dto.UserDetail;
import com.daoyou.user.dto.UserRankItem;
import com.daoyou.user.service.UserService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

 private final UserService userService;

 @PostMapping("/register")
 public Result<AuthView> register(@Valid @RequestBody RegisterRequest request) {
 return Result.success(userService.register(request));
 }

 @PostMapping("/login")
 public Result<AuthView> login(@Valid @RequestBody LoginRequest request) {
 return Result.success(userService.login(request));
 }

 @PostMapping("/logout")
 public Result<Void> logout() {
 userService.logout();
 return Result.success(null);
 }

 @GetMapping("/me")
 public Result<UserDetail> me() {
 return Result.success(userService.me());
 }

 @GetMapping("/leaderboard")
 @SaIgnore
 public Result<List<UserRankItem>> leaderboard() {
 return Result.success(userService.leaderboard());
 }

 @GetMapping("/search")
 @SaIgnore
 public Result<List<UserBrief>> search(@RequestParam(required = false) BigDecimal minCultivation,
 @RequestParam(required = false) BigDecimal maxCultivation) {
 return Result.success(userService.search(minCultivation, maxCultivation));
 }

 @PostMapping("/batch")
 public Result<List<UserBrief>> batch(@Valid @RequestBody BatchUserRequest request) {
 return Result.success(userService.batch(request.uuids()));
 }

 @PutMapping("/profile")
 public Result<Integer> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
 return Result.success(userService.updateProfile(UserContext.requireUserId(), request));
 }

 @PutMapping("/password")
 public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
 userService.updatePassword(UserContext.requireUserId(), request);
 return Result.success(null);
 }

 @DeleteMapping("/{uuid}")
 public Result<Void> delete(@PathVariable String uuid) {
 userService.delete(UserContext.requireUserId(), uuid);
 return Result.success(null);
 }

 @PostMapping("/{uuid}/restore")
 public Result<Void> restore(@PathVariable String uuid) {
 userService.restore(UserContext.requireUserId(), uuid);
 return Result.success(null);
 }

 @GetMapping("/{uuid}")
 @SaIgnore
 public Result<UserBrief> get(@PathVariable String uuid) {
 return Result.success(userService.getBrief(uuid));
 }
}
