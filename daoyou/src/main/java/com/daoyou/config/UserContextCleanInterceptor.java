package com.daoyou.config;

import com.daoyou.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserContextCleanInterceptor implements HandlerInterceptor {

 @Override
 public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
 Object handler, Exception exception) {
 UserContext.clear();
 }
}
