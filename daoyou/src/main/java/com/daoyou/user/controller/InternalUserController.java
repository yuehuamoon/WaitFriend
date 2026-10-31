package com.daoyou.user.controller;

import com.daoyou.common.Result;
import com.daoyou.user.dto.InternalCultivationRequest;
import com.daoyou.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/user")
@RequiredArgsConstructor
public class InternalUserController {

 private final UserService userService;

 @PostMapping("/cultivation")
 public Result<Integer> addCultivation(@Valid @RequestBody InternalCultivationRequest request) {
 return Result.success(userService.addCultivationInternal(request));
 }
}
