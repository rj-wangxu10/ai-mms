<template>
  <div class="manager-application">
    <el-card shadow="never">
      <template #header>待审批申请</template>
      <el-table :data="pendingApps" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="applicantId" label="申请人ID" width="100" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            {{ row.type === 'additional' ? '追加额度' : '临时额度' }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额 (¥)" width="120" :formatter="fmtMoney" />
        <el-table-column prop="reason" label="理由" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="提交时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="openApprove(row, 'approve')">通过</el-button>
            <el-button type="danger" size="small" @click="openApprove(row, 'reject')">拒绝</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'approve' ? '审批通过' : '审批拒绝'" width="450px">
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
        <el-button :type="dialogMode === 'approve' ? 'success' : 'danger'" @click="doApprove" :loading="submitting">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { getApplications, approveApplication, rejectApplication } from '../../api/application';
import { useUserStore } from '../../store';

const userStore = useUserStore();
const loading = ref(false);
const submitting = ref(false);
const applications = ref<any[]>([]);
const dialogVisible = ref(false);
const dialogMode = ref<'approve' | 'reject'>('approve');
const currentApp = ref<any>(null);
const comment = ref('');

const pendingApps = computed(() =>
  applications.value.filter((a) => a.status === 'pending')
);
const processedApps = computed(() =>
  applications.value.filter((a) => a.status !== 'pending')
);

function fmtMoney(_r: any, _c: any, v: number) {
  return '¥' + Number(v || 0).toFixed(2);
}

function statusType(status: string) {
  switch (status) {
    case 'manager_approved': return 'success';
    case 'admin_approved': return 'success';
    case 'rejected': return 'danger';
    case 'pending_admin': return 'warning';
    case 'pending': return 'warning';
    default: return 'info';
  }
}

function statusLabel(status: string) {
  switch (status) {
    case 'manager_approved': return '主管已批';
    case 'admin_approved': return '已通过';
    case 'rejected': return '已拒绝';
    case 'pending_admin': return '待管理员终审';
    case 'pending': return '待审批';
    default: return status;
  }
}

function openApprove(row: any, mode: 'approve' | 'reject') {
  currentApp.value = row;
  dialogMode.value = mode;
  comment.value = '';
  dialogVisible.value = true;
}

async function doApprove() {
  if (!currentApp.value) return;
  submitting.value = true;
  try {
    if (dialogMode.value === 'approve') {
      await approveApplication(currentApp.value.id, userStore.userId, comment.value);
      ElMessage.success('已通过审批');
    } else {
      await rejectApplication(currentApp.value.id, userStore.userId, comment.value);
      ElMessage.success('已拒绝申请');
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
    const res = await getApplications({ deptId: userStore.deptId });
    applications.value = res.data || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

onMounted(loadData);

watch(() => userStore.deptId, () => loadData());
</script>
