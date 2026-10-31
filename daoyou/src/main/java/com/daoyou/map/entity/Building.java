package com.daoyou.map.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("buildings")
public class Building {

 @TableId(type = IdType.AUTO)
 private Long id;
 private String name;
 private String region;
 private String description;
 private String coverUrl;
 private Integer sortOrder;
 private Integer status;
 @TableLogic
 private Integer isDeleted;
 private LocalDateTime createTime;
 private LocalDateTime updateTime;
}
