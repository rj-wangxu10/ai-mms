import request from './request';

export function getRemainingQuota(userId: number, period?: string) {
  return request.get('/quota/remaining', { params: { userId, period } });
}

export function batchAllocateQuota(data: any) {
  return request.post('/quota/batch', data);
}
