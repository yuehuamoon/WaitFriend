package com.daoyou.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("notifications")
public class Notification {
 @TableId(type = IdType.AUTO)
 private Long id;
 private String userUuid;
 private String type;
 private String title;
 private String content;
 private String payload;
 private LocalDateTime readAt;
 private LocalDateTime createTime;
}
