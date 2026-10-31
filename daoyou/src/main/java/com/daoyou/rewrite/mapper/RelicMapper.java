package com.daoyou.rewrite.mapper;

import com.daoyou.content.dto.ArticleView;
import com.daoyou.rewrite.dto.RelicPoolView;
import com.daoyou.rewrite.dto.RelicSegmentView;
import com.daoyou.rewrite.entity.RelicPool;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RelicMapper {

 int insertPool(RelicPool pool);

 int insertParticipantIgnore(@Param("articleId") Long articleId, @Param("generation") Integer generation,
 @Param("userUuid") String userUuid);

 RelicPool selectPoolById(@Param("id") Long id);

 RelicPool selectPoolForUpdate(@Param("id") Long id);

 int incrementPoolLike(@Param("id") Long id);

 int insertPoolLikeIgnore(@Param("poolId") Long poolId, @Param("userUuid") String userUuid);

 int discardPendingSameRound(@Param("articleId") Long articleId, @Param("generation") Integer generation,
 @Param("poolRound") Integer poolRound, @Param("exceptId") Long exceptId);

 int markPoolMerged(@Param("id") Long id, @Param("status") String status, @Param("mergedAt") LocalDateTime mergedAt);

 int maxSegmentSeq(@Param("articleId") Long articleId, @Param("generation") Integer generation);

 int insertSegment(@Param("articleId") Long articleId, @Param("generation") Integer generation,
 @Param("seq") Integer seq, @Param("poolRound") Integer poolRound,
 @Param("userUuid") String userUuid, @Param("content") String content);

 int updateSegmentContent(@Param("id") Long id, @Param("userUuid") String userUuid,
 @Param("content") String content);

 RelicSegmentView selectSegmentById(@Param("id") Long id);

 List<RelicSegmentView> selectSegments(@Param("articleId") Long articleId,
 @Param("generation") Integer generation);

 List<RelicSegmentView> selectSegmentsPage(@Param("articleId") Long articleId,
 @Param("generation") Integer generation, @Param("offset") int offset,
 @Param("limit") int limit);

 long countSegments(@Param("articleId") Long articleId, @Param("generation") Integer generation);

 List<RelicPoolView> selectPendingPool(@Param("articleId") Long articleId,
 @Param("generation") Integer generation, @Param("poolRound") Integer poolRound);

 List<RelicPool> selectExpiredPending(@Param("limit") Integer limit);

 int insertDestroyVoteIgnore(@Param("articleId") Long articleId, @Param("generation") Integer generation,
 @Param("userUuid") String userUuid);

 int countVotes(@Param("articleId") Long articleId, @Param("generation") Integer generation);

 int countParticipants(@Param("articleId") Long articleId, @Param("generation") Integer generation);

 List<String> selectParticipantUuids(@Param("articleId") Long articleId, @Param("generation") Integer generation);

 int markAllPendingDestroyed(@Param("articleId") Long articleId, @Param("generation") Integer generation);
}
