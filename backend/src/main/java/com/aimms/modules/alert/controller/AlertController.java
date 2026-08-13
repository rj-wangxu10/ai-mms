package com.aimms.modules.alert.controller;

import com.aimms.common.R;
import com.aimms.modules.alert.entity.AlertLog;
import com.aimms.modules.alert.entity.AlertRule;
import com.aimms.modules.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alert")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/rule")
    public R<List<AlertRule>> listRules() {
        return R.ok(alertService.listRules());
    }

    @PostMapping("/rule")
    public R<AlertRule> saveRule(@RequestBody AlertRule rule) {
        return R.ok(alertService.saveRule(rule));
    }

    @DeleteMapping("/rule/{id}")
    public R<Void> deleteRule(@PathVariable Integer id) {
        alertService.deleteRule(id);
        return R.ok();
    }

    @GetMapping("/log")
    public R<List<AlertLog>> listLogs() {
        return R.ok(alertService.listLogs());
    }

    @PostMapping("/check")
    public R<Void> check(@RequestParam(required = false) Integer userId,
                         @RequestParam(required = false) Integer deptId,
                         @RequestParam(required = false) String period) {
        alertService.checkAndTrigger(userId, deptId, period);
        return R.ok();
    }
}
