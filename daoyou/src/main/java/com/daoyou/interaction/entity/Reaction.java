package com.daoyou.interaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("reactions")
public class Reaction {
 @TableId(type = IdType.AUTO)
 private Long id;
 private Long articleId;
 private String userUuid;
 private String type;
 private LocalDateTime createTime;
}
