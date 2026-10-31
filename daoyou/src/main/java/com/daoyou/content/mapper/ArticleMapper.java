package com.daoyou.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daoyou.content.dto.ArticleView;
import com.daoyou.content.entity.Article;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ArticleMapper extends BaseMapper<Article> {

 Article selectActiveById(@Param("id") Long id);

 Article selectRelicForUpdate(@Param("id") Long id);

 IPage<ArticleView> selectBuildingPage(Page<ArticleView> page,
 @Param("buildingId") Long buildingId, @Param("tab") String tab);

 IPage<ArticleView> search(Page<ArticleView> page, @Param("keyword") String keyword);

 List<ArticleView> selectRelicGrowing();

 List<ArticleView> selectUserBestPerBuilding(@Param("authorUuid") String authorUuid);

 List<ArticleView> selectUserArticles(@Param("authorUuid") String authorUuid);

 List<ArticleView> selectCollections(@Param("userUuid") String userUuid);

 int logicalDelete(@Param("id") Long id);

 int incrementAgree(@Param("id") Long id);

 int incrementCollect(@Param("id") Long id);

 int incrementChat(@Param("id") Long id);

 int appendSegment(@Param("id") Long id, @Param("content") String content);

 int resetRelic(@Param("id") Long id);

 int markMergedInfo(@Param("id") Long id, @Param("userUuid") String userUuid,
 @Param("mergedAt") LocalDateTime mergedAt);
}
