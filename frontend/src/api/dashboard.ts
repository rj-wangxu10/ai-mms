import request from './request';

export function getEmployeeDashboard(userId: number, period?: string) {
  return request.get('/dashboard/employee', { params: { userId, period } });
}

export function getManagerDashboard(deptId: number, period?: string) {
  return request.get('/dashboard/manager', { params: { deptId, period } });
}

export function getAdminDashboard(period?: string) {
  return request.get('/dashboard/admin', { params: { period } });
}
