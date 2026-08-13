package com.aimms.modules.alert.service.impl;

import com.aimms.modules.alert.entity.AlertLog;
import com.aimms.modules.alert.entity.AlertRule;
import com.aimms.modules.alert.mapper.AlertLogMapper;
import com.aimms.modules.alert.mapper.AlertRuleMapper;
import com.aimms.modules.alert.service.AlertService;
import com.aimms.modules.budget.entity.CompanyBudget;
import com.aimms.modules.budget.entity.Department;
import com.aimms.modules.budget.service.CompanyBudgetService;
import com.aimms.modules.budget.service.DepartmentService;
import com.aimms.modules.quota.entity.QuotaRecord;
import com.aimms.modules.quota.service.QuotaRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRuleMapper alertRuleMapper;
    private final AlertLogMapper alertLogMapper;
    private final CompanyBudgetService companyBudgetService;
    private final DepartmentService departmentService;
    private final QuotaRecordService quotaRecordService;

    @Override
    public List<AlertRule> listRules() {
        return alertRuleMapper.selectList(null);
    }

    @Override
    public AlertRule saveRule(AlertRule rule) {
        if (rule.getId() == null) {
            rule.setCreatedAt(LocalDateTime.now());
            alertRuleMapper.insert(rule);
        } else {
            alertRuleMapper.updateById(rule);
        }
        return rule;
    }

    @Override
    public void deleteRule(Integer id) {
        alertRuleMapper.deleteById(id);
    }

    @Override
    @SuppressWarnings("null")
    public List<AlertLog> listLogs() {
        return alertLogMapper.selectList(new LambdaQueryWrapper<AlertLog>().orderByDesc(AlertLog::getCreatedAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndTrigger(Integer userId, Integer deptId, String period) {
        List<AlertRule> rules = listRules();
        for (AlertRule rule : rules) {
            if (!Boolean.TRUE.equals(rule.getEnabled())) {
                continue;
            }
            Integer actualPct = null;
            Integer targetId = rule.getTargetId();
            switch (rule.getTargetType()) {
                case "company":
                    actualPct = calcCompanyUsagePct();
                    break;
                case "department":
                    if (targetId == null || targetId.equals(deptId)) {
                        actualPct = calcDeptUsagePct(deptId != null ? deptId : targetId);
                    }
                    break;
                case "user":
                    if (targetId == null || targetId.equals(0) || targetId.equals(userId)) {
                        actualPct = calcUserUsagePct(userId != null ? userId : targetId, period);
                    }
                    break;
                default:
                    break;
            }
            if (actualPct != null && actualPct >= rule.getThresholdPct()) {
                AlertLog alertLog = new AlertLog();
                alertLog.setRuleId(rule.getId());
                alertLog.setTargetType(rule.getTargetType());
                alertLog.setTargetId(targetId != null ? targetId : 0);
                alertLog.setActualPct(actualPct);
                alertLog.setMessage(String.format("%s 使用率达到 %d%%，超过阈值 %d%%",
                        rule.getTargetType(), actualPct, rule.getThresholdPct()));
                alertLog.setCreatedAt(LocalDateTime.now());
                alertLogMapper.insert(alertLog);
                log.warn("预警触发: {}", alertLog.getMessage());
            }
        }
    }

    private Integer calcCompanyUsagePct() {
        List<CompanyBudget> budgets = companyBudgetService.list();
        if (budgets.isEmpty()) {
            return 0;
        }
        CompanyBudget budget = budgets.get(0);
        BigDecimal total = budget.getTotalBudgetCny();
        BigDecimal used = BigDecimal.ZERO;
        List<Department> departments = departmentService.list();
        for (Department dept : departments) {
            used = used.add(dept.getUsedBudgetCny());
        }
        return pct(used, total);
    }

    private Integer calcDeptUsagePct(Integer deptId) {
        if (deptId == null) {
            return null;
        }
        Department dept = departmentService.getById(deptId);
        if (dept == null) {
            return null;
        }
        return pct(dept.getUsedBudgetCny(), dept.getMonthlyBudgetCny());
    }

    private Integer calcUserUsagePct(Integer userId, String period) {
        if (userId == null) {
            return null;
        }
        QuotaRecord record = quotaRecordService.getRemainingQuota(userId, period);
        BigDecimal total = record.getTotalAmount();
        BigDecimal used = record.getUsedAmount();
        return pct(used, total);
    }

    private Integer pct(BigDecimal used, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return used.multiply(BigDecimal.valueOf(100))
                .divide(total, 2, RoundingMode.HALF_UP)
                .intValue();
    }
}
