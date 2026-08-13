import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 1,
    username: 'admin',
    role: 'admin',
    deptId: 1
  }),
  getters: {
    isAdmin: (state) => state.role === 'admin',
    isManager: (state) => state.role === 'manager',
    isEmployee: (state) => state.role === 'employee'
  },
  actions: {
    setUser(user: { userId: number; username: string; role: string; deptId: number }) {
      this.userId = user.userId;
      this.username = user.username;
      this.role = user.role;
      this.deptId = user.deptId;
    }
  },
  persist: true
});
