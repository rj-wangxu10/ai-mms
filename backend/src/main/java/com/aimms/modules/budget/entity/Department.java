package com.aimms.modules.budget.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("department")
public class Department {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer companyId;
    private String name;
    private BigDecimal monthlyBudgetCny;
    private BigDecimal usedBudgetCny;
    private Integer managerId;
    private LocalDateTime createdAt;
}
