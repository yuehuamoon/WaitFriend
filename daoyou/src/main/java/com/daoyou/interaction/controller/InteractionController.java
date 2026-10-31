package com.daoyou.interaction.controller;

import com.daoyou.common.Result;
import com.daoyou.common.UserContext;
import com.daoyou.content.dto.ArticleView;
import com.daoyou.interaction.dto.ChatRequest;
import com.daoyou.interaction.dto.ChatView;
import com.daoyou.interaction.dto.FriendRequestCreate;
import com.daoyou.interaction.dto.FriendRequestView;
import com.daoyou.interaction.dto.FriendView;
import com.daoyou.interaction.service.InteractionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interaction")
@RequiredArgsConstructor
public class InteractionController {

 private final InteractionService interactionService;

 @PostMapping("/agree/{articleId}")
 public Result<String> agree(@PathVariable Long articleId) {
 return Result.success(interactionService.agree(UserContext.requireUserId(), articleId));
 }

 @PostMapping("/collect/{articleId}")
 public Result<String> collect(@PathVariable Long articleId) {
 return Result.success(interactionService.collect(UserContext.requireUserId(), articleId));
 }

 @DeleteMapping("/agree/{articleId}")
 public Result<Void> unAgree(@PathVariable Long articleId) {
 interactionService.unAgree(UserContext.requireUserId(), articleId);
 return Result.success(null);
 }

 @GetMapping("/my/collections")
 public Result<List<ArticleView>> collections() {
 return Result.success(interactionService.myCollections(UserContext.requireUserId()));
 }

 @PostMapping("/chat/{articleId}")
 public Result<ChatView> chat(@PathVariable Long articleId, @Valid @RequestBody ChatRequest request) {
 return Result.success(interactionService.chat(UserContext.requireUserId(), articleId, request));
 }

 @GetMapping("/chat/{articleId}")
 public Result<List<ChatView>> chats(@PathVariable Long articleId) {
 return Result.success(interactionService.recentChats(articleId));
 }

 @PostMapping("/friend/request")
 public Result<Long> requestFriend(@Valid @RequestBody FriendRequestCreate request) {
 return Result.success(interactionService.requestFriend(UserContext.requireUserId(), request));
 }

 @GetMapping("/friend/requests")
 public Result<List<FriendRequestView>> pendingRequests() {
 return Result.success(interactionService.pendingRequests(UserContext.requireUserId()));
 }

 @PostMapping("/friend/{requestId}/accept")
 public Result<Void> accept(@PathVariable Long requestId) {
 interactionService.acceptFriend(UserContext.requireUserId(), requestId);
 return Result.success(null);
 }

 @PostMapping("/friend/{requestId}/reject")
 public Result<Void> reject(@PathVariable Long requestId) {
 interactionService.rejectFriend(UserContext.requireUserId(), requestId);
 return Result.success(null);
 }

 @GetMapping("/friends")
 public Result<List<FriendView>> friends() {
 return Result.success(interactionService.friends(UserContext.requireUserId()));
 }

 @DeleteMapping("/friend/{friendUuid}")
 public Result<Void> deleteFriend(@PathVariable String friendUuid) {
 interactionService.deleteFriend(UserContext.requireUserId(), friendUuid);
 return Result.success(null);
 }
}
