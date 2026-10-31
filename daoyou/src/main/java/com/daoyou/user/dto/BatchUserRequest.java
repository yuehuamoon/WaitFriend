package com.daoyou.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BatchUserRequest(
 @NotEmpty(message = "用户列表不能为空")
 @Size(max =100, message = "单次最多查询100位用户") List<String> uuids
) {
}
