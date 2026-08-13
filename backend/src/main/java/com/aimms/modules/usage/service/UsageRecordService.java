package com.aimms.modules.usage.service;

import com.aimms.modules.usage.entity.UsageRecord;
import com.baomidou.mybatisplus.spring.service.IService;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

public interface UsageRecordService extends IService<UsageRecord> {

    List<UsageRecord> listByUser(Integer userId, String period);

    List<UsageRecord> listByDept(Integer deptId, String period);

    BigDecimal sumAmountByDeptAndPeriod(Integer deptId, String period);

    ImportResult importCsv(Integer toolId, InputStream inputStream, String period);

    class ImportResult {
        private int totalRows;
        private int successRows;
        private int failRows;
        private BigDecimal totalAmountCny;
        private String message;

        public int getTotalRows() { return totalRows; }
        public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
        public int getSuccessRows() { return successRows; }
        public void setSuccessRows(int successRows) { this.successRows = successRows; }
        public int getFailRows() { return failRows; }
        public void setFailRows(int failRows) { this.failRows = failRows; }
        public BigDecimal getTotalAmountCny() { return totalAmountCny; }
        public void setTotalAmountCny(BigDecimal totalAmountCny) { this.totalAmountCny = totalAmountCny; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
