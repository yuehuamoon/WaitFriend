package com.daoyou.rewrite.service;

import com.daoyou.common.BizException;
import com.daoyou.common.ErrorCodes;
import com.daoyou.content.dto.ArticleDraft;
import com.daoyou.content.dto.ArticleView;
import com.daoyou.content.entity.Article;
import com.daoyou.content.service.ArticleService;
import com.daoyou.rewrite.dto.RelicDetail;
import com.daoyou.rewrite.dto.RelicContentItem;
import com.daoyou.rewrite.dto.RelicContentUpdateRequest;
import com.daoyou.rewrite.dto.RelicSegmentRequest;
import com.daoyou.rewrite.entity.RelicPool;
import com.daoyou.rewrite.event.RelicDestroyedEvent;
import com.daoyou.rewrite.event.RelicMergedEvent;
import com.daoyou.rewrite.mapper.RelicMapper;
import com.daoyou.user.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelicService {

 private static final int EARLY_MERGE_LIKES =10;
 private static final int DESTROY_COOLDOWN_HOURS =24;
 private static final int DEFAULT_SEGMENT_PAGE_SIZE =10;
 private static final int MAX_SEGMENT_PAGE_SIZE =50;
 private final RelicMapper relicMapper;
 private final ArticleService articleService;
 private final UserService userService;
 private final ApplicationEventPublisher eventPublisher;

 public ArticleView createRelic(String userId, ArticleDraft draft) {
 ArticleDraft relicDraft = new ArticleDraft(draft.buildingId(), draft.title(), draft.body(),
 draft.imgUrls(), "relic");
 return articleService.get(articleService.create(userId, relicDraft).id());
 }

 public List<ArticleView> listOpen() {
 return articleService.listRelics();
 }

 public RelicDetail detail(Long articleId, long requestedPage, long requestedSize, String viewerId) {
 ArticleView article = articleService.get(articleId);
 if (!"relic".equals(article.type())) {
 throw new BizException("6001", "该文章不是残碑");
 }
 long pageSize = Math.min(Math.max(requestedSize,1), MAX_SEGMENT_PAGE_SIZE);
 long total = relicMapper.countSegments(articleId, article.relicGeneration()) +1;
 long totalPages = Math.max(1, (total + pageSize -1) / pageSize);
 long page = Math.min(Math.max(requestedPage,1), totalPages);
 int offset = Math.toIntExact((page -1) * pageSize);
 int segmentLimit = page ==1 ? Math.toIntExact(pageSize) -1 : Math.toIntExact(pageSize);
 List<com.daoyou.rewrite.dto.RelicSegmentView> segments = relicMapper.selectSegmentsPage(articleId,
 article.relicGeneration(), page ==1 ?0 : Math.max(0, offset -1), segmentLimit);
 List<RelicContentItem> content = new java.util.ArrayList<>();
 if (page ==1) {
 content.add(new RelicContentItem("root", null,0, article.authorUuid(), null,
 article.relicRootContent() == null ? article.body() : article.relicRootContent(),
 article.authorUuid().equals(viewerId)));
 }
 content.addAll(segments.stream().map(segment -> new RelicContentItem("segment", segment.id(),
 segment.seq(), segment.userUuid(), segment.username(), segment.content(),
 segment.userUuid().equals(viewerId))).toList());
 List<com.daoyou.rewrite.dto.RelicPoolView> pool = relicMapper.selectPendingPool(articleId,
 article.relicGeneration(), article.relicPoolRound());
 return new RelicDetail(article, segments, content, pool, total, page, pageSize, total);
 }

 @Transactional
 public void updateContent(String userId, Long articleId, Long segmentId,
 RelicContentUpdateRequest request) {
 Article article = articleService.requireActive(articleId);
 if (!"relic".equals(article.getType())) {
 throw new BizException("6001", "该文章不是残碑");
 }
 if (segmentId == null) {
 if (!article.getAuthorUuid().equals(userId)) {
 throw new BizException(ErrorCodes.FORBIDDEN, "只能修改自己发送的残碑内容");
 }
 articleService.updateRelicRoot(articleId, request.content());
 return;
 }
 var segment = relicMapper.selectSegmentById(segmentId);
 if (segment == null || !articleId.equals(segment.articleId())
 || !segment.generation().equals(article.getRelicGeneration())) {
 throw new BizException("6001", "残碑段落不存在");
 }
 if (!userId.equals(segment.userUuid())) {
 throw new BizException(ErrorCodes.FORBIDDEN, "只能修改自己发送的残碑内容");
 }
 if (relicMapper.updateSegmentContent(segmentId, userId, request.content()) ==0) {
 throw new BizException(ErrorCodes.FORBIDDEN, "只能修改自己发送的残碑内容");
 }
 }

 @Transactional
 public Long submitSegment(String userId, Long articleId, RelicSegmentRequest request) {
 Article article = requireGrowingRelic(articleId);
 RelicPool pool = new RelicPool();
 pool.setArticleId(articleId);
 pool.setGeneration(article.getRelicGeneration());
 pool.setPoolRound(article.getRelicPoolRound());
 pool.setUserUuid(userId);
 pool.setContent(request.content());
 pool.setLikeCount(0);
 pool.setStatus("pending");
 pool.setMergeDeadline(LocalDateTime.now().plusMinutes(5));
 relicMapper.insertPool(pool);
 relicMapper.insertParticipantIgnore(articleId, article.getRelicGeneration(), userId);
 return pool.getId();
 }

 @Transactional
 public boolean likePool(String userId, Long poolId) {
 RelicPool pool = relicMapper.selectPoolById(poolId);
 if (pool == null || !"pending".equals(pool.getStatus())) {
 throw new BizException("6002", "投稿已不在池中");
 }
 if (relicMapper.insertPoolLikeIgnore(poolId, userId) ==0) {
 return false;
 }
 if (relicMapper.incrementPoolLike(poolId) ==0) {
 throw new BizException("6002", "投稿已不在池中");
 }
 RelicPool current = relicMapper.selectPoolForUpdate(poolId);
 if (current != null && current.getLikeCount() >= EARLY_MERGE_LIKES && "pending".equals(current.getStatus())) {
 merge(current);
 }
 return true;
 }

 @Transactional
 public String destroyVote(String userId, Long articleId) {
 Article article = articleService.requireActive(articleId);
 requireGrowingRelic(article);
 if (article.getRelicLastDestroyedAt() != null
 && article.getRelicLastDestroyedAt().plusHours(DESTROY_COOLDOWN_HOURS).isAfter(LocalDateTime.now())) {
 throw new BizException("6003", "残碑尚在自毁冷却期");
 }
 if (!relicMapper.selectParticipantUuids(articleId, article.getRelicGeneration()).contains(userId)) {
 throw new BizException("6004", "当前代次未参与残碑");
 }
 relicMapper.insertDestroyVoteIgnore(articleId, article.getRelicGeneration(), userId);
 int total = relicMapper.countParticipants(articleId, article.getRelicGeneration());
 int votes = relicMapper.countVotes(articleId, article.getRelicGeneration());
 if (total <2 || votes *2 < total) {
 return "自毁票已记录";
 }
 List<String> participants = relicMapper.selectParticipantUuids(articleId, article.getRelicGeneration());
 relicMapper.markAllPendingDestroyed(articleId, article.getRelicGeneration());
 articleService.resetRelicContent(articleId);
 eventPublisher.publishEvent(new RelicDestroyedEvent(articleId, article.getRelicGeneration(), participants));
 return "残碑已自毁";
 }

 @Scheduled(cron = "0 * * * * ?")
 @Transactional
 public void mergeExpiredPools() {
 List<RelicPool> expired = relicMapper.selectExpiredPending(50);
 for (RelicPool pool : expired) {
 try {
 RelicPool locked = relicMapper.selectPoolForUpdate(pool.getId());
 if (locked != null && "pending".equals(locked.getStatus())) {
 merge(locked);
 }
 } catch (RuntimeException exception) {
 log.error("Failed to merge relic pool {}", pool.getId(), exception);
 }
 }
 }

 @Transactional
 public void merge(RelicPool pool) {
 RelicPool locked = relicMapper.selectPoolForUpdate(pool.getId());
 if (locked == null || !"pending".equals(locked.getStatus())) {
 return;
 }
 Article article = articleService.requireActive(locked.getArticleId());
 if (!"relic".equals(article.getType()) || !"growing".equals(article.getRelicStatus())) {
 return;
 }
 int seq = relicMapper.maxSegmentSeq(locked.getArticleId(), locked.getGeneration()) +1;
 relicMapper.markPoolMerged(locked.getId(), "merged", LocalDateTime.now());
 relicMapper.insertSegment(locked.getArticleId(), locked.getGeneration(), seq,
 locked.getPoolRound(), locked.getUserUuid(), locked.getContent());
 relicMapper.discardPendingSameRound(locked.getArticleId(), locked.getGeneration(),
 locked.getPoolRound(), locked.getId());
 articleService.appendRelicSegment(locked.getArticleId(), locked.getContent());
 articleService.markRelicMerged(locked.getArticleId(), locked.getUserUuid());
 awardIgnoreFailure(locked.getUserUuid(), new BigDecimal("3"), "relic_merge", locked.getArticleId().toString());
 eventPublisher.publishEvent(new RelicMergedEvent(locked.getArticleId(), article.getAuthorUuid(),
 locked.getGeneration(), locked.getPoolRound()));
 }

 private Article requireGrowingRelic(Long articleId) {
 Article article = articleService.requireActive(articleId);
 requireGrowingRelic(article);
 return article;
 }

 private void requireGrowingRelic(Article article) {
 if (!"relic".equals(article.getType()) || !"growing".equals(article.getRelicStatus())) {
 throw new BizException("6001", "残碑未开放续写");
 }
 }

 private void awardIgnoreFailure(String userId, BigDecimal delta, String source, String refId) {
 try {
 userService.addCultivation(userId, delta, source, refId);
 } catch (RuntimeException exception) {
 log.warn("Relic cultivation award failed", exception);
 }
 }
}
