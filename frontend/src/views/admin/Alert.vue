<template>
  <div class="admin-alert">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>预警规则配置</span>
          <el-button type="primary" size="small" @click="openRuleDialog(null)">新增规则</el-button>
        </div>
      </template>
      <el-table :data="rules" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="targetType" label="目标类型" width="120">
          <template #default="{ row }">
            {{ targetTypeLabel(row.targetType) }}
          </template>
        </el-table-column>
        <el-table-column prop="targetId" label="目标ID" width="100" />
        <el-table-column prop="thresholdPct" label="阈值 (%)" width="120" />
        <el-table-column prop="notifyRoles" label="通知角色" />
        <el-table-column prop="enabled" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openRuleDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteRule(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>预警日志</span>
          <el-button type="warning" size="small" @click="checkAlerts" :loading="checking">手动触发检查</el-button>
        </div>
      </template>
      <el-table :data="logs" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="targetType" label="目标类型" width="120">
          <template #default="{ row }">
            {{ targetTypeLabel(row.targetType) }}
          </template>
        </el-table-column>
        <el-table-column prop="targetId" label="目标ID" width="100" />
        <el-table-column prop="actualPct" label="实际使用率 (%)" width="140" />
        <el-table-column prop="message" label="预警消息" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="触发时间" width="180" />
      </el-table>
    </el-card>

    <el-dialog v-model="ruleDialogVisible" :title="ruleForm.id ? '编辑规则' : '新增规则'" width="500px">
      <el-form :model="ruleForm" label-width="120px">
        <el-form-item label="目标类型">
          <el-select v-model="ruleForm.targetType" placeholder="请选择">
            <el-option label="个人" value="user" />
            <el-option label="部门" value="department" />
            <el-option label="公司" value="company" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标ID">
          <el-input-number v-model="ruleForm.targetId" :min="1" style="width: 200px" />
        </el-form-item>
        <el-form-item label="阈值 (%)">
          <el-input-number v-model="ruleForm.thresholdPct" :min="1" :max="200" style="width: 200px" />
        </el-form-item>
        <el-form-item label="通知角色">
          <el-input v-model="ruleForm.notifyRoles" placeholder="如: admin,manager" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="ruleForm.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ruleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRule" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getAlertRules, saveAlertRule, deleteAlertRule, getAlertLogs, checkAlert } from '../../api/alert';

const loading = ref(false);
const submitting = ref(false);
const checking = ref(false);
const rules = ref<any[]>([]);
const logs = ref<any[]>([]);
const ruleDialogVisible = ref(false);
const ruleForm = ref<any>({});

function targetTypeLabel(type: string) {
  switch (type) {
    case 'user': return '个人';
    case 'department': return '部门';
    case 'company': return '公司';
    default: return type;
  }
}

function openRuleDialog(row: any) {
  ruleForm.value = row ? { ...row } : { targetType: 'user', targetId: 1, thresholdPct: 80, notifyRoles: 'admin,manager', enabled: true };
  ruleDialogVisible.value = true;
}

async function saveRule() {
  submitting.value = true;
  try {
    await saveAlertRule(ruleForm.value);
    ElMessage.success('保存成功');
    ruleDialogVisible.value = false;
    await loadRules();
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败');
  } finally {
    submitting.value = false;
  }
}

async function deleteRule(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除此预警规则？`, '提示', { type: 'warning' });
    await deleteAlertRule(row.id);
    ElMessage.success('删除成功');
    await loadRules();
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败');
  }
}

async function checkAlerts() {
  checking.value = true;
  try {
    await checkAlert();
    ElMessage.success('检查完成');
    await loadLogs();
  } catch (e: any) {
    ElMessage.error(e.message || '检查失败');
  } finally {
    checking.value = false;
  }
}

async function loadRules() {
  loading.value = true;
  try {
    const res = await getAlertRules();
    rules.value = res.data || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

async function loadLogs() {
  try {
    const res = await getAlertLogs();
    logs.value = res.data || [];
  } catch (e) {
    console.error(e);
  }
}

onMounted(async () => {
  await loadRules();
  await loadLogs();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
