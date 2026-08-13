package com.aimms.modules.alert.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("alert_log")
public class AlertLog {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer ruleId;
    private String targetType;
    private Integer targetId;
    private Integer actualPct;
    private String message;
    private LocalDateTime createdAt;
}
