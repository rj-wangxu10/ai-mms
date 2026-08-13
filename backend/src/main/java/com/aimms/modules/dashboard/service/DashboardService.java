package com.aimms.modules.dashboard.service;

import com.aimms.modules.dashboard.dto.DashboardDTO;

public interface DashboardService {

    DashboardDTO.EmployeeDashboard employeeDashboard(Integer userId, String period);

    DashboardDTO.ManagerDashboard managerDashboard(Integer deptId, String period);

    DashboardDTO.AdminDashboard adminDashboard(String period);
}
