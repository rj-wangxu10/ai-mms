package com.aimms.modules.budget.service;

import com.aimms.modules.budget.entity.Department;
import com.baomidou.mybatisplus.spring.service.IService;

import java.math.BigDecimal;

public interface DepartmentService extends IService<Department> {

    void increaseUsedBudget(Integer deptId, BigDecimal amount);
}
