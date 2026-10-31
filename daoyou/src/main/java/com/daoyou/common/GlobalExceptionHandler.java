package com.daoyou.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(BizException.class)
 public Result<Void> handleBizException(BizException exception) {
 return Result.fail(exception.getErrorCode(), exception.getMessage());
 }

 @ExceptionHandler(NotLoginException.class)
 @ResponseStatus(HttpStatus.UNAUTHORIZED)
 public Result<Void> handleNotLogin(NotLoginException exception) {
 return Result.fail(ErrorCodes.UNAUTHORIZED, "请先登录");
 }

 @ExceptionHandler(NotPermissionException.class)
 @ResponseStatus(HttpStatus.FORBIDDEN)
 public Result<Void> handleNotPermission(NotPermissionException exception) {
 return Result.fail(ErrorCodes.FORBIDDEN, "无权执行此操作");
 }

 @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
 public Result<Void> handleValidation(Exception exception) {
 String message = exception instanceof MethodArgumentNotValidException validException
 ? validException.getBindingResult().getAllErrors().get(0).getDefaultMessage()
 : ((BindException) exception).getBindingResult().getAllErrors().get(0).getDefaultMessage();
 return Result.fail(ErrorCodes.VALIDATION_ERROR, message);
 }

 @ExceptionHandler(ConstraintViolationException.class)
 public Result<Void> handleConstraintViolation(ConstraintViolationException exception) {
 return Result.fail(ErrorCodes.VALIDATION_ERROR, exception.getMessage());
 }

 @ExceptionHandler(HttpMessageNotReadableException.class)
 public Result<Void> handleUnreadable(HttpMessageNotReadableException exception) {
 return Result.fail(ErrorCodes.BAD_REQUEST, "请求参数格式错误");
 }

 @ExceptionHandler(DuplicateKeyException.class)
 public Result<Void> handleDuplicateKey(DuplicateKeyException exception) {
 return Result.fail(ErrorCodes.DUPLICATE_DATA, "数据已存在");
 }

 @ExceptionHandler(DataAccessResourceFailureException.class)
 @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
 public Result<Void> handleDataSourceUnavailable(DataAccessResourceFailureException exception) {
 log.error("Database connection unavailable: {}", exception.getMostSpecificCause().getMessage());
 return Result.fail("503", "数据库连接失败，请检查 DB_USERNAME和 DB_PASSWORD");
 }

 @ExceptionHandler(Exception.class)
 public Result<Void> handleException(Exception exception) {
 log.error("Unhandled request exception", exception);
 return Result.fail(ErrorCodes.SERVER_ERROR, "服务异常");
 }
}
