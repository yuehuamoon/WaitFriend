package com.daoyou.notification.service;

import com.daoyou.content.event.ContentCreatedEvent;
import com.daoyou.interaction.event.FriendRequestedEvent;
import com.daoyou.interaction.event.ReactionCreatedEvent;
import com.daoyou.rewrite.event.RelicDestroyedEvent;
import com.daoyou.rewrite.event.RelicMergedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

 private final NotificationService notificationService;
 private final ObjectMapper objectMapper;

 @Async("eventExecutor")
 @EventListener
 public void onContentCreated(ContentCreatedEvent event) {
 notificationService.create(event.authorUuid(), "content_created", "文章已发布",
 "《" + event.title() + "》已发布", payload(Map.of("articleId", event.articleId())));
 }

 @Async("eventExecutor")
 @EventListener
 public void onReactionCreated(ReactionCreatedEvent event) {
 if (event.articleAuthorUuid().equals(event.actorUuid())) {
 return;
 }
 String action = "agree".equals(event.type()) ? "赞同" : "收藏";
 notificationService.create(event.articleAuthorUuid(), "reaction_" + event.type(),
 "收到新的" + action, "有道友" + action + "了《" + event.articleTitle() + "》",
 payload(Map.of("articleId", event.articleId(), "actorUuid", event.actorUuid())));
 }

 @Async("eventExecutor")
 @EventListener
 public void onRelicMerged(RelicMergedEvent event) {
 notificationService.create(event.authorUuid(), "relic_merged", "残碑已合璧",
 "第" + event.generation() + "代第" + event.poolRound() + "轮残碑合璧",
 payload(Map.of("articleId", event.articleId())));
 }

 @Async("eventExecutor")
 @EventListener
 public void onRelicDestroyed(RelicDestroyedEvent event) {
 for (String userUuid : event.participantUuids()) {
 notificationService.create(userUuid, "relic_destroyed", "残碑已自毁",
 "参与的第" + event.generation() + "代残碑已自毁",
 payload(Map.of("articleId", event.articleId())));
 }
 }

 @Async("eventExecutor")
 @EventListener
 public void onFriendRequested(FriendRequestedEvent event) {
 notificationService.create(event.toUuid(), "friend_requested", "新的好友申请",
 "有道友向你发来好友申请", payload(Map.of("requestId", event.requestId())));
 }

 private String payload(Map<String, Object> value) {
 try {
 return objectMapper.writeValueAsString(value);
 } catch (JsonProcessingException exception) {
 log.warn("Failed to serialize notification payload", exception);
 return "{}";
 }
 }
}
