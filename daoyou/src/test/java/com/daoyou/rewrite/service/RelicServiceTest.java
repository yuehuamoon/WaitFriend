package com.daoyou.rewrite.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.daoyou.content.entity.Article;
import com.daoyou.content.service.ArticleService;
import com.daoyou.common.BizException;
import com.daoyou.rewrite.dto.RelicContentUpdateRequest;
import com.daoyou.rewrite.dto.RelicSegmentView;
import com.daoyou.rewrite.dto.RelicSegmentRequest;
import com.daoyou.rewrite.entity.RelicPool;
import com.daoyou.rewrite.mapper.RelicMapper;
import com.daoyou.user.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class RelicServiceTest {

 @Mock
 private RelicMapper relicMapper;
 @Mock
 private ArticleService articleService;
 @Mock
 private UserService userService;
 @Mock
 private ApplicationEventPublisher eventPublisher;
 @InjectMocks
 private RelicService relicService;

 @Test
 void newSegmentStaysInPoolForFiveMinutes() {
 Article article = new Article();
 article.setId(1L);
 article.setType("relic");
 article.setRelicStatus("growing");
 article.setRelicGeneration(0);
 article.setRelicPoolRound(3);
 when(articleService.requireActive(1L)).thenReturn(article);

 LocalDateTime before = LocalDateTime.now().plusMinutes(5);
 relicService.submitSegment("user-1",1L, new RelicSegmentRequest("一段续写"));
 LocalDateTime after = LocalDateTime.now().plusMinutes(5);

 ArgumentCaptor<RelicPool> captor = ArgumentCaptor.forClass(RelicPool.class);
 verify(relicMapper).insertPool(captor.capture());
 RelicPool pool = captor.getValue();
 assertEquals(3, pool.getPoolRound());
 assertEquals(0, pool.getGeneration());
 assertEquals("user-1", pool.getUserUuid());
 assertEquals(0, pool.getLikeCount());
 assertEquals("pending", pool.getStatus());
 org.junit.jupiter.api.Assertions.assertFalse(pool.getMergeDeadline().isBefore(before));
 org.junit.jupiter.api.Assertions.assertFalse(pool.getMergeDeadline().isAfter(after));
 verify(relicMapper).insertParticipantIgnore(eq(1L), eq(0), eq("user-1"));
 }

 @Test
 void sameUserCanLikePoolOnlyOnce() {
 RelicPool pool = new RelicPool();
 pool.setId(9L);
 pool.setStatus("pending");
 pool.setLikeCount(1);
 when(relicMapper.selectPoolById(9L)).thenReturn(pool);
 when(relicMapper.insertPoolLikeIgnore(9L, "user-1")).thenReturn(0);

 assertEquals(false, relicService.likePool("user-1",9L));
 verify(relicMapper, never()).incrementPoolLike(9L);
 }

 @Test
 void firstLikeInsertsRelationAndIncrementsCount() {
 RelicPool pool = new RelicPool();
 pool.setId(9L);
 pool.setStatus("pending");
 pool.setLikeCount(1);
 when(relicMapper.selectPoolById(9L)).thenReturn(pool);
 when(relicMapper.insertPoolLikeIgnore(9L, "user-1")).thenReturn(1);
 when(relicMapper.incrementPoolLike(9L)).thenReturn(1);
 when(relicMapper.selectPoolForUpdate(9L)).thenReturn(pool);

 relicService.likePool("user-1",9L);

 verify(relicMapper).insertPoolLikeIgnore(9L, "user-1");
 verify(relicMapper).incrementPoolLike(9L);
 }

 @Test
 void authorCanEditOnlyOwnSegment() {
 Article article = new Article();
 article.setId(1L);
 article.setType("relic");
 article.setRelicGeneration(0);
 when(articleService.requireActive(1L)).thenReturn(article);
 RelicSegmentView segment = new RelicSegmentView(7L,1L,0,3,0,"user-1","道友甲","旧内容",
 LocalDateTime.now());
 when(relicMapper.selectSegmentById(7L)).thenReturn(segment);
 when(relicMapper.updateSegmentContent(7L,"user-1","新内容")).thenReturn(1);

 relicService.updateContent("user-1",1L,7L,new RelicContentUpdateRequest("新内容"));

 verify(relicMapper).updateSegmentContent(7L,"user-1","新内容");
 }

 @Test
 void nonAuthorCannotEditSegment() {
 Article article = new Article();
 article.setId(1L);
 article.setType("relic");
 article.setRelicGeneration(0);
 when(articleService.requireActive(1L)).thenReturn(article);
 RelicSegmentView segment = new RelicSegmentView(7L,1L,0,3,0,"user-1","道友甲","旧内容",
 LocalDateTime.now());
 when(relicMapper.selectSegmentById(7L)).thenReturn(segment);

 assertThrows(BizException.class, () -> relicService.updateContent("user-2",1L,7L,
 new RelicContentUpdateRequest("越权修改")));
 verify(relicMapper, never()).updateSegmentContent(7L,"user-2","越权修改");
 }
}
