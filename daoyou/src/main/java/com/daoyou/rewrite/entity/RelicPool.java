package com.daoyou.rewrite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("relic_pool")
public class RelicPool {
 @TableId(type = IdType.AUTO)
 private Long id;
 private Long articleId;
 private Integer generation;
 private Integer poolRound;
 private String userUuid;
 private String content;
 private Integer likeCount;
 private String status;
 private LocalDateTime mergeDeadline;
 private LocalDateTime mergedAt;
 private LocalDateTime createTime;
}
