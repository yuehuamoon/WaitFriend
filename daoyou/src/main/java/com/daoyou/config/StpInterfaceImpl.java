package com.daoyou.config;

import cn.dev33.satoken.stp.StpInterface;
import com.daoyou.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

 private final UserService userService;

 @Override
 public List<String> getPermissionList(Object loginId, String loginType) {
 return List.of();
 }

 @Override
 public List<String> getRoleList(Object loginId, String loginType) {
 return userService.isAdmin(String.valueOf(loginId)) ? List.of("admin") : List.of("user");
 }
}
