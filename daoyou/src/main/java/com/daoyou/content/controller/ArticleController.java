package com.daoyou.content.controller;

import com.daoyou.common.Result;
import cn.dev33.satoken.annotation.SaIgnore;
import com.daoyou.common.UserContext;
import com.daoyou.content.dto.ArticleCreateRequest;
import com.daoyou.content.dto.ArticleCreated;
import com.daoyou.content.dto.ArticleDraft;
import com.daoyou.content.dto.ArticleUpdateRequest;
import com.daoyou.content.dto.ArticleView;
import com.daoyou.content.dto.PageView;
import com.daoyou.content.dto.SteleItem;
import com.daoyou.content.service.ArticleService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ArticleController {

 private final ArticleService articleService;

 @PostMapping("/articles")
 public Result<ArticleCreated> create(@Valid @RequestBody ArticleCreateRequest request) {
 ArticleDraft draft = new ArticleDraft(request.buildingId(), request.title(), request.body(),
 request.imgUrls(), request.type());
 return Result.success(articleService.create(UserContext.requireUserId(), draft));
 }

 @GetMapping("/articles/{id}")
 @SaIgnore
 public Result<ArticleView> get(@PathVariable Long id) {
 return Result.success(articleService.get(id));
 }

 @GetMapping("/articles")
 @SaIgnore
 public Result<PageView<ArticleView>> list(@RequestParam Long buildingId,
 @RequestParam(defaultValue = "hot") String tab,
 @RequestParam(defaultValue = "1") long page,
 @RequestParam(defaultValue = "10") long size) {
 return Result.success(articleService.list(buildingId, tab, page, size));
 }

 @PutMapping("/articles/{id}")
 public Result<ArticleView> update(@PathVariable Long id, @Valid @RequestBody ArticleUpdateRequest request) {
 return Result.success(articleService.update(UserContext.requireUserId(), id, request));
 }

 @DeleteMapping("/articles/{id}")
 public Result<Void> delete(@PathVariable Long id) {
 articleService.delete(UserContext.requireUserId(), id);
 return Result.success(null);
 }

 @GetMapping("/search")
 @SaIgnore
 public Result<PageView<ArticleView>> search(@RequestParam String keyword,
 @RequestParam(defaultValue = "1") long page, @RequestParam(defaultValue = "10") long size) {
 return Result.success(articleService.search(keyword, page, size));
 }

 @GetMapping("/user/{authorUuid}/stele")
 @SaIgnore
 public Result<List<SteleItem>> stele(@PathVariable String authorUuid) {
 return Result.success(articleService.stele(authorUuid));
 }
 @GetMapping("/user/{authorUuid}/articles")
 @SaIgnore
 public Result<List<ArticleView>> userArticles(@PathVariable String authorUuid) {
 return Result.success(articleService.userArticles(authorUuid));
 }
}
