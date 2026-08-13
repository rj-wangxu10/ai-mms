package com.aimms.modules.dashboard.controller;

import com.aimms.common.R;
import com.aimms.modules.dashboard.dto.DashboardDTO;
import com.aimms.modules.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    private String currentPeriod() {
        return YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    @GetMapping("/employee")
    public R<DashboardDTO.EmployeeDashboard> employee(@RequestParam Integer userId,
                                                      @RequestParam(required = false) String period) {
        return R.ok(dashboardService.employeeDashboard(userId, period != null ? period : currentPeriod()));
    }

    @GetMapping("/manager")
    public R<DashboardDTO.ManagerDashboard> manager(@RequestParam Integer deptId,
                                                    @RequestParam(required = false) String period) {
        return R.ok(dashboardService.managerDashboard(deptId, period != null ? period : currentPeriod()));
    }

    @GetMapping("/admin")
    public R<DashboardDTO.AdminDashboard> admin(@RequestParam(required = false) String period) {
        return R.ok(dashboardService.adminDashboard(period != null ? period : currentPeriod()));
    }
}
