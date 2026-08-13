package com.aimms.modules.report.controller;

import com.aimms.common.R;
import com.aimms.modules.report.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/export")
    public void export(@RequestParam String type,
                          @RequestParam(required = false, defaultValue = "xlsx") String format,
                          @RequestParam(required = false) Integer userId,
                          @RequestParam(required = false) Integer deptId,
                          @RequestParam(required = false) String period,
                          HttpServletResponse response) throws IOException {
        switch (type) {
            case "usage":
                reportService.exportUsage(userId, deptId, period, format, response);
                break;
            case "budget":
                reportService.exportBudgetExecution(period, format, response);
                break;
            case "tool":
                reportService.exportToolRanking(period, format, response);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }
}
