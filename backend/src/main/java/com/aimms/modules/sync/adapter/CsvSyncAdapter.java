package com.aimms.modules.sync.adapter;

import com.aimms.modules.usage.entity.UsageRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CsvSyncAdapter implements SyncAdapter {

    @Override
    public boolean supports(Integer toolId) {
        return true;
    }

    @Override
    public List<UsageRecord> parse(InputStream inputStream, Integer toolId, Integer defaultDeptId,
                                   String defaultCurrency, BigDecimal exchangeRate) throws Exception {
        List<UsageRecord> records = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreEmptyLines(true)
                     .build())) {

            for (CSVRecord csvRecord : parser) {
                UsageRecord record = new UsageRecord();
                record.setToolId(toolId);
                record.setSource("import");

                // 支持两种字段命名：下划线 或 驼峰
                Integer userId = getInt(csvRecord, "user_id", "userId");
                Integer deptId = getInt(csvRecord, "dept_id", "deptId");
                Integer modelId = getInt(csvRecord, "model_id", "modelId");
                String dateStr = getString(csvRecord, "usage_date", "usageDate");
                BigDecimal quantity = getDecimal(csvRecord, "usage_quantity", "usageQuantity");
                BigDecimal originalAmount = getDecimal(csvRecord, "original_amount", "originalAmount");
                String currency = getString(csvRecord, "original_currency", "originalCurrency");
                String remark = getString(csvRecord, "remark", "备注");

                record.setUserId(userId);
                record.setDeptId(deptId != null ? deptId : defaultDeptId);
                record.setModelId(modelId);
                record.setUsageDate(dateStr != null ? LocalDate.parse(dateStr, formatter) : LocalDate.now());
                record.setUsageQuantity(quantity != null ? quantity : BigDecimal.ZERO);
                record.setOriginalAmount(originalAmount != null ? originalAmount : BigDecimal.ZERO);
                record.setOriginalCurrency(currency != null ? currency : defaultCurrency);
                record.setRemark(remark);

                // 币种统一折算为人民币
                BigDecimal amountCny = record.getOriginalAmount();
                if ("USD".equalsIgnoreCase(record.getOriginalCurrency()) && exchangeRate != null) {
                    amountCny = amountCny.multiply(exchangeRate);
                }
                record.setAmountCny(amountCny);

                record.setRawData(csvRecord.toString());
                records.add(record);
            }
        }
        return records;
    }

    private String getString(CSVRecord record, String... keys) {
        for (String key : keys) {
            if (record.isMapped(key)) {
                String value = record.get(key);
                if (value != null && !value.trim().isEmpty()) {
                    return value.trim();
                }
            }
        }
        return null;
    }

    private Integer getInt(CSVRecord record, String... keys) {
        String value = getString(record, keys);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal getDecimal(CSVRecord record, String... keys) {
        String value = getString(record, keys);
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
