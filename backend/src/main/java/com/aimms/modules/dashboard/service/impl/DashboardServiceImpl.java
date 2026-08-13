package com.aimms.modules.dashboard.service.impl;

import com.aimms.modules.budget.entity.CompanyBudget;
import com.aimms.modules.budget.entity.Department;
import com.aimms.modules.budget.service.CompanyBudgetService;
import com.aimms.modules.budget.service.DepartmentService;
import com.aimms.modules.dashboard.dto.DashboardDTO;
import com.aimms.modules.dashboard.service.DashboardService;
import com.aimms.modules.quota.entity.QuotaRecord;
import com.aimms.modules.quota.service.QuotaRecordService;
import com.aimms.modules.system.entity.SysUser;
import com.aimms.modules.system.service.SysUserService;
import com.aimms.modules.tool.entity.AiTool;
import com.aimms.modules.tool.service.AiToolService;
import com.aimms.modules.usage.entity.UsageRecord;
import com.aimms.modules.usage.service.UsageRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final QuotaRecordService quotaRecordService;
    private final UsageRecordService usageRecordService;
    private final DepartmentService departmentService;
    private final CompanyBudgetService companyBudgetService;
    private final SysUserService sysUserService;
    private final AiToolService aiToolService;

    @Override
    public DashboardDTO.EmployeeDashboard employeeDashboard(Integer userId, String period) {
        QuotaRecord remaining = quotaRecordService.getRemainingQuota(userId, period);
        List<UsageRecord> usages = usageRecordService.listByUser(userId, period);

        DashboardDTO.EmployeeDashboard dto = new DashboardDTO.EmployeeDashboard();
        dto.setPeriod(period);
        dto.setTotalQuota(remaining.getTotalAmount());
        dto.setUsedQuota(remaining.getUsedAmount());
        dto.setRemainingQuota(remaining.getRemainingAmount());
        dto.setUsagePct(pct(remaining.getUsedAmount(), remaining.getTotalAmount()));
        dto.setToolUsages(aggregateByTool(usages));
        return dto;
    }

    @Override
    public DashboardDTO.ManagerDashboard managerDashboard(Integer deptId, String period) {
        Department department = departmentService.getById(deptId);
        List<UsageRecord> usages = usageRecordService.listByDept(deptId, period);
        BigDecimal used = department.getUsedBudgetCny();

        DashboardDTO.ManagerDashboard dto = new DashboardDTO.ManagerDashboard();
        dto.setPeriod(period);
        dto.setDeptId(deptId);
        dto.setDeptName(department.getName());
        dto.setMonthlyBudget(department.getMonthlyBudgetCny());
        dto.setUsedBudget(used);
        dto.setRemainingBudget(department.getMonthlyBudgetCny().subtract(used));
        dto.setUsagePct(pct(used, department.getMonthlyBudgetCny()));
        dto.setToolUsages(aggregateByTool(usages));
        dto.setMemberUsages(aggregateByMember(deptId, period));
        return dto;
    }

    @Override
    public DashboardDTO.AdminDashboard adminDashboard(String period) {
        List<Department> departments = departmentService.list();
        List<UsageRecord> allUsages = usageRecordService.list();
        if (period != null && !period.isEmpty()) {
            allUsages = allUsages.stream()
                    .filter(u -> u.getUsageDate().toString().startsWith(period))
                    .collect(Collectors.toList());
        }

        BigDecimal companyTotal = BigDecimal.ZERO;
        BigDecimal companyUsed = BigDecimal.ZERO;
        List<DashboardDTO.DeptUsage> deptUsages = new ArrayList<>();
        for (Department dept : departments) {
            companyTotal = companyTotal.add(dept.getMonthlyBudgetCny());
            companyUsed = companyUsed.add(dept.getUsedBudgetCny());
            DashboardDTO.DeptUsage du = new DashboardDTO.DeptUsage();
            du.setDeptId(dept.getId());
            du.setDeptName(dept.getName());
            du.setMonthlyBudget(dept.getMonthlyBudgetCny());
            du.setUsedBudget(dept.getUsedBudgetCny());
            du.setUsagePct(pct(dept.getUsedBudgetCny(), dept.getMonthlyBudgetCny()));
            deptUsages.add(du);
        }

        DashboardDTO.AdminDashboard dto = new DashboardDTO.AdminDashboard();
        dto.setPeriod(period);
        CompanyBudget budget = companyBudgetService.list().stream().findFirst().orElse(null);
        dto.setCompanyTotalBudget(budget != null ? budget.getTotalBudgetCny() : companyTotal);
        dto.setCompanyUsedBudget(companyUsed);
        dto.setCompanyRemainingBudget(dto.getCompanyTotalBudget().subtract(companyUsed));
        dto.setCompanyUsagePct(pct(companyUsed, dto.getCompanyTotalBudget()));
        dto.setDeptUsages(deptUsages);
        dto.setToolUsages(aggregateByTool(allUsages));
        dto.setOverBudgetItems(buildOverBudgetItems(deptUsages));
        return dto;
    }

    private List<DashboardDTO.ToolUsage> aggregateByTool(List<UsageRecord> usages) {
        Map<Integer, List<UsageRecord>> grouped = usages.stream()
                .collect(Collectors.groupingBy(UsageRecord::getToolId));
        BigDecimal total = usages.stream().map(UsageRecord::getAmountCny)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DashboardDTO.ToolUsage> result = new ArrayList<>();
        for (Map.Entry<Integer, List<UsageRecord>> entry : grouped.entrySet()) {
            AiTool tool = aiToolService.getById(entry.getKey());
            BigDecimal amount = entry.getValue().stream().map(UsageRecord::getAmountCny)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal quantity = entry.getValue().stream().map(UsageRecord::getUsageQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            DashboardDTO.ToolUsage tu = new DashboardDTO.ToolUsage();
            tu.setToolId(entry.getKey());
            tu.setToolName(tool != null ? tool.getName() : "未知工具");
            tu.setAmountCny(amount);
            tu.setQuantity(quantity);
            tu.setPct(pct(amount, total));
            result.add(tu);
        }
        result.sort(Comparator.comparing(DashboardDTO.ToolUsage::getAmountCny).reversed());
        return result;
    }

    private List<DashboardDTO.MemberUsage> aggregateByMember(Integer deptId, String period) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeptId, deptId);
        List<SysUser> users = sysUserService.list(wrapper);
        List<DashboardDTO.MemberUsage> result = new ArrayList<>();
        for (SysUser user : users) {
            List<UsageRecord> usages = usageRecordService.listByUser(user.getId(), period);
            BigDecimal amount = usages.stream().map(UsageRecord::getAmountCny)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            QuotaRecord quota = quotaRecordService.getRemainingQuota(user.getId(), period);
            DashboardDTO.MemberUsage mu = new DashboardDTO.MemberUsage();
            mu.setUserId(user.getId());
            mu.setUsername(user.getUsername());
            mu.setAmountCny(amount);
            mu.setUsedQuota(quota.getUsedAmount());
            mu.setRemainingQuota(quota.getRemainingAmount());
            result.add(mu);
        }
        result.sort(Comparator.comparing(DashboardDTO.MemberUsage::getAmountCny).reversed());
        return result;
    }

    private List<DashboardDTO.OverBudgetItem> buildOverBudgetItems(List<DashboardDTO.DeptUsage> deptUsages) {
        List<DashboardDTO.OverBudgetItem> items = new ArrayList<>();
        for (DashboardDTO.DeptUsage dept : deptUsages) {
            if (dept.getUsagePct() >= 100) {
                DashboardDTO.OverBudgetItem item = new DashboardDTO.OverBudgetItem();
                item.setType("department");
                item.setId(dept.getDeptId());
                item.setName(dept.getDeptName());
                item.setUsagePct(dept.getUsagePct());
                items.add(item);
            }
        }
        return items;
    }

    private Integer pct(BigDecimal used, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return used.multiply(BigDecimal.valueOf(100))
                .divide(total, 0, RoundingMode.HALF_UP)
                .intValue();
    }
}
