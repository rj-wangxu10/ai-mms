import axios from 'axios';
import { useUserStore } from '../store';

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 30000
});

request.interceptors.request.use((config) => {
  const userStore = useUserStore();
  config.headers['X-User-Id'] = userStore.userId;
  config.headers['X-User-Role'] = userStore.role;
  return config;
});

request.interceptors.response.use(
  (response) => {
    const data = response.data;
    if (data.code !== 200) {
      return Promise.reject(new Error(data.message || '请求失败'));
    }
    return data;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default request;
