package com.daoyou.user.dto;

import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
 @Size(max =255, message = "头像地址过长") String avatar,
 @Size(max =255, message = "简介不能超过255位") String introduction,
 @Size(max =255, message = "背景地址过长") String background
) {
}
