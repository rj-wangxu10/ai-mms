package com.aimms.modules.report.service.impl;

import com.alibaba.excel.EasyExcel;
import com.aimms.modules.budget.entity.Department;
import com.aimms.modules.budget.service.DepartmentService;
import com.aimms.modules.report.service.ReportService;
import com.aimms.modules.tool.entity.AiTool;
import com.aimms.modules.tool.service.AiToolService;
import com.aimms.modules.usage.entity.UsageRecord;
import com.aimms.modules.usage.service.UsageRecordService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UsageRecordService usageRecordService;
    private final DepartmentService departmentService;
    private final AiToolService aiToolService;

    @Override
    public void exportUsage(Integer userId, Integer deptId, String period, String format,
                            HttpServletResponse response) throws IOException {
        List<UsageRecord> records;
        if (userId != null) {
            records = usageRecordService.listByUser(userId, period);
        } else if (deptId != null) {
            records = usageRecordService.listByDept(deptId, period);
        } else {
            records = usageRecordService.list();
        }

        List<UsageRow> rows = records.stream().map(r -> {
            UsageRow row = new UsageRow();
            row.setUsageDate(r.getUsageDate() != null ? r.getUsageDate().toString() : "");
            row.setUserId(r.getUserId());
            row.setDeptId(r.getDeptId());
            row.setToolId(r.getToolId());
            row.setModelId(r.getModelId());
            row.setUsageQuantity(r.getUsageQuantity());
            row.setOriginalAmount(r.getOriginalAmount());
            row.setOriginalCurrency(r.getOriginalCurrency());
            row.setAmountCny(r.getAmountCny());
            row.setRemark(r.getRemark());
            return row;
        }).collect(Collectors.toList());

        if ("xlsx".equalsIgnoreCase(format)) {
            writeExcel(response, "消费明细", UsageRow.class, rows);
        } else {
            writeCsv(response, "消费明细", rows);
        }
    }

    @Override
    public void exportBudgetExecution(String period, String format, HttpServletResponse response) throws IOException {
        List<Department> departments = departmentService.list();
        List<BudgetRow> rows = departments.stream().map(d -> {
            BudgetRow row = new BudgetRow();
            row.setDeptId(d.getId());
            row.setDeptName(d.getName());
            row.setMonthlyBudget(d.getMonthlyBudgetCny());
            row.setUsedBudget(d.getUsedBudgetCny());
            row.setRemainingBudget(d.getMonthlyBudgetCny().subtract(d.getUsedBudgetCny()));
            row.setUsagePct(d.getMonthlyBudgetCny().compareTo(BigDecimal.ZERO) > 0
                    ? d.getUsedBudgetCny().multiply(BigDecimal.valueOf(100)).divide(d.getMonthlyBudgetCny(), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO);
            return row;
        }).collect(Collectors.toList());

        if ("xlsx".equalsIgnoreCase(format)) {
            writeExcel(response, "预算执行率", BudgetRow.class, rows);
        } else {
            writeCsv(response, "预算执行率", rows);
        }
    }

    @Override
    public void exportToolRanking(String period, String format, HttpServletResponse response) throws IOException {
        List<UsageRecord> records;
        if (period != null && !period.isEmpty()) {
            records = usageRecordService.list().stream()
                    .filter(u -> u.getUsageDate().toString().startsWith(period))
                    .collect(Collectors.toList());
        } else {
            records = usageRecordService.list();
        }
        Map<Integer, List<UsageRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(UsageRecord::getToolId));
        List<ToolRow> rows = new ArrayList<>();
        for (Map.Entry<Integer, List<UsageRecord>> entry : grouped.entrySet()) {
            AiTool tool = aiToolService.getById(entry.getKey());
            BigDecimal amount = entry.getValue().stream().map(UsageRecord::getAmountCny)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ToolRow row = new ToolRow();
            row.setToolId(entry.getKey());
            row.setToolName(tool != null ? tool.getName() : "未知工具");
            row.setAmountCny(amount);
            rows.add(row);
        }
        rows.sort(Comparator.comparing(ToolRow::getAmountCny).reversed());

        if ("xlsx".equalsIgnoreCase(format)) {
            writeExcel(response, "模型费用排行", ToolRow.class, rows);
        } else {
            writeCsv(response, "模型费用排行", rows);
        }
    }

    private void writeExcel(HttpServletResponse response, String fileName, Class<?> clazz, List<?> data) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encoded + ".xlsx");
        EasyExcel.write(response.getOutputStream(), clazz).sheet(fileName).doWrite(data);
    }

    private void writeCsv(HttpServletResponse response, String fileName, List<?> data) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding("utf-8");
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encoded + ".csv");
        if (data.isEmpty()) {
            return;
        }
        try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8),
                CSVFormat.DEFAULT)) {
            for (Object item : data) {
                List<String> values = new ArrayList<>();
                for (java.lang.reflect.Field field : item.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(item);
                        values.add(value != null ? value.toString() : "");
                    } catch (IllegalAccessException ignored) {
                    }
                }
                printer.printRecord(values);
            }
        }
    }

    @Data
    public static class UsageRow {
        private String usageDate;
        private Integer userId;
        private Integer deptId;
        private Integer toolId;
        private Integer modelId;
        private BigDecimal usageQuantity;
        private BigDecimal originalAmount;
        private String originalCurrency;
        private BigDecimal amountCny;
        private String remark;
    }

    @Data
    public static class BudgetRow {
        private Integer deptId;
        private String deptName;
        private BigDecimal monthlyBudget;
        private BigDecimal usedBudget;
        private BigDecimal remainingBudget;
        private BigDecimal usagePct;
    }

    @Data
    public static class ToolRow {
        private Integer toolId;
        private String toolName;
        private BigDecimal amountCny;
    }
}
