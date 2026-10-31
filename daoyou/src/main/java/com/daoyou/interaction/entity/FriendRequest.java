package com.daoyou.interaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("friend_requests")
public class FriendRequest {
 @TableId(type = IdType.AUTO)
 private Long id;
 private String fromUuid;
 private String toUuid;
 private String message;
 private String status;
 private LocalDateTime createTime;
}
