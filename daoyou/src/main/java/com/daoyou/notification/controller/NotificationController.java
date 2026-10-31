package com.daoyou.notification.controller;

import com.daoyou.common.Result;
import com.daoyou.common.UserContext;
import com.daoyou.notification.entity.Notification;
import com.daoyou.notification.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

 private final NotificationService notificationService;

 @GetMapping("/list")
 public Result<List<Notification>> list(@RequestParam(defaultValue = "false") boolean unreadOnly) {
 return Result.success(notificationService.list(UserContext.requireUserId(), unreadOnly));
 }

 @PostMapping("/{id}/read")
 public Result<Void> read(@PathVariable Long id) {
 notificationService.markRead(UserContext.requireUserId(), id);
 return Result.success(null);
 }

 @GetMapping("/unread-count")
 public Result<Integer> unreadCount() {
 return Result.success(notificationService.unreadCount(UserContext.requireUserId()));
 }
}
