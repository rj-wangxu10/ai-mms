package com.aimms.modules.quota.service;

import com.aimms.modules.quota.entity.QuotaRecord;
import com.baomidou.mybatisplus.spring.service.IService;

import java.math.BigDecimal;
import java.util.List;

public interface QuotaRecordService extends IService<QuotaRecord> {

    List<QuotaRecord> listByUserAndPeriod(Integer userId, String period);

    QuotaRecord getRemainingQuota(Integer userId, String period);

    void batchAllocateBaseQuota(Integer deptId, List<Integer> userIds, String period, BigDecimal amountPerUser);

    void addAdditionalQuota(Integer userId, Integer deptId, String period, BigDecimal amount, Integer applicationId);

    void deductQuota(Integer userId, String period, BigDecimal amount);
}
