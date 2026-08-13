import request from './request';

export function getCompanyBudget() {
  return request.get('/budget/company');
}

export function saveCompanyBudget(data: any) {
  return request.post('/budget/company', data);
}

export function getDepartments() {
  return request.get('/budget/department');
}

export function saveDepartment(data: any) {
  return request.post('/budget/department', data);
}

export function updateDepartment(id: number, data: any) {
  return request.put(`/budget/department/${id}`, data);
}

export function deleteDepartment(id: number) {
  return request.delete(`/budget/department/${id}`);
}
