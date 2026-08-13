package com.aimms.modules.usage.mapper;

import com.aimms.modules.usage.entity.UsageRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface UsageRecordMapper extends BaseMapper<UsageRecord> {

    @Select("SELECT COALESCE(SUM(amount_cny), 0) FROM usage_record WHERE dept_id = #{deptId} " +
            "AND strftime('%Y-%m', usage_date) = #{period}")
    BigDecimal sumAmountByDeptAndPeriod(@Param("deptId") Integer deptId, @Param("period") String period);
}
