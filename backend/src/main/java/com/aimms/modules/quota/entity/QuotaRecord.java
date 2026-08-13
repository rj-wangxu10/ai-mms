package com.aimms.modules.quota.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("quota_record")
public class QuotaRecord {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer deptId;
    private String period;
    private String quotaType;
    private BigDecimal totalAmount;
    private BigDecimal usedAmount;
    private BigDecimal remainingAmount;
    private Integer sourceApplicationId;
    private LocalDateTime createdAt;
}
