package com.aimms.modules.budget.mapper;

import com.aimms.modules.budget.entity.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    @Update("UPDATE department SET used_budget_cny = used_budget_cny + #{amount} WHERE id = #{deptId}")
    int increaseUsedBudget(Integer deptId, java.math.BigDecimal amount);
}
