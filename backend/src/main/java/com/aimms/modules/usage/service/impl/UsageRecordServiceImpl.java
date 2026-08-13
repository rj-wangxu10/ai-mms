package com.aimms.modules.usage.service.impl;

import com.aimms.common.BusinessException;
import com.aimms.modules.budget.service.DepartmentService;
import com.aimms.modules.tool.entity.AiTool;
import com.aimms.modules.quota.service.QuotaRecordService;
import com.aimms.modules.sync.adapter.CsvSyncAdapter;
import com.aimms.modules.tool.service.AiToolService;
import com.aimms.modules.usage.entity.UsageRecord;
import com.aimms.modules.usage.mapper.UsageRecordMapper;
import com.aimms.modules.usage.service.UsageRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageRecordServiceImpl extends ServiceImpl<UsageRecordMapper, UsageRecord> implements UsageRecordService {

    private final AiToolService aiToolService;
    private final DepartmentService departmentService;
    private final QuotaRecordService quotaRecordService;
    private final CsvSyncAdapter csvSyncAdapter;

    @Override
    public List<UsageRecord> listByUser(Integer userId, String period) {
        LambdaQueryWrapper<UsageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsageRecord::getUserId, userId);
        if (period != null && !period.isEmpty()) {
            wrapper.apply("strftime('%Y-%m', usage_date) = {0}", period);
        }
        wrapper.orderByDesc(UsageRecord::getUsageDate);
        return list(wrapper);
    }

    @Override
    public List<UsageRecord> listByDept(Integer deptId, String period) {
        LambdaQueryWrapper<UsageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsageRecord::getDeptId, deptId);
        if (period != null && !period.isEmpty()) {
            wrapper.apply("strftime('%Y-%m', usage_date) = {0}", period);
        }
        wrapper.orderByDesc(UsageRecord::getUsageDate);
        return list(wrapper);
    }

    @Override
    public BigDecimal sumAmountByDeptAndPeriod(Integer deptId, String period) {
        return baseMapper.sumAmountByDeptAndPeriod(deptId, period);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResult importCsv(Integer toolId, InputStream inputStream, String period) {
        AiTool tool = aiToolService.getById(toolId);
        if (tool == null) {
            throw new BusinessException("工具不存在");
        }

        BigDecimal exchangeRate = BigDecimal.valueOf(7.1);
        BigDecimal totalAmountCny = BigDecimal.ZERO;
        int successRows = 0;
        int failRows = 0;

        try {
            List<UsageRecord> records = csvSyncAdapter.parse(inputStream, toolId, 1, tool.getCurrency(), exchangeRate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

            for (UsageRecord record : records) {
                String recordPeriod = record.getUsageDate().format(formatter);
                if (!recordPeriod.equals(period)) {
                    record.setUsageDate(java.time.LocalDate.parse(period + "-01"));
                }

                save(record);
                totalAmountCny = totalAmountCny.add(record.getAmountCny());

                // 更新部门已用预算
                departmentService.increaseUsedBudget(record.getDeptId(), record.getAmountCny());

                // 更新个人额度（如果能归属到个人）
                if (record.getUserId() != null) {
                    try {
                        quotaRecordService.deductQuota(record.getUserId(), period, record.getAmountCny());
                    } catch (BusinessException e) {
                        log.warn("用户 {} 额度不足，消费记录归入部门公共池: {}", record.getUserId(), e.getMessage());
                    }
                }
                successRows++;
            }
        } catch (Exception e) {
            log.error("CSV 导入失败", e);
            throw new BusinessException("CSV 解析失败: " + e.getMessage());
        }

        ImportResult result = new ImportResult();
        result.setTotalRows(successRows + failRows);
        result.setSuccessRows(successRows);
        result.setFailRows(failRows);
        result.setTotalAmountCny(totalAmountCny);
        result.setMessage("导入成功");
        return result;
    }
}
