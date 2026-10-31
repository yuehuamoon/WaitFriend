package com.daoyou.search.controller;

import com.daoyou.common.Result;
import cn.dev33.satoken.annotation.SaIgnore;
import com.daoyou.search.dto.SearchResponse;
import com.daoyou.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

 private final SearchService searchService;

 @GetMapping
 @SaIgnore
 public Result<SearchResponse> search(@RequestParam String keyword,
 @RequestParam(defaultValue = "article") String type,
 @RequestParam(defaultValue = "1") long page,
 @RequestParam(defaultValue = "10") long size) {
 return Result.success(searchService.search(keyword, type, page, size));
 }
}
