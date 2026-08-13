<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="title">AI 费用管理平台</div>
      <div class="actions">
        <el-select v-model="currentRole" placeholder="切换角色" style="width: 160px; margin-right: 12px;" @change="onRoleChange">
          <el-option label="管理员" value="admin" />
          <el-option label="部门主管" value="manager" />
          <el-option label="员工" value="employee" />
        </el-select>
        <el-select v-if="currentRole !== 'admin'" v-model="currentUserId" placeholder="切换用户" style="width: 160px;" @change="onUserChange">
          <el-option v-for="u in filteredUsers" :key="u.id" :label="u.username" :value="u.id" />
        </el-select>
      </div>
    </el-header>
    <el-container>
      <el-aside width="220px" class="aside">
        <el-menu :default-active="$route.path" router>
          <template v-if="isEmployee">
            <el-menu-item index="/employee">员工看板</el-menu-item>
            <el-menu-item index="/employee/usage">消费明细</el-menu-item>
            <el-menu-item index="/employee/application">额度申请</el-menu-item>
          </template>
          <template v-if="isManager">
            <el-menu-item index="/manager">主管看板</el-menu-item>
            <el-menu-item index="/manager/member">成员排名</el-menu-item>
            <el-menu-item index="/manager/application">审批</el-menu-item>
          </template>
          <template v-if="isAdmin">
            <el-menu-item index="/admin">全局看板</el-menu-item>
            <el-menu-item index="/admin/budget">预算配置</el-menu-item>
            <el-menu-item index="/admin/tool">工具管理</el-menu-item>
            <el-menu-item index="/admin/user">用户管理</el-menu-item>
            <el-menu-item index="/admin/import">账单导入</el-menu-item>
            <el-menu-item index="/admin/application">终审</el-menu-item>
            <el-menu-item index="/admin/alert">预警配置</el-menu-item>
          </template>
        </el-menu>
      </el-aside>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '../store';
import { getUsers } from '../api/system';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const currentRole = ref(userStore.role);
const currentUserId = ref(userStore.userId);
const users = ref<any[]>([]);

const isEmployee = computed(() => currentRole.value === 'employee');
const isManager = computed(() => currentRole.value === 'manager');
const isAdmin = computed(() => currentRole.value === 'admin');

const filteredUsers = computed(() => {
  if (currentRole.value === 'manager') {
    return users.value.filter((u) => u.role === 'manager');
  }
  if (currentRole.value === 'employee') {
    return users.value.filter((u) => u.role === 'employee');
  }
  return users.value;
});

// Auto-correct currentUserId when it's not in the filtered list
watch(filteredUsers, (list) => {
  if (list.length > 0 && !list.find((u) => u.id === currentUserId.value)) {
    const first = list[0];
    currentUserId.value = first.id;
    userStore.setUser({ userId: first.id, username: first.username, role: currentRole.value, deptId: first.deptId });
  }
});

function onRoleChange() {
  if (currentRole.value === 'admin') {
    userStore.setUser({ userId: 1, username: 'admin', role: 'admin', deptId: 1 });
    router.push('/admin');
  } else if (currentRole.value === 'manager') {
    const manager = users.value.find((u) => u.role === 'manager');
    if (manager) {
      currentUserId.value = manager.id;
      userStore.setUser({ userId: manager.id, username: manager.username, role: 'manager', deptId: manager.deptId });
      router.push('/manager');
    }
  } else {
    const emp = users.value.find((u) => u.role === 'employee');
    if (emp) {
      userStore.setUser({ userId: emp.id, username: emp.username, role: 'employee', deptId: emp.deptId });
      currentUserId.value = emp.id;
      router.push('/employee');
    }
  }
}

function onUserChange() {
  const u = users.value.find((item) => item.id === currentUserId.value);
  if (u) {
    userStore.setUser({ userId: u.id, username: u.username, role: currentRole.value, deptId: u.deptId });
  }
}

onMounted(async () => {
  const res = await getUsers();
  users.value = res.data || [];
});

watch(() => route.path, () => {
  currentRole.value = userStore.role;
  currentUserId.value = userStore.userId;
});
</script>

<style scoped>
.layout {
  height: 100vh;
}
.header {
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.title {
  font-size: 20px;
  font-weight: bold;
}
.aside {
  background: #f5f7fa;
}
.main {
  padding: 20px;
  overflow: auto;
}
</style>
