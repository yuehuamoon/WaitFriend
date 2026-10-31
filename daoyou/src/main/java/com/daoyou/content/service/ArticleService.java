package com.daoyou.content.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daoyou.common.BizException;
import com.daoyou.common.ErrorCodes;
import com.daoyou.content.dto.ArticleCreateRequest;
import com.daoyou.content.dto.ArticleCreated;
import com.daoyou.content.dto.ArticleDraft;
import com.daoyou.content.dto.ArticleUpdateRequest;
import com.daoyou.content.dto.ArticleView;
import com.daoyou.content.dto.PageView;
import com.daoyou.content.dto.SteleItem;
import com.daoyou.content.entity.Article;
import com.daoyou.content.event.ContentCreatedEvent;
import com.daoyou.content.mapper.ArticleMapper;
import com.daoyou.map.dto.BuildingView;
import com.daoyou.map.service.BuildingService;
import com.daoyou.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

 private final ArticleMapper articleMapper;
 private final BuildingService buildingService;
 private final UserService userService;
 private final ApplicationEventPublisher eventPublisher;
 private final ObjectMapper objectMapper;

 @Transactional
 public ArticleCreated create(String authorUuid, ArticleDraft draft) {
 buildingService.requireActive(draft.buildingId());
 Article article = new Article();
 article.setUuid(UUID.randomUUID().toString());
 article.setAuthorUuid(authorUuid);
 article.setBuildingId(draft.buildingId());
 article.setTitle(draft.title());
 article.setBody(draft.body());
 article.setImgUrls(toJson(draft.imgUrls()));
 String type = StringUtils.hasText(draft.type()) ? draft.type() : "normal";
 article.setType(type);
 article.setAgreeCount(0);
 article.setCollectCount(0);
 article.setChatCount(0);
 article.setScore(0);
 article.setRelicGeneration(0);
 article.setRelicPoolRound(0);
 article.setIsDeleted(0);
 if ("relic".equals(type)) {
 article.setRelicStatus("growing");
 article.setRelicRootContent(draft.body());
 }
 articleMapper.insert(article);
 try {
 userService.addCultivation(authorUuid, BigDecimal.ONE, "article_create", article.getUuid());
 } catch (RuntimeException exception) {
 log.warn("Award cultivation failed for article {}", article.getUuid(), exception);
 }
 eventPublisher.publishEvent(new ContentCreatedEvent(article.getId(), article.getUuid(),
 authorUuid, article.getTitle()));
 return new ArticleCreated(article.getId(), article.getUuid());
 }

 public ArticleView get(Long id) {
 Article article = articleMapper.selectActiveById(id);
 if (article == null) {
 throw new BizException("3001", "文章不存在");
 }
 return ArticleView.from(article);
 }

 public Article requireActive(Long id) {
 Article article = articleMapper.selectActiveById(id);
 if (article == null) {
 throw new BizException("3001", "文章不存在");
 }
 return article;
 }

 public PageView<ArticleView> list(Long buildingId, String tab, long page, long size) {
 buildingService.requireActive(buildingId);
 String normalizedTab = "new".equals(tab) ? tab : "hot";
 IPage<ArticleView> result = articleMapper.selectBuildingPage(
 new Page<>(Math.max(page,1), Math.min(Math.max(size,1),50)), buildingId, normalizedTab);
 return new PageView<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
 }

 public ArticleView update(String userId, Long id, ArticleUpdateRequest request) {
 Article article = requireActive(id);
 if (!article.getAuthorUuid().equals(userId)) {
 throw new BizException(ErrorCodes.FORBIDDEN, "只能修改自己的文章");
 }
 article.setTitle(request.title());
 article.setBody(request.body());
 article.setImgUrls(toJson(request.imgUrls()));
 if ("relic".equals(article.getType())) {
 article.setRelicRootContent(request.body());
 }
 articleMapper.updateById(article);
 return ArticleView.from(article);
 }

 public void delete(String operatorId, Long id) {
 Article article = requireActive(id);
 if (!article.getAuthorUuid().equals(operatorId) && !userService.isAdmin(operatorId)) {
 throw new BizException(ErrorCodes.FORBIDDEN, "无权删除该文章");
 }
 if (articleMapper.logicalDelete(id) ==0) {
 throw new BizException("3001", "文章不存在");
 }
 }

 public PageView<ArticleView> search(String keyword, long page, long size) {
 if (!StringUtils.hasText(keyword)) {
 throw new BizException(ErrorCodes.BAD_REQUEST, "搜索关键词不能为空");
 }
 IPage<ArticleView> result = articleMapper.search(
 new Page<>(Math.max(page,1), Math.min(Math.max(size,1),50)), keyword.trim());
 return new PageView<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
 }

 public List<SteleItem> stele(String authorUuid) {
 userService.getBrief(authorUuid);
 List<ArticleView> best = articleMapper.selectUserBestPerBuilding(authorUuid);
 return buildingService.listOpen().stream().map(building -> toStele(building, best)).toList();
 }

 public List<ArticleView> listRelics() {
 return articleMapper.selectRelicGrowing();
 }

 public List<ArticleView> collections(String userId) {
 return articleMapper.selectCollections(userId);
 }

 public void incrementAgree(Long id) {
 if (articleMapper.incrementAgree(id) ==0) {
 throw new BizException("3001", "文章不存在");
 }
 }

 public void incrementCollect(Long id) {
 if (articleMapper.incrementCollect(id) ==0) {
 throw new BizException("3001", "文章不存在");
 }
 }

 public void incrementChat(Long id) {
 if (articleMapper.incrementChat(id) ==0) {
 throw new BizException("3001", "文章不存在");
 }
 }

 public void appendRelicSegment(Long id, String segment) {
 if (articleMapper.appendSegment(id, segment) ==0) {
 throw new BizException("3001", "文章不存在");
 }
 }

 public void resetRelicContent(Long id) {
 if (articleMapper.resetRelic(id) ==0) {
 throw new BizException("3001", "文章不存在");
 }
 }

 public void markRelicMerged(Long id, String userUuid) {
 if (articleMapper.markMergedInfo(id, userUuid, LocalDateTime.now()) ==0) {
 throw new BizException("3001", "文章不存在");
 }
 }

 public void updateRelicRoot(Long id, String content) {
 Article article = requireActive(id);
 if (!"relic".equals(article.getType())) {
 throw new BizException("6001", "该文章不是残碑");
 }
 article.setRelicRootContent(content);
 article.setBody(content);
 articleMapper.updateById(article);
 }

 public List<ArticleView> userArticles(String userId) {
 return articleMapper.selectUserArticles(userId);
 }

 private SteleItem toStele(BuildingView building, List<ArticleView> articles) {
 ArticleView selected = articles.stream()
 .filter(article -> article.buildingId().equals(building.id())).findFirst().orElse(null);
 return new SteleItem(building.id(), building.name(), building.region(), selected);
 }

 private String toJson(List<String> values) {
 try {
 return objectMapper.writeValueAsString(values == null ? List.of() : values);
 } catch (JsonProcessingException exception) {
 throw new BizException(ErrorCodes.BAD_REQUEST, "图片列表格式错误");
 }
 }
}
