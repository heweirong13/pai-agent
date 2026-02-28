package com.paiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paiagent.entity.NodeExecutionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 节点执行日志 Mapper
 */
@Mapper
public interface NodeExecutionLogMapper extends BaseMapper<NodeExecutionLog> {
    
    /**
     * 根据执行ID查询所有日志
     */
    List<NodeExecutionLog> selectByExecutionId(@Param("executionId") String executionId);
}
