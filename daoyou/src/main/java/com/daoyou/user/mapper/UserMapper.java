package com.daoyou.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daoyou.user.dto.UserBrief;
import com.daoyou.user.dto.UserDetail;
import com.daoyou.user.dto.UserRankItem;
import com.daoyou.user.entity.User;
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {

 User selectByUsername(@Param("username") String username);

 UserDetail selectDetail(@Param("uuid") String uuid);

 UserBrief selectBrief(@Param("uuid") String uuid);

 List<UserBrief> selectBriefs(@Param("uuids") List<String> uuids);

 List<UserRankItem> selectLeaderboard(@Param("limit") int limit);

 List<UserBrief> searchByCultivation(@Param("minCultivation") BigDecimal minCultivation,
 @Param("maxCultivation") BigDecimal maxCultivation);

 int updateProfile(@Param("uuid") String uuid, @Param("avatar") String avatar,
 @Param("introduction") String introduction,
 @Param("background") String background);

 int updatePassword(@Param("uuid") String uuid, @Param("oldHash") String oldHash,
 @Param("newHash") String newHash);

 int addCultivation(@Param("uuid") String uuid, @Param("delta") BigDecimal delta);

 int logicalDelete(@Param("uuid") String uuid);

 int restore(@Param("uuid") String uuid);

 int countByUsername(@Param("username") String username);

 List<UserBrief> searchByKeyword(@Param("keyword") String keyword);
}
