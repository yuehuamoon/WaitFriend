package com.daoyou.config;

import cn.dev33.satoken.stp.StpUtil;
import com.daoyou.common.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class UserContextFilter extends OncePerRequestFilter {

 public static void bindCurrentUser() {
 if (StpUtil.isLogin()) {
 UserContext.set(StpUtil.getLoginIdAsString());
 }
 }

 @Override
 protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
 FilterChain filterChain) throws ServletException, IOException {
 try {
 bindCurrentUser();
 filterChain.doFilter(request, response);
 } finally {
 UserContext.clear();
 }
 }
}
