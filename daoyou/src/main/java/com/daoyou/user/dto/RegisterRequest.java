package com.daoyou.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
 @NotBlank(message = "道号不能为空")
 String username,
 @NotBlank(message = "密码不能为空")
 @Size(min =8, max =32, message = "密码长度须为8-32位")
 @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "密码必须同时包含字母和数字")
 String password
) {
}
