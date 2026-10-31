package com.daoyou.rewrite.dto;

import com.daoyou.content.dto.ArticleView;
import java.util.List;

public record RelicDetail(ArticleView article, List<RelicSegmentView> segments,
 List<RelicContentItem> content, List<RelicPoolView> pool, long segmentTotal,
 long segmentPage, long segmentPageSize, long contentTotal) {
}
