package com.aimms.modules.report.service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ReportService {

    void exportUsage(Integer userId, Integer deptId, String period, String format, HttpServletResponse response) throws IOException;

    void exportBudgetExecution(String period, String format, HttpServletResponse response) throws IOException;

    void exportToolRanking(String period, String format, HttpServletResponse response) throws IOException;
}
