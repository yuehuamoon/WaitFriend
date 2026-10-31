package com.daoyou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan(basePackages = {"com.daoyou.user.mapper", "com.daoyou.map.mapper",
 "com.daoyou.content.mapper", "com.daoyou.interaction.mapper",
 "com.daoyou.rewrite.mapper", "com.daoyou.notification.mapper",
 "com.daoyou.count.mapper", "com.daoyou.admin.mapper"})
@SpringBootApplication
public class DaoyouApplication {

 public static void main(String[] args) {
 SpringApplication.run(DaoyouApplication.class, args);
 }
}
