package com.aimms.modules.quota.service.impl;

import com.aimms.common.BusinessException;
import com.aimms.common.Constants;
import com.aimms.modules.budget.entity.Department;
import com.aimms.modules.budget.service.DepartmentService;
import com.aimms.modules.quota.entity.QuotaRecord;
import com.aimms.modules.quota.mapper.QuotaRecordMapper;
import com.aimms.modules.quota.service.QuotaRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotaRecordServiceImpl extends ServiceImpl<QuotaRecordMapper, QuotaRecord> implements QuotaRecordService {

    private final DepartmentService departmentService;

    @Override
    public List<QuotaRecord> listByUserAndPeriod(Integer userId, String period) {
        LambdaQueryWrapper<QuotaRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuotaRecord::getUserId, userId);
        if (period != null) {
            wrapper.eq(QuotaRecord::getPeriod, period);
        }
        wrapper.orderByAsc(QuotaRecord::getId);
        return list(wrapper);
    }

    @Override
    public QuotaRecord getRemainingQuota(Integer userId, String period) {
        List<QuotaRecord> records = listByUserAndPeriod(userId, period);
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal used = BigDecimal.ZERO;
        BigDecimal remaining = BigDecimal.ZERO;
        Integer deptId = null;
        for (QuotaRecord record : records) {
            total = total.add(record.getTotalAmount());
            used = used.add(record.getUsedAmount());
            remaining = remaining.add(record.getRemainingAmount());
            deptId = record.getDeptId();
        }
        QuotaRecord summary = new QuotaRecord();
        summary.setUserId(userId);
        summary.setDeptId(deptId);
        summary.setPeriod(period);
        summary.setTotalAmount(total);
        summary.setUsedAmount(used);
        summary.setRemainingAmount(remaining);
        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAllocateBaseQuota(Integer deptId, List<Integer> userIds, String period, BigDecimal amountPerUser) {
        Department department = departmentService.getById(deptId);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }
        BigDecimal totalNeed = amountPerUser.multiply(BigDecimal.valueOf(userIds.size()));
        if (department.getMonthlyBudgetCny().compareTo(totalNeed) < 0) {
            throw new BusinessException("部门月度预算不足");
        }

        for (Integer userId : userIds) {
            LambdaQueryWrapper<QuotaRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(QuotaRecord::getUserId, userId)
                    .eq(QuotaRecord::getPeriod, period)
                    .eq(QuotaRecord::getQuotaType, Constants.QUOTA_TYPE_MONTHLY_BASE);
            QuotaRecord exist = getOne(wrapper);
            if (exist != null) {
                exist.setTotalAmount(amountPerUser);
                exist.setRemainingAmount(amountPerUser.subtract(exist.getUsedAmount()));
                updateById(exist);
            } else {
                QuotaRecord record = new QuotaRecord();
                record.setUserId(userId);
                record.setDeptId(deptId);
                record.setPeriod(period);
                record.setQuotaType(Constants.QUOTA_TYPE_MONTHLY_BASE);
                record.setTotalAmount(amountPerUser);
                record.setUsedAmount(BigDecimal.ZERO);
                record.setRemainingAmount(amountPerUser);
                save(record);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAdditionalQuota(Integer userId, Integer deptId, String period, BigDecimal amount, Integer applicationId) {
        QuotaRecord record = new QuotaRecord();
        record.setUserId(userId);
        record.setDeptId(deptId);
        record.setPeriod(period);
        record.setQuotaType(Constants.QUOTA_TYPE_ADDITIONAL);
        record.setTotalAmount(amount);
        record.setUsedAmount(BigDecimal.ZERO);
        record.setRemainingAmount(amount);
        record.setSourceApplicationId(applicationId);
        save(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductQuota(Integer userId, String period, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        // 先扣月度基础额度，再扣追加额度
        LambdaQueryWrapper<QuotaRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuotaRecord::getUserId, userId)
                .eq(QuotaRecord::getPeriod, period)
                .gt(QuotaRecord::getRemainingAmount, BigDecimal.ZERO)
                .orderByAsc(QuotaRecord::getQuotaType)
                .orderByAsc(QuotaRecord::getId);
        List<QuotaRecord> records = list(wrapper);
        BigDecimal remaining = amount;
        for (QuotaRecord record : records) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal deduct = record.getRemainingAmount().min(remaining);
            record.setUsedAmount(record.getUsedAmount().add(deduct));
            record.setRemainingAmount(record.getRemainingAmount().subtract(deduct));
            updateById(record);
            remaining = remaining.subtract(deduct);
        }
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("用户额度不足，无法扣减");
        }
    }
}
