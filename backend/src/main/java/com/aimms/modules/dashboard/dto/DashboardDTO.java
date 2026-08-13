package com.aimms.modules.dashboard.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {

    @Data
    public static class EmployeeDashboard implements Serializable {
        private String period;
        private BigDecimal totalQuota;
        private BigDecimal usedQuota;
        private BigDecimal remainingQuota;
        private Integer usagePct;
        private List<ToolUsage> toolUsages;
    }

    @Data
    public static class ManagerDashboard implements Serializable {
        private String period;
        private Integer deptId;
        private String deptName;
        private BigDecimal monthlyBudget;
        private BigDecimal usedBudget;
        private BigDecimal remainingBudget;
        private Integer usagePct;
        private List<ToolUsage> toolUsages;
        private List<MemberUsage> memberUsages;
    }

    @Data
    public static class AdminDashboard implements Serializable {
        private String period;
        private BigDecimal companyTotalBudget;
        private BigDecimal companyUsedBudget;
        private BigDecimal companyRemainingBudget;
        private Integer companyUsagePct;
        private List<DeptUsage> deptUsages;
        private List<ToolUsage> toolUsages;
        private List<OverBudgetItem> overBudgetItems;
    }

    @Data
    public static class ToolUsage implements Serializable {
        private Integer toolId;
        private String toolName;
        private BigDecimal amountCny;
        private BigDecimal quantity;
        private Integer pct;
    }

    @Data
    public static class MemberUsage implements Serializable {
        private Integer userId;
        private String username;
        private BigDecimal amountCny;
        private BigDecimal usedQuota;
        private BigDecimal remainingQuota;
    }

    @Data
    public static class DeptUsage implements Serializable {
        private Integer deptId;
        private String deptName;
        private BigDecimal monthlyBudget;
        private BigDecimal usedBudget;
        private Integer usagePct;
    }

    @Data
    public static class OverBudgetItem implements Serializable {
        private String type;
        private Integer id;
        private String name;
        private Integer usagePct;
    }
}
