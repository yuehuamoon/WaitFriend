package com.daoyou.interaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("friends")
public class Friend {
 @TableId(type = IdType.AUTO)
 private Long id;
 private String userUuid;
 private String friendUuid;
 private LocalDateTime createTime;
}
