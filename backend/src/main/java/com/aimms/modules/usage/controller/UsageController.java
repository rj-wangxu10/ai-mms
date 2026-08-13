package com.aimms.modules.usage.controller;

import com.aimms.common.R;
import com.aimms.modules.usage.entity.UsageRecord;
import com.aimms.modules.usage.service.UsageRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageRecordService usageRecordService;

    @GetMapping
    public R<List<UsageRecord>> list(@RequestParam(required = false) Integer userId,
                                     @RequestParam(required = false) Integer deptId,
                                     @RequestParam(required = false) String period) {
        if (userId != null) {
            return R.ok(usageRecordService.listByUser(userId, period));
        }
        if (deptId != null) {
            return R.ok(usageRecordService.listByDept(deptId, period));
        }
        return R.ok(usageRecordService.list());
    }

    @PostMapping("/import")
    public R<UsageRecordService.ImportResult> importCsv(@RequestParam Integer toolId,
                                                        @RequestParam(required = false) String period,
                                                        @RequestParam("file") MultipartFile file) throws Exception {
        if (period == null || period.isEmpty()) {
            period = java.time.YearMonth.now().toString();
        }
        return R.ok(usageRecordService.importCsv(toolId, file.getInputStream(), period));
    }
}
