package com.aimms.modules.application.controller;

import com.aimms.common.R;
import com.aimms.modules.application.entity.QuotaApplication;
import com.aimms.modules.application.service.QuotaApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final QuotaApplicationService quotaApplicationService;

    @GetMapping
    public R<List<QuotaApplication>> list(@RequestParam(required = false) Integer deptId,
                                          @RequestParam(required = false) String status) {
        if ("pending".equals(status)) {
            return R.ok(quotaApplicationService.listPending(deptId));
        }
        return R.ok(quotaApplicationService.list());
    }

    @PostMapping
    public R<QuotaApplication> submit(@RequestBody QuotaApplication application) {
        return R.ok(quotaApplicationService.submit(application));
    }

    @PutMapping("/{id}/approve")
    public R<QuotaApplication> approve(@PathVariable Integer id,
                                       @RequestParam Integer approverId,
                                       @RequestParam(required = false) String comment) {
        return R.ok(quotaApplicationService.approve(id, approverId, comment));
    }

    @PutMapping("/{id}/reject")
    public R<QuotaApplication> reject(@PathVariable Integer id,
                                      @RequestParam Integer approverId,
                                      @RequestParam(required = false) String comment) {
        return R.ok(quotaApplicationService.reject(id, approverId, comment));
    }

    @PutMapping("/{id}/admin-approve")
    public R<QuotaApplication> adminApprove(@PathVariable Integer id,
                                            @RequestParam Integer adminId,
                                            @RequestParam(required = false) String comment) {
        return R.ok(quotaApplicationService.adminApprove(id, adminId, comment));
    }
}
