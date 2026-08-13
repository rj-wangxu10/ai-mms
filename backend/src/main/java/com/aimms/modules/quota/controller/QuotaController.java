package com.aimms.modules.quota.controller;

import com.aimms.common.R;
import com.aimms.modules.quota.entity.QuotaRecord;
import com.aimms.modules.quota.service.QuotaRecordService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/quota")
@RequiredArgsConstructor
public class QuotaController {

    private final QuotaRecordService quotaRecordService;

    @GetMapping("/remaining")
    public R<QuotaRecord> remaining(@RequestParam Integer userId,
                                    @RequestParam(required = false) String period) {
        if (period == null) {
            period = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        return R.ok(quotaRecordService.getRemainingQuota(userId, period));
    }

    @GetMapping
    public R<List<QuotaRecord>> list(@RequestParam Integer userId,
                                     @RequestParam(required = false) String period) {
        return R.ok(quotaRecordService.listByUserAndPeriod(userId, period));
    }

    @PostMapping("/batch")
    public R<Void> batchAllocate(@RequestBody BatchAllocateRequest request) {
        quotaRecordService.batchAllocateBaseQuota(request.getDeptId(), request.getUserIds(),
                request.getPeriod(), request.getAmountPerUser());
        return R.ok();
    }

    @Data
    public static class BatchAllocateRequest {
        private Integer deptId;
        private List<Integer> userIds;
        private String period;
        private BigDecimal amountPerUser;
    }
}
