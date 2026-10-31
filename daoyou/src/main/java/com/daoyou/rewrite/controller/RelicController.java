package com.daoyou.rewrite.controller;

import com.daoyou.common.Result;
import cn.dev33.satoken.annotation.SaIgnore;
import com.daoyou.common.UserContext;
import com.daoyou.content.dto.ArticleCreateRequest;
import com.daoyou.content.dto.ArticleDraft;
import com.daoyou.content.dto.ArticleView;
import com.daoyou.rewrite.dto.RelicDetail;
import com.daoyou.rewrite.dto.RelicContentUpdateRequest;
import com.daoyou.rewrite.dto.RelicSegmentRequest;
import com.daoyou.rewrite.service.RelicService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rewrite/relic")
@RequiredArgsConstructor
public class RelicController {

 private final RelicService relicService;

 @PostMapping
 public Result<ArticleView> create(@Valid @RequestBody ArticleCreateRequest request) {
 ArticleDraft draft = new ArticleDraft(request.buildingId(), request.title(), request.body(),
 request.imgUrls(), "relic");
 return Result.success(relicService.createRelic(UserContext.requireUserId(), draft));
 }

 @GetMapping("/list")
 @SaIgnore
 public Result<List<ArticleView>> list() {
 return Result.success(relicService.listOpen());
 }

 @GetMapping("/{articleId}")
 @SaIgnore
 public Result<RelicDetail> detail(@PathVariable Long articleId,
 @RequestParam(defaultValue = "1") long page,
 @RequestParam(defaultValue = "10") long size) {
 String viewerId = cn.dev33.satoken.stp.StpUtil.isLogin()
 ? UserContext.requireUserId() : null;
 return Result.success(relicService.detail(articleId, page, size, viewerId));
 }

 @PutMapping("/{articleId}/content")
 public Result<Void> updateRoot(@PathVariable Long articleId,
 @Valid @RequestBody RelicContentUpdateRequest request) {
 relicService.updateContent(UserContext.requireUserId(), articleId, null, request);
 return Result.success(null);
 }

 @PutMapping("/{articleId}/content/{segmentId}")
 public Result<Void> updateSegment(@PathVariable Long articleId, @PathVariable Long segmentId,
 @Valid @RequestBody RelicContentUpdateRequest request) {
 relicService.updateContent(UserContext.requireUserId(), articleId, segmentId, request);
 return Result.success(null);
 }

 @PostMapping("/{articleId}/segment")
 public Result<Long> submit(@PathVariable Long articleId,
 @Valid @RequestBody RelicSegmentRequest request) {
 return Result.success(relicService.submitSegment(UserContext.requireUserId(), articleId, request));
 }

 @PostMapping("/pool/{poolId}/like")
 public Result<Void> like(@PathVariable Long poolId) {
 boolean liked = relicService.likePool(UserContext.requireUserId(), poolId);
 return liked ? Result.success(null) : Result.fail("6005", "已点过赞，一人一文一赞");
 }

 @PostMapping("/{articleId}/destroy-vote")
 public Result<String> destroyVote(@PathVariable Long articleId) {
 return Result.success(relicService.destroyVote(UserContext.requireUserId(), articleId));
 }
}
