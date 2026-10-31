package com.daoyou.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daoyou.notification.entity.Notification;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface NotificationMapper extends BaseMapper<Notification> {
 List<Notification> selectNotifications(@Param("userUuid") String userUuid,
 @Param("unreadOnly") boolean unreadOnly);
 int markRead(@Param("userUuid") String userUuid, @Param("id") Long id);
 int unreadCount(@Param("userUuid") String userUuid);
}
