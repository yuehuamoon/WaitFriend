package com.daoyou.common;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

 private final String errorCode;

 public BizException(String errorCode, String message) {
 super(message);
 this.errorCode = errorCode;
 }
}
