package com.daoyou.notification.service;

import com.daoyou.notification.entity.Notification;
import com.daoyou.notification.mapper.NotificationMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

 private final NotificationMapper notificationMapper;

 public void create(String userUuid, String type, String title, String content, String payload) {
 Notification notification = new Notification();
 notification.setUserUuid(userUuid);
 notification.setType(type);
 notification.setTitle(title);
 notification.setContent(content);
 notification.setPayload(payload);
 notificationMapper.insert(notification);
 }

 public List<Notification> list(String userId, boolean unreadOnly) {
 return notificationMapper.selectNotifications(userId, unreadOnly);
 }

 public void markRead(String userId, Long id) {
 notificationMapper.markRead(userId, id);
 }

 public int unreadCount(String userId) {
 return notificationMapper.unreadCount(userId);
 }
}
