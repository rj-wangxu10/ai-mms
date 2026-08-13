<template>
  <div class="employee-application">
    <el-card shadow="never">
      <template #header>提交额度申请</template>
      <el-form :model="form" label-width="100px" style="max-width: 500px">
        <el-form-item label="申请类型">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="追加额度" value="additional" />
            <el-option label="临时额度" value="temporary" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请金额">
          <el-input-number v-model="form.amount" :min="1" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请说明申请理由" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit" :loading="submitting">提交申请</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>我的申请记录</template>
      <el-table :data="applications" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            {{ row.type === 'additional' ? '追加额度' : '临时额度' }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额 (¥)" width="120" :formatter="fmtMoney" />
        <el-table-column prop="reason" label="理由" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveComment" label="审批意见" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="提交时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { getApplications, submitApplication } from '../../api/application';
import { useUserStore } from '../../store';

const userStore = useUserStore();
const loading = ref(false);
const submitting = ref(false);
const applications = ref<any[]>([]);
const form = ref({
  type: 'additional',
  amount: 100,
  reason: ''
});

function fmtMoney(_r: any, _c: any, v: number) {
  return '¥' + Number(v || 0).toFixed(2);
}

function statusType(status: string) {
  switch (status) {
    case 'manager_approved': return 'success';
    case 'admin_approved': return 'success';
    case 'rejected': return 'danger';
    case 'pending': return 'warning';
    case 'pending_admin': return 'warning';
    default: return 'info';
  }
}

function statusLabel(status: string) {
  switch (status) {
    case 'manager_approved': return '主管已批';
    case 'admin_approved': return '已通过';
    case 'rejected': return '已拒绝';
    case 'pending': return '待主管审批';
    case 'pending_admin': return '待管理员审批';
    default: return status;
  }
}

async function loadData() {
  loading.value = true;
  try {
    const res = await getApplications({});
    applications.value = (res.data || []).filter((a: any) => a.applicantId === userStore.userId);
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

async function submit() {
  if (!form.value.reason) {
    ElMessage.warning('请填写申请理由');
    return;
  }
  submitting.value = true;
  try {
    await submitApplication({
      applicantId: userStore.userId,
      deptId: userStore.deptId,
      type: form.value.type,
      amount: form.value.amount,
      reason: form.value.reason
    });
    ElMessage.success('申请已提交');
    form.value.reason = '';
    await loadData();
  } catch (e: any) {
    ElMessage.error(e.message || '提交失败');
  } finally {
    submitting.value = false;
  }
}

onMounted(loadData);

watch(() => userStore.userId, () => loadData());
</script>
