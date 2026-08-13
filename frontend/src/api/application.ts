import request from './request';

export function getApplications(params?: any) {
  return request.get('/application', { params });
}

export function submitApplication(data: any) {
  return request.post('/application', data);
}

export function approveApplication(id: number, approverId: number, comment?: string) {
  return request.put(`/application/${id}/approve`, null, { params: { approverId, comment } });
}

export function rejectApplication(id: number, approverId: number, comment?: string) {
  return request.put(`/application/${id}/reject`, null, { params: { approverId, comment } });
}

export function adminApproveApplication(id: number, adminId: number, comment?: string) {
  return request.put(`/application/${id}/admin-approve`, null, { params: { adminId, comment } });
}
