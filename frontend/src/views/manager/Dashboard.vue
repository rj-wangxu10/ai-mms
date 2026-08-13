<template>
  <div class="manager-dashboard">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>部门预算概览 — {{ dash.deptName }}</span>
          <el-date-picker
            v-model="period"
            type="month"
            format="YYYY-MM"
            value-format="YYYY-MM"
            placeholder="选择月份"
            @change="loadData"
            style="width: 160px"
          />
        </div>
      </template>
      <el-row :gutter="20" v-loading="loading">
        <el-col :span="6">
          <el-statistic title="月度预算 (¥)" :value="dash.monthlyBudget || 0" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="已用 (¥)" :value="dash.usedBudget || 0" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="剩余 (¥)" :value="dash.remainingBudget || 0" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="使用率" :value="dash.usagePct || 0" suffix="%" />
        </el-col>
      </el-row>
      <el-progress
        :percentage="dash.usagePct || 0"
        :color="progressColor"
        style="margin-top: 20px"
      />
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>工具消费分布</template>
          <v-chart v-if="dash.toolUsages && dash.toolUsages.length" :option="pieOption" style="height: 300px" />
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>成员消费排名</template>
          <v-chart v-if="dash.memberUsages && dash.memberUsages.length" :option="barOption" style="height: 300px" />
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>成员消费明细</span>
          <el-button type="primary" size="small" @click="showAllocateDialog">批量分配额度</el-button>
        </div>
      </template>
      <el-table :data="dash.memberUsages || []" stripe>
        <el-table-column type="selection" width="50" />
        <el-table-column type="index" label="排名" width="70" />
        <el-table-column prop="username" label="姓名" />
        <el-table-column prop="amountCny" label="消费金额 (¥)" :formatter="fmtMoney" />
        <el-table-column prop="usedQuota" label="已用额度 (¥)" :formatter="fmtMoney" />
        <el-table-column prop="remainingQuota" label="剩余额度 (¥)" :formatter="fmtMoney" />
      </el-table>
    </el-card>

    <el-dialog v-model="allocateDialogVisible" title="批量分配月度基础额度" width="500px">
      <el-form :model="allocateForm" label-width="120px">
        <el-form-item label="月份">
          <el-input v-model="allocateForm.period" placeholder="如 2026-08" />
        </el-form-item>
        <el-form-item label="每人额度 (¥)">
          <el-input-number v-model="allocateForm.amountPerUser" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="选择成员">
          <el-select v-model="allocateForm.userIds" multiple placeholder="选择成员" style="width: 100%">
            <el-option
              v-for="m in (dash.memberUsages || [])"
              :key="m.userId"
              :label="m.username"
              :value="m.userId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="allocateDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="allocating" @click="doAllocate">确认分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { PieChart, BarChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import { getManagerDashboard } from '../../api/dashboard';
import { batchAllocateQuota } from '../../api/quota';
import { useUserStore } from '../../store';
import { ElMessage } from 'element-plus';

use([CanvasRenderer, PieChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent]);

const userStore = useUserStore();
const loading = ref(false);
const period = ref(new Date().toISOString().slice(0, 7));
const dash = ref<any>({});
const allocateDialogVisible = ref(false);
const allocating = ref(false);
const allocateForm = ref({
  period: new Date().toISOString().slice(0, 7),
  amountPerUser: 1000,
  userIds: [] as number[]
});

const progressColor = computed(() => {
  const pct = dash.value.usagePct || 0;
  if (pct >= 90) return '#f56c6c';
  if (pct >= 70) return '#e6a23c';
  return '#67c23a';
});

const pieOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
  legend: { bottom: 0 },
  series: [
    {
      type: 'pie',
      radius: ['40%', '70%'],
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{d}%' },
      data: (dash.value.toolUsages || []).map((t: any) => ({
        name: t.toolName,
        value: Number(t.amountCny)
      }))
    }
  ]
}));

const barOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: (dash.value.memberUsages || []).map((m: any) => m.username),
    axisLabel: { rotate: 30 }
  },
  yAxis: { type: 'value', name: '¥' },
  series: [
    {
      type: 'bar',
      data: (dash.value.memberUsages || []).map((m: any) => Number(m.amountCny)),
      itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }
    }
  ]
}));

function fmtMoney(_r: any, _c: any, v: number) {
  return '¥' + Number(v || 0).toFixed(2);
}

async function loadData() {
  loading.value = true;
  try {
    const res = await getManagerDashboard(userStore.deptId, period.value);
    dash.value = res.data || {};
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

function showAllocateDialog() {
  allocateForm.value.period = period.value;
  allocateForm.value.userIds = (dash.value.memberUsages || []).map((m: any) => m.userId);
  allocateDialogVisible.value = true;
}

async function doAllocate() {
  if (allocateForm.value.userIds.length === 0) {
    ElMessage.warning('请选择至少一名成员');
    return;
  }
  allocating.value = true;
  try {
    await batchAllocateQuota({
      deptId: userStore.deptId,
      userIds: allocateForm.value.userIds,
      period: allocateForm.value.period,
      amountPerUser: allocateForm.value.amountPerUser
    });
    ElMessage.success('额度分配成功');
    allocateDialogVisible.value = false;
    loadData();
  } catch (e: any) {
    ElMessage.error(e.message || '分配失败');
  } finally {
    allocating.value = false;
  }
}

onMounted(loadData);

watch(() => userStore.deptId, () => loadData());
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
