package com.aimms.modules.sync.adapter;

import com.aimms.modules.usage.entity.UsageRecord;

import java.io.InputStream;
import java.util.List;

/**
 * 工具账单同步适配器接口。
 * 新增工具时只需实现该接口并注册为 Spring Bean。
 */
public interface SyncAdapter {

    boolean supports(Integer toolId);

    List<UsageRecord> parse(InputStream inputStream, Integer toolId, Integer defaultDeptId, String defaultCurrency,
                            java.math.BigDecimal exchangeRate) throws Exception;
}
