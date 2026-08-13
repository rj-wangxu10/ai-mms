package com.aimms.modules.budget.service.impl;

import com.aimms.modules.budget.entity.Department;
import com.aimms.modules.budget.mapper.DepartmentMapper;
import com.aimms.modules.budget.service.DepartmentService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public void increaseUsedBudget(Integer deptId, BigDecimal amount) {
        baseMapper.increaseUsedBudget(deptId, amount);
    }
}
