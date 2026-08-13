package com.aimms.modules.tool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_tool")
public class AiTool {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String billingMode;
    private String currency;
    private String syncType;
    private String syncConfig;
    private LocalDateTime createdAt;
}
