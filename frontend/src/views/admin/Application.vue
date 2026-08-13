<template>
  <div class="admin-application">
    <el-card shadow="never">
      <template #header>待终审申请（大额升级）</template>
      <el-table :data="pendingAdminApps" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="applicantId" label="申请人ID" width="100" />
        <el-table-column prop="deptId" label="部门ID" width="100" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            {{ row.type === 'additional' ? '追加额度' : '临时额度' }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额 (¥)" width="120" :formatter="fmtMoney" />
        <el-table-column prop="reason" label="理由" show-overflow-tooltip />
        <el-table-column prop="approveComment" label="主管意见" show-overflow-tooltip />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="openDialog(row, 'approve')">终审通过</el-button>
            <el-button type="danger" size="small" @click="openDialog(row, 'reject')">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>已处理申请</template>
      <el-table :data="processedApps" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="applicantId" label="申请人ID" width="100" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            {{ row.type === 'additional' ? '追加额度' : '临时额度' }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额 (¥)" width="120" :formatter="fmtMoney" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveComment" label="审批意见" show-overflow-tooltip />
        <el-table-column prop="updatedAt" label="处理时间" width="180" />
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'approve' ? '终审通过' : '拒绝申请'" width="450px">
      <el-form label-width="80px">
        <el-form-item label="申请ID">
          <span>{{ currentApp?.id }}</span>
        </el-form-item>
        <el-form-item label="金额">
          <span>¥{{ currentApp?.amount }}</span>
        </el-form-item>
        <el-form-item label="理由">
          <span>{{ currentApp?.reason }}</span>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="comment" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button :type="dialogMode === 'approve' ? 'success' : 'danger'" @click="doAction" :loading="submitting">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getApplications, adminApproveApplication, rejectApplication } from '../../api/application';
import { useUserStore } from '../../store';

const userStore = useUserStore();
const loading = ref(false);
const submitting = ref(false);
const applications = ref<any[]>([]);
const dialogVisible = ref(false);
const dialogMode = ref<'approve' | 'reject'>('approve');
const currentApp = ref<any>(null);
const comment = ref('');

const pendingAdminApps = computed(() =>
  applications.value.filter((a) => a.status === 'pending_admin')
);
const processedApps = computed(() =>
  applications.value.filter((a) => a.status === 'manager_approved' || a.status === 'admin_approved' || a.status === 'rejected')
);

function fmtMoney(_r: any, _c: any, v: number) {
  return '¥' + Number(v || 0).toFixed(2);
}

function statusType(status: string) {
  switch (status) {
    case 'admin_approved': return 'success';
    case 'manager_approved': return 'success';
    case 'rejected': return 'danger';
    default: return 'info';
  }
}

function statusLabel(status: string) {
  switch (status) {
    case 'admin_approved': return '已通过';
    case 'manager_approved': return '主管已批';
    case 'rejected': return '已拒绝';
    default: return status;
  }
}

function openDialog(row: any, mode: 'approve' | 'reject') {
  currentApp.value = row;
  dialogMode.value = mode;
  comment.value = '';
  dialogVisible.value = true;
}

async function doAction() {
  if (!currentApp.value) return;
  submitting.value = true;
  try {
    if (dialogMode.value === 'approve') {
      await adminApproveApplication(currentApp.value.id, userStore.userId, comment.value);
      ElMessage.success('终审通过');
    } else {
      await rejectApplication(currentApp.value.id, userStore.userId, comment.value);
      ElMessage.success('已拒绝');
    }
    dialogVisible.value = false;
    await loadData();
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败');
  } finally {
    submitting.value = false;
  }
}

async function loadData() {
  loading.value = true;
  try {
    const res = await getApplications({});
    applications.value = res.data || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

onMounted(loadData);
</script>
