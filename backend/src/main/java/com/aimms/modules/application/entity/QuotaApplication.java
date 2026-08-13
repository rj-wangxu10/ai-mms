package com.aimms.modules.application.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("quota_application")
public class QuotaApplication {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer applicantId;
    private Integer deptId;
    private String type;
    private BigDecimal amount;
    private String reason;
    private String status;
    private Integer approverId;
    private String approveComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
