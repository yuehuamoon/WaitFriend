package com.daoyou.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

 private boolean success = false;
 private String message;
 private String errorCode;
 private T data;

 public static <T> Result<T> success(T data) {
 return new Result<>(true, "操作成功", null, data);
 }

 public static <T> Result<T> fail(String errorCode, String message) {
 return new Result<>(false, message, errorCode, null);
 }

 public static <T> Result<T> fail(String message) {
 return fail(ErrorCodes.SERVER_ERROR, message);
 }
}
