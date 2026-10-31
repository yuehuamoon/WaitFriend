package com.daoyou.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("admin_operation_log")
public class AdminOperationLog {
 @TableId(type = IdType.AUTO)
 private Long id;
 private String adminUuid;
 private String operation;
 private String targetType;
 private String targetId;
 private String detail;
 private LocalDateTime createTime;
}
