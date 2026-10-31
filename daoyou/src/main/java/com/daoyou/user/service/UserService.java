package com.daoyou.user.service;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.daoyou.common.BizException;
import com.daoyou.common.CultivationConstants;
import com.daoyou.common.ErrorCodes;
import com.daoyou.common.RedisKeys;
import com.daoyou.common.UserContext;
import com.daoyou.user.dto.AuthView;
import com.daoyou.user.dto.InternalCultivationRequest;
import com.daoyou.user.dto.LoginRequest;
import com.daoyou.user.dto.PasswordUpdateRequest;
import com.daoyou.user.dto.ProfileUpdateRequest;
import com.daoyou.user.dto.RegisterRequest;
import com.daoyou.user.dto.UserBrief;
import com.daoyou.user.dto.UserDetail;
import com.daoyou.user.dto.UserRankItem;
import com.daoyou.user.entity.CultivationLog;
import com.daoyou.user.entity.User;
import com.daoyou.user.event.UserRegisteredEvent;
import com.daoyou.user.mapper.CultivationLogMapper;
import com.daoyou.user.mapper.UserMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

 private static final int MAX_LOGIN_FAILURES =5;
 private static final Duration LOGIN_LOCK_DURATION = Duration.ofMinutes(15);
 private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

 private final UserMapper userMapper;
 private final CultivationLogMapper cultivationLogMapper;
 private final PasswordEncoder passwordEncoder;
 private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;
 private final ApplicationEventPublisher eventPublisher;
 private final ObjectMapper objectMapper;

 @Value("${daoyou.admin-usernames:admin}")
 private String adminUsernames;

 @Transactional
 public AuthView register(RegisterRequest request) {
 if (userMapper.countByUsername(request.username()) >0) {
 throw new BizException(ErrorCodes.USERNAME_EXISTS, "用户名已存在");
 }
 User user = new User();
 user.setUuid(UUID.randomUUID().toString());
 user.setUsername(request.username());
 user.setPassword(passwordEncoder.encode(request.password()));
 user.setCultivation(BigDecimal.ZERO);
 user.setIsDeleted(0);
 try {
 userMapper.insert(user);
 } catch (DuplicateKeyException exception) {
 throw new BizException(ErrorCodes.USERNAME_EXISTS, "用户名已存在");
 }
 eventPublisher.publishEvent(new UserRegisteredEvent(user.getUuid()));
 redisTemplate.delete(RedisKeys.LEADERBOARD);
 return loginResult(user.getUuid());
 }

 public AuthView login(LoginRequest request) {
 String failKey = RedisKeys.LOGIN_FAIL + request.username();
 String fails = redisTemplate.opsForValue().get(failKey);
 if (isLocked(fails)) {
 throw new BizException(ErrorCodes.LOGIN_LOCKED, "账号已锁定");
 }
 User user = userMapper.selectByUsername(request.username());
 if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
 recordLoginFailure(failKey);
 throw new BizException(ErrorCodes.LOGIN_FAILED, "用户名或密码错误");
 }
 redisTemplate.delete(failKey);
 return loginResult(user.getUuid());
 }

 public void logout() {
 StpUtil.logout();
 }

 public UserDetail me() {
 UserDetail detail = userMapper.selectDetail(UserContext.requireUserId());
 if (detail == null) {
 throw new BizException(ErrorCodes.USER_NOT_FOUND, "用户不存在或已删除");
 }
 return detail;
 }

 public UserBrief getBrief(String uuid) {
 UserBrief brief = userMapper.selectBrief(uuid);
 if (brief == null) {
 throw new BizException(ErrorCodes.USER_NOT_FOUND, "用户不存在或已删除");
 }
 return brief;
 }

 public List<UserBrief> batch(List<String> uuids) {
 return uuids.isEmpty() ? List.of() : userMapper.selectBriefs(uuids.stream().distinct().toList());
 }

 public int updateProfile(String userId, ProfileUpdateRequest request) {
 int rows = userMapper.updateProfile(userId, request.avatar(), request.introduction(),
 request.background());
 redisTemplate.delete(RedisKeys.LEADERBOARD);
 return rows;
 }

 public void updatePassword(String userId, PasswordUpdateRequest request) {
 User user = userMapper.selectById(userId);
 if (user == null || !passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
 throw new BizException(ErrorCodes.OLD_PASSWORD_MISMATCH, "旧密码不正确");
 }
 int rows = userMapper.updatePassword(userId, user.getPassword(), passwordEncoder.encode(request.newPassword()));
 if (rows ==0) {
 throw new BizException(ErrorCodes.OLD_PASSWORD_MISMATCH, "密码已变更，请重试");
 }
 }

 public void delete(String operatorId, String targetUuid) {
 if (!operatorId.equals(targetUuid) && !isAdmin(operatorId)) {
 throw new BizException(ErrorCodes.FORBIDDEN, "无权删除该用户");
 }
 if (userMapper.logicalDelete(targetUuid) ==0) {
 throw new BizException(ErrorCodes.USER_NOT_FOUND, "用户不存在或已删除");
 }
 redisTemplate.delete(RedisKeys.USER + targetUuid);
 redisTemplate.delete(RedisKeys.LEADERBOARD);
 StpUtil.logout(targetUuid);
 }

 public void restore(String operatorId, String targetUuid) {
 requireAdmin(operatorId);
 if (userMapper.restore(targetUuid) ==0) {
 throw new BizException(ErrorCodes.USER_NOT_FOUND, "用户不存在或未删除");
 }
 redisTemplate.delete(RedisKeys.USER + targetUuid);
 redisTemplate.delete(RedisKeys.LEADERBOARD);
 }

 public List<UserRankItem> leaderboard() {
 String cached = redisTemplate.opsForValue().get(RedisKeys.LEADERBOARD);
 if (StringUtils.hasText(cached)) {
 try {
 return objectMapper.readValue(cached, new TypeReference<>() { });
 } catch (JsonProcessingException exception) {
 log.warn("Invalid leaderboard cache, rebuilding", exception);
 redisTemplate.delete(RedisKeys.LEADERBOARD);
 }
 }
 List<UserRankItem> ranking = userMapper.selectLeaderboard(100);
 try {
 redisTemplate.opsForValue().set(RedisKeys.LEADERBOARD, objectMapper.writeValueAsString(ranking),
 Duration.ofSeconds(30));
 } catch (JsonProcessingException exception) {
 log.warn("Failed to cache leaderboard", exception);
 }
 return ranking;
 }

 public List<UserBrief> search(BigDecimal min, BigDecimal max) {
 if (min != null && max != null && min.compareTo(max) >0) {
 throw new BizException(ErrorCodes.BAD_REQUEST, "最低修为不能大于最高修为");
 }
 return userMapper.searchByCultivation(min, max);
 }

 public List<UserBrief> search(String keyword) {
 return userMapper.searchByKeyword(keyword);
 }

 @Transactional(propagation = Propagation.REQUIRES_NEW)
 public int addCultivation(String userId, BigDecimal delta, String source, String refId) {
 if (!StringUtils.hasText(userId) || delta == null || delta.compareTo(BigDecimal.ZERO) <=0) {
 throw new BizException(ErrorCodes.BAD_REQUEST, "用户标识和修为增量必须有效");
 }
 String dailyKey = RedisKeys.CULTIVATION_DAILY + userId + ":" + LocalDate.now().format(DAY_FORMAT);
 Double requestedTotal = redisTemplate.opsForValue().increment(dailyKey, delta.doubleValue());
 if (requestedTotal != null && requestedTotal.compareTo(CultivationConstants.DAILY_LIMIT.doubleValue()) >0) {
 if (requestedTotal - delta.doubleValue() <=0) {
 redisTemplate.delete(dailyKey);
 } else {
 redisTemplate.opsForValue().increment(dailyKey, delta.negate().doubleValue());
 }
 throw new BizException(ErrorCodes.DAILY_CULTIVATION_LIMIT, "今日修为已达上限");
 }
 if (requestedTotal != null && requestedTotal.equals(delta.doubleValue())) {
 redisTemplate.expire(dailyKey,2, TimeUnit.DAYS);
 }
 int rows = userMapper.addCultivation(userId, delta);
 if (rows ==0) {
 throw new BizException(ErrorCodes.USER_NOT_FOUND, "用户不存在或已删除");
 }
 CultivationLog log = new CultivationLog();
 log.setUserUuid(userId);
 log.setDeltaRequested(delta);
 log.setDeltaActual(delta);
 log.setSource(StringUtils.hasText(source) ? source : CultivationConstants.INTERNAL_SOURCE);
 log.setRefId(refId);
 cultivationLogMapper.insert(log);
 redisTemplate.delete(RedisKeys.USER + userId);
 redisTemplate.delete(RedisKeys.LEADERBOARD);
 return rows;
 }

 public int addCultivationInternal(InternalCultivationRequest request) {
 return addCultivation(request.uuid(), request.delta(), CultivationConstants.INTERNAL_SOURCE, null);
 }

 public User findByCredentialsOrNull(String username, String password) {
 User user = userMapper.selectByUsername(username);
 return user != null && passwordEncoder.matches(password, user.getPassword()) ? user : null;
 }

 public boolean isAdmin(String userId) {
 UserDetail detail = userMapper.selectDetail(userId);
 if (detail == null) {
 return false;
 }
 User user = userMapper.selectById(userId);
 if (user == null) {
 return false;
 }
 return adminUsernames != null && List.of(adminUsernames.split(",")).stream()
 .map(String::trim).anyMatch(name -> name.equalsIgnoreCase(user.getUsername()));
 }

 public void requireAdmin(String userId) {
 if (!isAdmin(userId)) {
 throw new BizException(ErrorCodes.FORBIDDEN, "无权执行此操作");
 }
 }

 private AuthView loginResult(String userId) {
 StpUtil.login(userId);
 long timeoutSeconds = StpUtil.getTokenTimeout();
 long expireAt = System.currentTimeMillis() + timeoutSeconds *1000;
 return new AuthView(StpUtil.getTokenValue(), expireAt);
 }

 private void recordLoginFailure(String failKey) {
 Long failures = redisTemplate.opsForValue().increment(failKey);
 if (failures != null && failures ==1) {
 redisTemplate.expire(failKey, LOGIN_LOCK_DURATION);
 }
 }

 private boolean isLocked(String failures) {
 if (!StringUtils.hasText(failures)) {
 return false;
 }
 try {
 return Integer.parseInt(failures) >= MAX_LOGIN_FAILURES;
 } catch (NumberFormatException exception) {
 log.warn("Invalid login failure counter for key", exception);
 return false;
 }
 }
}
