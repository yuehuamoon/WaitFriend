package com.daoyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("`user`")
public class User {

 @TableId(type = IdType.INPUT)
 private String uuid;
 private String username;
 private String password;
 private String avatar;
 private String introduction;
 private String background;
 private BigDecimal cultivation;
 @TableLogic
 private Integer isDeleted;
 private LocalDateTime createTime;
 private LocalDateTime updateTime;
}
