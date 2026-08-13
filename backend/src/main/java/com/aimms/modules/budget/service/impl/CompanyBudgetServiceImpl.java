package com.aimms.modules.budget.service.impl;

import com.aimms.modules.budget.entity.CompanyBudget;
import com.aimms.modules.budget.mapper.CompanyBudgetMapper;
import com.aimms.modules.budget.service.CompanyBudgetService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CompanyBudgetServiceImpl extends ServiceImpl<CompanyBudgetMapper, CompanyBudget> implements CompanyBudgetService {
}
