package com.aimms.modules.usage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("usage_record")
public class UsageRecord {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer deptId;
    private Integer toolId;
    private Integer modelId;
    private LocalDate usageDate;
    private BigDecimal usageQuantity;
    private BigDecimal originalAmount;
    private String originalCurrency;
    private BigDecimal amountCny;
    private String source;
    private String remark;
    private String rawData;
    private LocalDateTime createdAt;
}
