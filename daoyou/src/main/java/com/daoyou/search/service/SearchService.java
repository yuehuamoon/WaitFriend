package com.daoyou.search.service;

import com.daoyou.content.service.ArticleService;
import com.daoyou.search.dto.SearchResponse;
import com.daoyou.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

 private final ArticleService articleService;
 private final UserService userService;

 public SearchResponse search(String keyword, String type, long page, long size) {
 if ("user".equals(type)) {
 return SearchResponse.users(userService.search(keyword));
 }
 return SearchResponse.articles(articleService.search(keyword, page, size));
 }
}
