import request from './request';

export function exportReportUrl(type: string, format: string = 'xlsx', params: any = {}) {
  const query = new URLSearchParams({ type, format, ...params }).toString();
  return `/api/v1/report/export?${query}`;
}
