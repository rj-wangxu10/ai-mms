package com.aimms.modules.quota.mapper;

import com.aimms.modules.quota.entity.QuotaRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface QuotaRecordMapper extends BaseMapper<QuotaRecord> {

    @Update("UPDATE quota_record SET used_amount = used_amount + #{amount}, " +
            "remaining_amount = remaining_amount - #{amount} " +
            "WHERE user_id = #{userId} AND period = #{period} AND remaining_amount >= #{amount}")
    int deductQuota(@Param("userId") Integer userId, @Param("period") String period,
                    @Param("amount") BigDecimal amount);
}
