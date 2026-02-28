package com.paiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paiagent.entity.WorkflowEdge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作流边 Mapper
 */
@Mapper
public interface WorkflowEdgeMapper extends BaseMapper<WorkflowEdge> {
    
    /**
     * 根据工作流ID删除所有边
     */
    int deleteByWorkflowId(@Param("workflowId") Long workflowId);
    
    /**
     * 根据工作流ID查询所有边
     */
    List<WorkflowEdge> selectByWorkflowId(@Param("workflowId") Long workflowId);
}
