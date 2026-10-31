package com.daoyou.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

 private final UserContextCleanInterceptor userContextCleanInterceptor;
 private final InternalTokenInterceptor internalTokenInterceptor;

 public WebMvcConfig(UserContextCleanInterceptor userContextCleanInterceptor,
 InternalTokenInterceptor internalTokenInterceptor) {
 this.userContextCleanInterceptor = userContextCleanInterceptor;
 this.internalTokenInterceptor = internalTokenInterceptor;
 }

 @Override
 public void addInterceptors(InterceptorRegistry registry) {
 registry.addInterceptor(userContextCleanInterceptor).addPathPatterns("/**");
 registry.addInterceptor(internalTokenInterceptor).addPathPatterns("/internal/**");
 }

 @Override
 public void addCorsMappings(CorsRegistry registry) {
 registry.addMapping("/**")
 .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
 .allowedHeaders("*")
 .allowCredentials(true);
 }
}
