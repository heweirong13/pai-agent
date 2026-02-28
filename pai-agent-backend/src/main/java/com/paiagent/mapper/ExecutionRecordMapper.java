package com.paiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paiagent.entity.ExecutionRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 执行记录 Mapper
 */
@Mapper
public interface ExecutionRecordMapper extends BaseMapper<ExecutionRecord> {
}
