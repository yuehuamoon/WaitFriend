package com.daoyou.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("articles")
public class Article {

 @TableId(type = IdType.AUTO)
 private Long id;
 private String uuid;
 private String authorUuid;
 private Long buildingId;
 private String title;
 private String body;
 private String imgUrls;
 private String type;
 private Integer agreeCount;
 private Integer collectCount;
 private Integer chatCount;
 private Integer score;
 private String relicStatus;
 private String relicRootContent;
 private Integer relicGeneration;
 private Integer relicPoolRound;
 private String relicLastMergedUserUuid;
 private LocalDateTime relicLastMergedAt;
 private LocalDateTime relicLastDestroyedAt;
 @TableLogic
 private Integer isDeleted;
 private LocalDateTime createTime;
 private LocalDateTime updateTime;
}
