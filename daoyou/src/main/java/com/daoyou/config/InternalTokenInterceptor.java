package com.daoyou.config;

import com.daoyou.common.BizException;
import com.daoyou.common.ErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InternalTokenInterceptor implements HandlerInterceptor {

 @Value("${daoyou.internal-token}")
 private String internalToken;

 @Override
 public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
 if (!internalToken.equals(request.getHeader("X-Internal-Token"))) {
 throw new BizException(ErrorCodes.FORBIDDEN, "内部令牌无效");
 }
 return true;
 }
}
