package com.paiagent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 音频文件实体
 */
@Data
@TableName("audio_file")
public class AudioFile {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 执行记录ID
     */
    private Long executionRecordId;

    /**
     * 执行唯一标识
     */
    private String executionId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 音频时长（秒）
     */
    private Double duration;

    /**
     * 访问URL
     */
    private String fileUrl;

    /**
     * 音频格式
     */
    private String format;

    /**
     * 原始文本
     */
    @TableField(exist = false)
    private String originalText;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
