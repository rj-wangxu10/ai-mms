package com.aimms.modules.application.service.impl;

import com.aimms.common.BusinessException;
import com.aimms.common.Constants;
import com.aimms.modules.application.entity.QuotaApplication;
import com.aimms.modules.application.mapper.QuotaApplicationMapper;
import com.aimms.modules.application.service.QuotaApplicationService;
import com.aimms.modules.budget.entity.Department;
import com.aimms.modules.budget.service.DepartmentService;
import com.aimms.modules.quota.service.QuotaRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotaApplicationServiceImpl extends ServiceImpl<QuotaApplicationMapper, QuotaApplication>
        implements QuotaApplicationService {

    private final DepartmentService departmentService;
    private final QuotaRecordService quotaRecordService;

    // 审批升级阈值：超过 5000 元或部门剩余预算 50% 需升级管理员
    private static final BigDecimal UPGRADE_THRESHOLD = new BigDecimal("5000");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuotaApplication submit(QuotaApplication application) {
        Department department = departmentService.getById(application.getDeptId());
        if (department == null) {
            throw new BusinessException("部门不存在");
        }
        application.setStatus(Constants.APPLICATION_STATUS_PENDING);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        save(application);
        return application;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuotaApplication approve(Integer id, Integer approverId, String comment) {
        QuotaApplication application = getById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!Constants.APPLICATION_STATUS_PENDING.equals(application.getStatus())) {
            throw new BusinessException("申请状态不正确");
        }

        Department department = departmentService.getById(application.getDeptId());
        BigDecimal deptRemaining = department.getMonthlyBudgetCny().subtract(department.getUsedBudgetCny());

        // 判断是否需要升级
        boolean needUpgrade = application.getAmount().compareTo(UPGRADE_THRESHOLD) > 0
                || application.getAmount().compareTo(deptRemaining.multiply(new BigDecimal("0.5"))) > 0
                || application.getAmount().compareTo(deptRemaining) > 0;

        if (needUpgrade) {
            return upgradeToAdmin(id);
        }

        if (application.getAmount().compareTo(deptRemaining) > 0) {
            throw new BusinessException("部门剩余预算不足");
        }

        application.setStatus(Constants.APPLICATION_STATUS_MANAGER_APPROVED);
        application.setApproverId(approverId);
        application.setApproveComment(comment);
        application.setUpdatedAt(LocalDateTime.now());
        updateById(application);

        // 增加追加额度
        String period = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        quotaRecordService.addAdditionalQuota(application.getApplicantId(), application.getDeptId(),
                period, application.getAmount(), application.getId());

        // 扣减部门已用预算（追加额度占用预算）
        departmentService.increaseUsedBudget(application.getDeptId(), application.getAmount());

        return application;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuotaApplication reject(Integer id, Integer approverId, String comment) {
        QuotaApplication application = getById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        application.setStatus(Constants.APPLICATION_STATUS_REJECTED);
        application.setApproverId(approverId);
        application.setApproveComment(comment);
        application.setUpdatedAt(LocalDateTime.now());
        updateById(application);
        return application;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuotaApplication upgradeToAdmin(Integer id) {
        QuotaApplication application = getById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        application.setStatus("pending_admin");
        application.setUpdatedAt(LocalDateTime.now());
        updateById(application);
        return application;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuotaApplication adminApprove(Integer id, Integer adminId, String comment) {
        QuotaApplication application = getById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        application.setStatus(Constants.APPLICATION_STATUS_ADMIN_APPROVED);
        application.setApproverId(adminId);
        application.setApproveComment(comment);
        application.setUpdatedAt(LocalDateTime.now());
        updateById(application);

        String period = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        quotaRecordService.addAdditionalQuota(application.getApplicantId(), application.getDeptId(),
                period, application.getAmount(), application.getId());
        departmentService.increaseUsedBudget(application.getDeptId(), application.getAmount());

        return application;
    }

    @Override
    public List<QuotaApplication> listPending(Integer deptId) {
        LambdaQueryWrapper<QuotaApplication> wrapper = new LambdaQueryWrapper<>();
        if (deptId != null) {
            wrapper.eq(QuotaApplication::getDeptId, deptId);
        }
        wrapper.in(QuotaApplication::getStatus, Constants.APPLICATION_STATUS_PENDING, "pending_admin");
        wrapper.orderByDesc(QuotaApplication::getCreatedAt);
        return list(wrapper);
    }
}
