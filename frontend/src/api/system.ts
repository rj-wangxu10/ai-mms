import request from './request';

export function getUsers(params?: any) {
  return request.get('/system/user', { params });
}

export function saveUser(data: any) {
  return request.post('/system/user', data);
}

export function updateUser(id: number, data: any) {
  return request.put(`/system/user/${id}`, data);
}

export function deleteUser(id: number) {
  return request.delete(`/system/user/${id}`);
}
