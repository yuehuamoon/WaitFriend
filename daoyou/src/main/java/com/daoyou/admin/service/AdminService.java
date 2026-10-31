package com.daoyou.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daoyou.admin.entity.AdminOperationLog;
import com.daoyou.admin.mapper.AdminMapper;
import com.daoyou.content.service.ArticleService;
import com.daoyou.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

 private final AdminMapper adminMapper;
 private final UserService userService;
 private final ArticleService articleService;
 private final ObjectMapper objectMapper;

 @Transactional
 public void ban(String adminUuid, String targetUuid) {
 userService.delete(adminUuid, targetUuid);
 log(adminUuid, "ban_user", "user", targetUuid, Map.of());
 }

 @Transactional
 public void unban(String adminUuid, String targetUuid) {
 userService.restore(adminUuid, targetUuid);
 log(adminUuid, "unban_user", "user", targetUuid, Map.of());
 }

 @Transactional
 public int adjustCultivation(String adminUuid, String targetUuid, BigDecimal delta) {
 if (delta == null || delta.compareTo(BigDecimal.ZERO) ==0) {
 throw new com.daoyou.common.BizException("400", "修为调整值不能为0");
 }
 if (delta.signum() <0) {
 throw new com.daoyou.common.BizException("400", "修为只增不减，不能下调");
 }
 int rows = userService.addCultivation(targetUuid, delta, "admin_adjust",
 "admin:" + adminUuid);
 log(adminUuid, "adjust_cultivation", "user", targetUuid, Map.of("delta", delta));
 return rows;
 }

 @Transactional
 public void deleteArticle(String adminUuid, Long articleId) {
 articleService.delete(adminUuid, articleId);
 log(adminUuid, "delete_article", "article", articleId.toString(), Map.of());
 }

 public IPage<AdminOperationLog> logs(long page, long size) {
 return adminMapper.selectLogs(new Page<>(Math.max(page,1), Math.min(Math.max(size,1),100)));
 }

 private void log(String adminUuid, String operation, String targetType, String targetId,
 Map<String, Object> detail) {
 AdminOperationLog log = new AdminOperationLog();
 log.setAdminUuid(adminUuid);
 log.setOperation(operation);
 log.setTargetType(targetType);
 log.setTargetId(targetId);
 try {
 log.setDetail(objectMapper.writeValueAsString(detail));
 } catch (JsonProcessingException exception) {
 log.setDetail("{}");
 }
 adminMapper.insertLog(log);
 }
}
