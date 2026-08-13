<template>
  <div class="admin-dashboard">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>公司全局预算概览</span>
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
          <el-statistic title="公司总预算 (¥)" :value="dash.companyTotalBudget || 0" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="已用 (¥)" :value="dash.companyUsedBudget || 0" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="剩余 (¥)" :value="dash.companyRemainingBudget || 0" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="使用率" :value="dash.companyUsagePct || 0" suffix="%" />
        </el-col>
      </el-row>
      <el-progress
        :percentage="dash.companyUsagePct || 0"
        :color="progressColor"
        style="margin-top: 20px"
      />
    </el-card>

    <el-alert
      v-if="dash.overBudgetItems && dash.overBudgetItems.length"
      title="超预算预警"
      type="error"
      :closable="false"
      style="margin-top: 20px"
    >
      <div v-for="item in dash.overBudgetItems" :key="item.id">
        {{ item.name }} — 使用率 {{ item.usagePct }}%
      </div>
    </el-alert>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>部门预算执行率</template>
          <v-chart v-if="dash.deptUsages && dash.deptUsages.length" :option="deptBarOption" style="height: 350px" />
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>工具消费占比</template>
          <v-chart v-if="dash.toolUsages && dash.toolUsages.length" :option="toolPieOption" style="height: 350px" />
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>部门预算明细</template>
      <el-table :data="dash.deptUsages || []" stripe>
        <el-table-column prop="deptName" label="部门" />
        <el-table-column prop="monthlyBudget" label="月度预算 (¥)" :formatter="fmtMoney" />
        <el-table-column prop="usedBudget" label="已用 (¥)" :formatter="fmtMoney" />
        <el-table-column prop="usagePct" label="执行率" width="200">
          <template #default="{ row }">
            <el-progress :percentage="row.usagePct || 0" :color="row.usagePct >= 100 ? '#f56c6c' : '#409eff'" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>报表导出</template>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-button type="primary" @click="exportReport('usage', 'xlsx')">导出消费明细 (Excel)</el-button>
        </el-col>
        <el-col :span="8">
          <el-button type="success" @click="exportReport('budget', 'xlsx')">导出预算执行率 (Excel)</el-button>
        </el-col>
        <el-col :span="8">
          <el-button type="warning" @click="exportReport('tool', 'xlsx')">导出工具费用排行 (Excel)</el-button>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { PieChart, BarChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import { getAdminDashboard } from '../../api/dashboard';
import { exportReportUrl } from '../../api/report';

use([CanvasRenderer, PieChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent]);

const loading = ref(false);
const period = ref(new Date().toISOString().slice(0, 7));
const dash = ref<any>({});

const progressColor = computed(() => {
  const pct = dash.value.companyUsagePct || 0;
  if (pct >= 90) return '#f56c6c';
  if (pct >= 70) return '#e6a23c';
  return '#67c23a';
});

const deptBarOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: (dash.value.deptUsages || []).map((d: any) => d.deptName),
    axisLabel: { rotate: 30 }
  },
  yAxis: { type: 'value', name: '¥' },
  series: [
    {
      name: '预算',
      type: 'bar',
      data: (dash.value.deptUsages || []).map((d: any) => Number(d.monthlyBudget)),
      itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }
    },
    {
      name: '已用',
      type: 'bar',
      data: (dash.value.deptUsages || []).map((d: any) => Number(d.usedBudget)),
      itemStyle: { color: '#e6a23c', borderRadius: [4, 4, 0, 0] }
    }
  ]
}));

const toolPieOption = computed(() => ({
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

function fmtMoney(_r: any, _c: any, v: number) {
  return '¥' + Number(v || 0).toFixed(2);
}

async function loadData() {
  loading.value = true;
  try {
    const res = await getAdminDashboard(period.value);
    dash.value = res.data || {};
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

function exportReport(type: string, format: string) {
  const url = exportReportUrl(type, format, { period: period.value });
  window.open(url, '_blank');
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
