package com.aimms.modules.budget.controller;

import com.aimms.common.R;
import com.aimms.modules.budget.entity.CompanyBudget;
import com.aimms.modules.budget.entity.Department;
import com.aimms.modules.budget.service.CompanyBudgetService;
import com.aimms.modules.budget.service.DepartmentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final CompanyBudgetService companyBudgetService;
    private final DepartmentService departmentService;

    @GetMapping("/company")
    public R<List<CompanyBudget>> listCompanyBudget() {
        return R.ok(companyBudgetService.list());
    }

    @PostMapping("/company")
    public R<Void> saveCompanyBudget(@RequestBody CompanyBudget budget) {
        companyBudgetService.saveOrUpdate(budget);
        return R.ok();
    }

    @GetMapping("/department")
    public R<List<Department>> listDepartment() {
        return R.ok(departmentService.list());
    }

    @GetMapping("/department/{id}")
    public R<Department> getDepartment(@PathVariable Integer id) {
        return R.ok(departmentService.getById(id));
    }

    @PostMapping("/department")
    public R<Void> saveDepartment(@RequestBody Department department) {
        departmentService.save(department);
        return R.ok();
    }

    @PutMapping("/department/{id}")
    public R<Void> updateDepartment(@PathVariable Integer id, @RequestBody Department department) {
        department.setId(id);
        departmentService.updateById(department);
        return R.ok();
    }

    @DeleteMapping("/department/{id}")
    public R<Void> deleteDepartment(@PathVariable Integer id) {
        departmentService.removeById(id);
        return R.ok();
    }
}
