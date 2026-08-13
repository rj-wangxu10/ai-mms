<template>
  <div class="admin-budget">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>公司年度预算</span>
          <el-button type="primary" size="small" @click="openCompanyDialog">编辑</el-button>
        </div>
      </template>
      <el-descriptions :column="4" border v-loading="loading">
        <el-descriptions-item label="财年">{{ companyBudget.fiscalYear }}</el-descriptions-item>
        <el-descriptions-item label="总预算 (¥)">{{ fmt(companyBudget.totalBudgetCny) }}</el-descriptions-item>
        <el-descriptions-item label="总预算 ($)">{{ fmt(companyBudget.totalBudgetUsd) }}</el-descriptions-item>
        <el-descriptions-item label="汇率">{{ companyBudget.exchangeRate }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>部门预算配置</span>
          <el-button type="primary" size="small" @click="openDeptDialog(null)">新增部门</el-button>
        </div>
      </template>
      <el-table :data="departments" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="部门名称" />
        <el-table-column prop="monthlyBudgetCny" label="月度预算 (¥)" :formatter="fmtMoney" />
        <el-table-column prop="usedBudgetCny" label="已用 (¥)" :formatter="fmtMoney" />
        <el-table-column prop="managerId" label="主管ID" width="100" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openDeptDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteDept(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="companyDialogVisible" title="编辑公司预算" width="500px">
      <el-form :model="companyForm" label-width="120px">
        <el-form-item label="财年">
          <el-input-number v-model="companyForm.fiscalYear" :min="2020" :max="2030" />
        </el-form-item>
        <el-form-item label="总预算 (¥)">
          <el-input-number v-model="companyForm.totalBudgetCny" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="总预算 ($)">
          <el-input-number v-model="companyForm.totalBudgetUsd" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="汇率">
          <el-input-number v-model="companyForm.exchangeRate" :min="0" :precision="4" style="width: 200px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="companyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCompany" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="deptDialogVisible" :title="deptForm.id ? '编辑部门' : '新增部门'" width="500px">
      <el-form :model="deptForm" label-width="120px">
        <el-form-item label="部门名称">
          <el-input v-model="deptForm.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="月度预算 (¥)">
          <el-input-number v-model="deptForm.monthlyBudgetCny" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="主管ID">
          <el-input-number v-model="deptForm.managerId" :min="1" style="width: 200px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deptDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDept" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getCompanyBudget, saveCompanyBudget, getDepartments, saveDepartment, updateDepartment, deleteDepartment } from '../../api/budget';

const loading = ref(false);
const submitting = ref(false);
const companyBudget = ref<any>({});
const departments = ref<any[]>([]);
const companyDialogVisible = ref(false);
const deptDialogVisible = ref(false);
const companyForm = ref<any>({});
const deptForm = ref<any>({});

function fmt(v: any) {
  return v != null ? Number(v).toFixed(2) : '0.00';
}
function fmtMoney(_r: any, _c: any, v: number) {
  return '¥' + Number(v || 0).toFixed(2);
}

function openCompanyDialog() {
  companyForm.value = { ...companyBudget.value };
  companyDialogVisible.value = true;
}

function openDeptDialog(row: any) {
  deptForm.value = row ? { ...row } : { name: '', monthlyBudgetCny: 0, managerId: 1 };
  deptDialogVisible.value = true;
}

async function saveCompany() {
  submitting.value = true;
  try {
    await saveCompanyBudget(companyForm.value);
    ElMessage.success('保存成功');
    companyDialogVisible.value = false;
    await loadData();
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败');
  } finally {
    submitting.value = false;
  }
}

async function saveDept() {
  submitting.value = true;
  try {
    if (deptForm.value.id) {
      await updateDepartment(deptForm.value.id, deptForm.value);
    } else {
      await saveDepartment(deptForm.value);
    }
    ElMessage.success('保存成功');
    deptDialogVisible.value = false;
    await loadData();
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败');
  } finally {
    submitting.value = false;
  }
}

async function deleteDept(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除部门「${row.name}」？`, '提示', { type: 'warning' });
    await deleteDepartment(row.id);
    ElMessage.success('删除成功');
    await loadData();
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败');
  }
}

async function loadData() {
  loading.value = true;
  try {
    const [companyRes, deptRes] = await Promise.all([getCompanyBudget(), getDepartments()]);
    companyBudget.value = (companyRes.data || [])[0] || {};
    departments.value = deptRes.data || [];
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
