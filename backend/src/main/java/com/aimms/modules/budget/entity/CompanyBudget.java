package com.aimms.modules.budget.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("company_budget")
public class CompanyBudget {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer fiscalYear;
    private BigDecimal totalBudgetCny;
    private BigDecimal totalBudgetUsd;
    private BigDecimal exchangeRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
