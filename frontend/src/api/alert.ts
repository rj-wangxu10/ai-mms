import request from './request';

export function getAlertRules() {
  return request.get('/alert/rule');
}

export function saveAlertRule(data: any) {
  return request.post('/alert/rule', data);
}

export function deleteAlertRule(id: number) {
  return request.delete(`/alert/rule/${id}`);
}

export function getAlertLogs() {
  return request.get('/alert/log');
}

export function checkAlert(params?: any) {
  return request.post('/alert/check', null, { params });
}
