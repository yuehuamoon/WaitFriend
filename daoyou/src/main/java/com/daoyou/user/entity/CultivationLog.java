package com.daoyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("cultivation_logs")
public class CultivationLog {

 @TableId(type = IdType.AUTO)
 private Long id;
 private String userUuid;
 private BigDecimal deltaRequested;
 private BigDecimal deltaActual;
 private String source;
 private String refId;
 private LocalDateTime createTime;
}
