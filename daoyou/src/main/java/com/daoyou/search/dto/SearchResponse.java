package com.daoyou.search.dto;

import com.daoyou.content.dto.ArticleView;
import com.daoyou.content.dto.PageView;
import com.daoyou.user.dto.UserBrief;
import java.util.List;

public record SearchResponse(String type, PageView<ArticleView> articles, List<UserBrief> users) {
 public static SearchResponse articles(PageView<ArticleView> page) {
 return new SearchResponse("article", page, List.of());
 }

 public static SearchResponse users(List<UserBrief> users) {
 return new SearchResponse("user", null, users);
 }
}
