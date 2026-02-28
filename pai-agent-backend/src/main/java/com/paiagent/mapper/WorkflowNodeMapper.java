package com.paiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paiagent.entity.WorkflowNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作流节点 Mapper
 */
@Mapper
public interface WorkflowNodeMapper extends BaseMapper<WorkflowNode> {
    
    /**
     * 根据工作流ID删除所有节点
     */
    int deleteByWorkflowId(@Param("workflowId") Long workflowId);
    
    /**
     * 根据工作流ID查询所有节点
     */
    List<WorkflowNode> selectByWorkflowId(@Param("workflowId") Long workflowId);
}
