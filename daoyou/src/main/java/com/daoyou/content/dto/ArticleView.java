package com.daoyou.content.dto;

import com.daoyou.content.entity.Article;
import java.time.LocalDateTime;

public record ArticleView(Long id, String uuid, String authorUuid, Long buildingId,
 String title, String body, String relicRootContent, String imgUrls, String type, Integer agreeCount,
 Integer collectCount, Integer chatCount, Integer score, String relicStatus,
 Integer relicGeneration, Integer relicPoolRound, LocalDateTime createTime,
 LocalDateTime updateTime) {

 public static ArticleView from(Article article) {
 return new ArticleView(article.getId(), article.getUuid(), article.getAuthorUuid(),
 article.getBuildingId(), article.getTitle(), article.getBody(), article.getRelicRootContent(),
 article.getImgUrls(),
 article.getType(), article.getAgreeCount(), article.getCollectCount(), article.getChatCount(),
 article.getScore(), article.getRelicStatus(), article.getRelicGeneration(),
 article.getRelicPoolRound(), article.getCreateTime(), article.getUpdateTime());
 }
}
