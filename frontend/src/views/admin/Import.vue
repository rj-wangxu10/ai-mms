<template>
  <div class="admin-import">
    <el-card shadow="never">
      <template #header>账单导入</template>
      <el-form label-width="120px" style="max-width: 600px">
        <el-form-item label="选择工具">
          <el-select v-model="toolId" placeholder="请选择工具" style="width: 300px">
            <el-option v-for="t in tools" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="账单月份">
          <el-date-picker
            v-model="period"
            type="month"
            format="YYYY-MM"
            value-format="YYYY-MM"
            placeholder="选择月份"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="选择文件">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".csv,.xlsx,.xls"
            :on-change="onFileChange"
            :on-exceed="onExceed"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div style="color: #999; font-size: 12px">支持 CSV / Excel 格式账单文件</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="doImport" :loading="importing" :disabled="!toolId || !file">
            开始导入
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="importResult" shadow="never" style="margin-top: 20px">
      <template #header>导入结果</template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="总记录数">{{ importResult.total }}</el-descriptions-item>
        <el-descriptions-item label="成功">{{ importResult.success }}</el-descriptions-item>
        <el-descriptions-item label="失败">{{ importResult.failed }}</el-descriptions-item>
        <el-descriptions-item label="总金额 (¥)">¥{{ Number(importResult.totalAmount || 0).toFixed(2) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import type { UploadFile } from 'element-plus';
import { getTools } from '../../api/tool';
import { importUsage } from '../../api/usage';

const tools = ref<any[]>([]);
const toolId = ref<number | null>(null);
const period = ref(new Date().toISOString().slice(0, 7));
const file = ref<File | null>(null);
const importing = ref(false);
const importResult = ref<any>(null);

function onFileChange(uploadFile: UploadFile) {
  file.value = uploadFile.raw || null;
}

function onExceed() {
  ElMessage.warning('只能上传一个文件');
}

async function doImport() {
  if (!toolId.value || !file.value) return;
  importing.value = true;
  try {
    const res = await importUsage(toolId.value, file.value, period.value);
    importResult.value = res.data;
    ElMessage.success(`导入完成: 成功 ${res.data.success} 条`);
  } catch (e: any) {
    ElMessage.error(e.message || '导入失败');
  } finally {
    importing.value = false;
  }
}

onMounted(async () => {
  const res = await getTools();
  tools.value = res.data || [];
});
</script>
