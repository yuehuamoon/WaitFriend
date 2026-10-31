package com.daoyou.count.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import lombok.Data;

@Data
@TableName("stats_daily")
public class StatsDaily {
 @TableId(type = IdType.AUTO)
 private Long id;
 private LocalDate statDate;
 private String metric;
 private Long value;
}
