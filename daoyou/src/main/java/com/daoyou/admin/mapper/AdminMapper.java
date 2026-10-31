package com.daoyou.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daoyou.admin.entity.AdminOperationLog;
import org.apache.ibatis.annotations.Param;

public interface AdminMapper {
 int insertLog(AdminOperationLog log);
 IPage<AdminOperationLog> selectLogs(Page<AdminOperationLog> page);
}
