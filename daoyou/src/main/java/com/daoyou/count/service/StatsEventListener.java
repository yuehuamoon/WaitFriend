package com.daoyou.count.service;

import com.daoyou.content.event.ContentCreatedEvent;
import com.daoyou.interaction.event.ChatCreatedEvent;
import com.daoyou.interaction.event.ReactionCreatedEvent;
import com.daoyou.rewrite.event.RelicMergedEvent;
import com.daoyou.user.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatsEventListener {

 private final StatsService statsService;

 @Async("eventExecutor")
 @EventListener
 public void onUserRegistered(UserRegisteredEvent event) {
 statsService.increment("user_register",1);
 }

 @Async("eventExecutor")
 @EventListener
 public void onContentCreated(ContentCreatedEvent event) {
 statsService.increment("article_create",1);
 }

 @Async("eventExecutor")
 @EventListener
 public void onReaction(ReactionCreatedEvent event) {
 if ("agree".equals(event.type())) {
 statsService.increment("agree",1);
 }
 }

 @Async("eventExecutor")
 @EventListener
 public void onChat(ChatCreatedEvent event) {
 statsService.increment("chat",1);
 }

 @Async("eventExecutor")
 @EventListener
 public void onRelicMerged(RelicMergedEvent event) {
 statsService.increment("relic_merge",1);
 }
}
