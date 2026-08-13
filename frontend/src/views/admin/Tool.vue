<template>
  <div class="admin-tool">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>AI 工具管理</span>
          <el-button type="primary" size="small" @click="openToolDialog(null)">新增工具</el-button>
        </div>
      </template>
      <el-table :data="tools" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="工具名称" />
        <el-table-column prop="billingMode" label="计费模式" width="120" />
        <el-table-column prop="currency" label="币种" width="80" />
        <el-table-column prop="syncType" label="同步方式" width="120" />
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button size="small" @click="openModelDialog(row)">模型管理</el-button>
            <el-button size="small" @click="openToolDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteTool(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="toolDialogVisible" :title="toolForm.id ? '编辑工具' : '新增工具'" width="500px">
      <el-form :model="toolForm" label-width="100px">
        <el-form-item label="工具名称">
          <el-input v-model="toolForm.name" placeholder="如: OpenAI" />
        </el-form-item>
        <el-form-item label="计费模式">
          <el-select v-model="toolForm.billingMode" placeholder="请选择">
            <el-option label="按量计费" value="usage" />
            <el-option label="套餐计费" value="subscription" />
            <el-option label="混合计费" value="hybrid" />
          </el-select>
        </el-form-item>
        <el-form-item label="币种">
          <el-select v-model="toolForm.currency" placeholder="请选择">
            <el-option label="CNY" value="CNY" />
            <el-option label="USD" value="USD" />
          </el-select>
        </el-form-item>
        <el-form-item label="同步方式">
          <el-select v-model="toolForm.syncType" placeholder="请选择">
            <el-option label="CSV导入" value="csv" />
            <el-option label="API同步" value="api" />
          </el-select>
        </el-form-item>
        <el-form-item label="同步配置">
          <el-input v-model="toolForm.syncConfig" type="textarea" :rows="2" placeholder="JSON配置（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="toolDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTool" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="modelDialogVisible" :title="`模型管理 — ${currentTool?.name}`" width="700px">
      <div style="margin-bottom: 12px">
        <el-button type="primary" size="small" @click="openModelForm(null)">新增模型</el-button>
      </div>
      <el-table :data="models" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="模型名称" />
        <el-table-column prop="unitPrice" label="单价" width="120" :formatter="fmtPrice" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openModelForm(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteModel(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="modelFormDialogVisible" :title="modelForm.id ? '编辑模型' : '新增模型'" width="450px" append-to-body>
      <el-form :model="modelForm" label-width="100px">
        <el-form-item label="模型名称">
          <el-input v-model="modelForm.name" placeholder="如: gpt-4" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="modelForm.unitPrice" :min="0" :precision="6" style="width: 200px" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="modelForm.unit" placeholder="如: 1K tokens" />
        </el-form-item>
        <el-form-item label="阶梯定价">
          <el-input v-model="modelForm.tieredPricing" type="textarea" :rows="2" placeholder="JSON（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="modelFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveModel" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getTools, saveTool, updateTool, deleteTool, getModels, saveModel, updateModel, deleteModel } from '../../api/tool';

const loading = ref(false);
const submitting = ref(false);
const tools = ref<any[]>([]);
const toolDialogVisible = ref(false);
const toolForm = ref<any>({});
const modelDialogVisible = ref(false);
const modelFormDialogVisible = ref(false);
const currentTool = ref<any>(null);
const models = ref<any[]>([]);
const modelForm = ref<any>({});

function fmtPrice(_r: any, _c: any, v: number) {
  return Number(v || 0).toFixed(6);
}

function openToolDialog(row: any) {
  toolForm.value = row ? { ...row } : { name: '', billingMode: 'usage', currency: 'USD', syncType: 'csv', syncConfig: '' };
  toolDialogVisible.value = true;
}

async function saveTool() {
  submitting.value = true;
  try {
    if (toolForm.value.id) {
      await updateTool(toolForm.value.id, toolForm.value);
    } else {
      await saveTool(toolForm.value);
    }
    ElMessage.success('保存成功');
    toolDialogVisible.value = false;
    await loadTools();
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败');
  } finally {
    submitting.value = false;
  }
}

async function deleteTool(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除工具「${row.name}」？`, '提示', { type: 'warning' });
    await deleteTool(row.id);
    ElMessage.success('删除成功');
    await loadTools();
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败');
  }
}

async function openModelDialog(tool: any) {
  currentTool.value = tool;
  modelDialogVisible.value = true;
  const res = await getModels(tool.id);
  models.value = res.data || [];
}

function openModelForm(row: any) {
  modelForm.value = row ? { ...row } : { name: '', unitPrice: 0, unit: '', tieredPricing: '' };
  modelFormDialogVisible.value = true;
}

async function saveModel() {
  submitting.value = true;
  try {
    if (modelForm.value.id) {
      await updateModel(modelForm.value.id, modelForm.value);
    } else {
      await saveModel(currentTool.value.id, modelForm.value);
    }
    ElMessage.success('保存成功');
    modelFormDialogVisible.value = false;
    const res = await getModels(currentTool.value.id);
    models.value = res.data || [];
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败');
  } finally {
    submitting.value = false;
  }
}

async function deleteModel(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除模型「${row.name}」？`, '提示', { type: 'warning' });
    await deleteModel(row.id);
    ElMessage.success('删除成功');
    const res = await getModels(currentTool.value.id);
    models.value = res.data || [];
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败');
  }
}

async function loadTools() {
  loading.value = true;
  try {
    const res = await getTools();
    tools.value = res.data || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

onMounted(loadTools);
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
