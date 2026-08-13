import request from './request';

export function getUsageRecords(params: any) {
  return request.get('/usage', { params });
}

export function importUsage(toolId: number, file: File, period?: string) {
  const formData = new FormData();
  formData.append('file', file);
  return request.post(`/usage/import?toolId=${toolId}${period ? '&period=' + period : ''}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}
