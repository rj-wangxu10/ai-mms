<template>
  <div class="admin-user">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" size="small" @click="openDialog(null)">新增用户</el-button>
        </div>
      </template>
      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="deptId" label="部门ID" width="100" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="roleType(row.role)">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="toolAccounts" label="工具账号" show-overflow-tooltip />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteUser(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="部门ID">
          <el-input-number v-model="form.deptId" :min="1" style="width: 200px" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" placeholder="请选择">
            <el-option label="管理员" value="admin" />
            <el-option label="部门主管" value="manager" />
            <el-option label="员工" value="employee" />
          </el-select>
        </el-form-item>
        <el-form-item label="工具账号">
          <el-input v-model="form.toolAccounts" type="textarea" :rows="2" placeholder="JSON格式（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getUsers, saveUser, updateUser, deleteUser } from '../../api/system';

const loading = ref(false);
const submitting = ref(false);
const users = ref<any[]>([]);
const dialogVisible = ref(false);
const form = ref<any>({});

function roleType(role: string) {
  switch (role) {
    case 'admin': return 'danger';
    case 'manager': return 'warning';
    default: return 'info';
  }
}

function roleLabel(role: string) {
  switch (role) {
    case 'admin': return '管理员';
    case 'manager': return '部门主管';
    case 'employee': return '员工';
    default: return role;
  }
}

function openDialog(row: any) {
  form.value = row ? { ...row } : { username: '', email: '', deptId: 1, role: 'employee', toolAccounts: '' };
  dialogVisible.value = true;
}

async function save() {
  submitting.value = true;
  try {
    if (form.value.id) {
      await updateUser(form.value.id, form.value);
    } else {
      await saveUser(form.value);
    }
    ElMessage.success('保存成功');
    dialogVisible.value = false;
    await loadData();
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败');
  } finally {
    submitting.value = false;
  }
}

async function deleteUser(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除用户「${row.username}」？`, '提示', { type: 'warning' });
    await deleteUser(row.id);
    ElMessage.success('删除成功');
    await loadData();
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败');
  }
}

async function loadData() {
  loading.value = true;
  try {
    const res = await getUsers();
    users.value = res.data || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

onMounted(loadData);
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
