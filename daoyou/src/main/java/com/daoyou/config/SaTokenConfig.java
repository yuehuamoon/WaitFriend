package com.daoyou.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

 private final UserContextFilter userContextFilter;

 public SaTokenConfig(UserContextFilter userContextFilter) {
 this.userContextFilter = userContextFilter;
 }

 @Override
 public void addInterceptors(InterceptorRegistry registry) {
 registry.addInterceptor(new cn.dev33.satoken.interceptor.SaInterceptor(handle -> {
 StpUtil.checkLogin();
 UserContextFilter.bindCurrentUser();
 }))
 .addPathPatterns("/**")
 .excludePathPatterns("/user/login", "/user/register", "/internal/**", "/doc.html",
 "/swagger-resources/**", "/v3/api-docs/**", "/actuator/**", "/error",
 "/webjars/**", "/favicon.ico");
 }
}
