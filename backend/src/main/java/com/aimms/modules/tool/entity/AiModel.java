package com.aimms.modules.tool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ai_model")
public class AiModel {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer toolId;
    private String name;
    private BigDecimal unitPrice;
    private String unit;
    private String tieredPricing;
    private LocalDateTime createdAt;
}
