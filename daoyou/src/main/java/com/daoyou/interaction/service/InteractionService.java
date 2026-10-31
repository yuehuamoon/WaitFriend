package com.daoyou.interaction.service;

import com.daoyou.common.BizException;
import com.daoyou.common.CultivationConstants;
import com.daoyou.common.ErrorCodes;
import com.daoyou.common.RedisKeys;
import com.daoyou.content.dto.ArticleView;
import com.daoyou.content.entity.Article;
import com.daoyou.content.service.ArticleService;
import com.daoyou.interaction.dto.ChatRequest;
import com.daoyou.interaction.dto.ChatView;
import com.daoyou.interaction.dto.FriendRequestCreate;
import com.daoyou.interaction.dto.FriendRequestView;
import com.daoyou.interaction.dto.FriendView;
import com.daoyou.interaction.entity.Chat;
import com.daoyou.interaction.entity.FriendRequest;
import com.daoyou.interaction.event.ChatCreatedEvent;
import com.daoyou.interaction.event.FriendRequestedEvent;
import com.daoyou.interaction.event.ReactionCreatedEvent;
import com.daoyou.interaction.mapper.InteractionMapper;
import com.daoyou.user.dto.UserBrief;
import com.daoyou.user.service.UserService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class InteractionService {

 private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;
 private final InteractionMapper interactionMapper;
 private final ArticleService articleService;
 private final UserService userService;
 private final StringRedisTemplate redisTemplate;
 private final ApplicationEventPublisher eventPublisher;

 @Transactional
 public String agree(String userId, Long articleId) {
 Article article = articleService.requireActive(articleId);
 int inserted = interactionMapper.insertReaction(articleId, userId, "agree");
 if (inserted ==0) {
 return "已赞同";
 }
 articleService.incrementAgree(articleId);
 awardIgnoreFailure(article.getAuthorUuid(), java.math.BigDecimal.ONE, "agree", articleId.toString());
 eventPublisher.publishEvent(new ReactionCreatedEvent(articleId, article.getTitle(),
 article.getAuthorUuid(), userId, "agree"));
 return "赞同成功";
 }

 @Transactional
 public String collect(String userId, Long articleId) {
 Article article = articleService.requireActive(articleId);
 int inserted = interactionMapper.insertReaction(articleId, userId, "collect");
 if (inserted ==0) {
 return "已收藏";
 }
 articleService.incrementCollect(articleId);
 awardIgnoreFailure(article.getAuthorUuid(), new java.math.BigDecimal("3"), "collect", articleId.toString());
 eventPublisher.publishEvent(new ReactionCreatedEvent(articleId, article.getTitle(),
 article.getAuthorUuid(), userId, "collect"));
 return "收藏成功";
 }

 @Transactional
 public void unAgree(String userId, Long articleId) {
 articleService.requireActive(articleId);
 interactionMapper.deleteReaction(articleId, userId, "agree");
 }

 public List<ArticleView> myCollections(String userId) {
 return articleService.collections(userId);
 }

 @Transactional
 public ChatView chat(String userId, Long articleId, ChatRequest request) {
 Article article = articleService.requireActive(articleId);
 String cooldownKey = RedisKeys.CHAT_COOLDOWN + userId;
 Boolean acquired = redisTemplate.opsForValue().setIfAbsent(cooldownKey, "1", Duration.ofSeconds(30));
 if (!Boolean.TRUE.equals(acquired)) {
 throw new BizException(ErrorCodes.CHAT_COOLDOWN, "论道太急，请稍后再言");
 }
 try {
 awardChatCultivation(userId);
 Chat chat = new Chat();
 chat.setArticleId(articleId);
 chat.setUserUuid(userId);
 chat.setContent(request.content());
 interactionMapper.insertChat(chat);
 articleService.incrementChat(articleId);
 eventPublisher.publishEvent(new ChatCreatedEvent(articleId, userId));
 UserBrief user = userService.getBrief(userId);
 return ChatView.from(chat, user);
 } catch (RuntimeException exception) {
 redisTemplate.delete(cooldownKey);
 throw exception;
 }
 }

 public List<ChatView> recentChats(Long articleId) {
 articleService.requireActive(articleId);
 List<Chat> chats = interactionMapper.selectRecentChats(articleId,50);
 Map<String, UserBrief> users = userService.batch(chats.stream().map(Chat::getUserUuid).distinct().toList())
 .stream().collect(Collectors.toMap(UserBrief::uuid, Function.identity()));
 List<ChatView> result = new ArrayList<>(chats.stream().map(chat -> ChatView.from(chat, users.get(chat.getUserUuid()))).toList());
 Collections.reverse(result);
 return result;
 }

 @Transactional
 public Long requestFriend(String userId, FriendRequestCreate request) {
 if (userId.equals(request.toUuid())) {
 throw new BizException(ErrorCodes.FRIEND_REQUEST_INVALID, "不能添加自己为好友");
 }
 userService.getBrief(request.toUuid());
 if (interactionMapper.countFriendship(userId, request.toUuid()) >0) {
 throw new BizException(ErrorCodes.FRIEND_REQUEST_INVALID, "你们已经是好友");
 }
 if (interactionMapper.selectPendingRequest(userId, request.toUuid()) != null) {
 throw new BizException(ErrorCodes.FRIEND_REQUEST_INVALID, "好友申请已发送");
 }
 FriendRequest entity = new FriendRequest();
 entity.setFromUuid(userId);
 entity.setToUuid(request.toUuid());
 entity.setMessage(request.message());
 interactionMapper.insertFriendRequest(entity);
 eventPublisher.publishEvent(new FriendRequestedEvent(entity.getId(), userId, request.toUuid()));
 return entity.getId();
 }

 @Transactional
 public void acceptFriend(String userId, Long requestId) {
 FriendRequest request = requireIncomingRequest(userId, requestId);
 if (interactionMapper.updateRequestStatus(requestId, "accepted", userId) ==0) {
 throw new BizException(ErrorCodes.FRIEND_REQUEST_INVALID, "申请已处理");
 }
 interactionMapper.insertFriend(request.getFromUuid(), request.getToUuid());
 interactionMapper.insertFriend(request.getToUuid(), request.getFromUuid());
 }

 @Transactional
 public void rejectFriend(String userId, Long requestId) {
 requireIncomingRequest(userId, requestId);
 if (interactionMapper.updateRequestStatus(requestId, "rejected", userId) ==0) {
 throw new BizException(ErrorCodes.FRIEND_REQUEST_INVALID, "申请已处理");
 }
 }

 public List<FriendRequestView> pendingRequests(String userId) {
 return interactionMapper.selectPendingIncoming(userId).stream()
 .map(request -> FriendRequestView.from(request, userService.getBrief(request.getFromUuid()).username()))
 .toList();
 }

 public List<FriendView> friends(String userId) {
 return interactionMapper.selectFriends(userId);
 }

 @Transactional
 public void deleteFriend(String userId, String friendUuid) {
 if (interactionMapper.deleteFriends(userId, friendUuid) ==0) {
 throw new BizException(ErrorCodes.FRIEND_REQUEST_INVALID, "好友关系不存在");
 }
 }

 private FriendRequest requireIncomingRequest(String userId, Long requestId) {
 FriendRequest request = interactionMapper.selectRequestById(requestId);
 if (request == null || !request.getToUuid().equals(userId) || !"pending".equals(request.getStatus())) {
 throw new BizException(ErrorCodes.FRIEND_REQUEST_INVALID, "好友申请不存在");
 }
 return request;
 }

 private void awardChatCultivation(String userId) {
 String day = LocalDate.now().format(DAY_FORMAT);
 String key = RedisKeys.CULTIVATION_CHAT_DAILY + userId + ":" + day;
 Double total = redisTemplate.opsForValue().increment(key, CultivationConstants.CHAT_REWARD.doubleValue());
 if (total != null && total.compareTo(CultivationConstants.CHAT_DAILY_LIMIT.doubleValue()) >0) {
 redisTemplate.opsForValue().increment(key, CultivationConstants.CHAT_REWARD.negate().doubleValue());
 throw new BizException(ErrorCodes.DAILY_CULTIVATION_LIMIT, "今日论道修为已达上限");
 }
 if (total != null && Double.compare(total, CultivationConstants.CHAT_REWARD.doubleValue()) ==0) {
 redisTemplate.expire(key, Duration.ofDays(2));
 }
 userService.addCultivation(userId, CultivationConstants.CHAT_REWARD, "chat", null);
 }

 private void awardIgnoreFailure(String userId, java.math.BigDecimal amount, String source, String refId) {
 try {
 userService.addCultivation(userId, amount, source, refId);
 } catch (RuntimeException ignored) {
 }
 }
}
