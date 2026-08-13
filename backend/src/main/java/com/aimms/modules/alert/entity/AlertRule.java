package com.aimms.modules.alert.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("alert_rule")
public class AlertRule {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String targetType;
    private Integer targetId;
    private Integer thresholdPct;
    private String notifyRoles;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
