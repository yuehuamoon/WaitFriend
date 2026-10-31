package com.daoyou.interaction.mapper;

import com.daoyou.interaction.dto.FriendView;
import com.daoyou.interaction.entity.Chat;
import com.daoyou.interaction.entity.FriendRequest;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface InteractionMapper {

 int insertReaction(@Param("articleId") Long articleId, @Param("userUuid") String userUuid,
 @Param("type") String type);

 int deleteReaction(@Param("articleId") Long articleId, @Param("userUuid") String userUuid,
 @Param("type") String type);

 int insertChat(Chat chat);

 List<Chat> selectRecentChats(@Param("articleId") Long articleId, @Param("limit") int limit);

 FriendRequest selectPendingRequest(@Param("fromUuid") String fromUuid,
 @Param("toUuid") String toUuid);

 FriendRequest selectRequestById(@Param("id") Long id);

 int insertFriendRequest(FriendRequest request);

 int updateRequestStatus(@Param("id") Long id, @Param("status") String status,
 @Param("toUuid") String toUuid);

 int insertFriend(@Param("userUuid") String userUuid, @Param("friendUuid") String friendUuid);

 int deleteFriends(@Param("userUuid") String userUuid, @Param("friendUuid") String friendUuid);

 int countFriendship(@Param("userUuid") String userUuid, @Param("friendUuid") String friendUuid);

 List<FriendView> selectFriends(@Param("userUuid") String userUuid);

 List<FriendRequest> selectPendingIncoming(@Param("toUuid") String toUuid);
}
