<template>
  <div class="employee-dashboard">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的额度概览</span>
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
          <el-statistic title="总额度 (¥)" :value="dash.totalQuota || 0" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="已用 (¥)" :value="dash.usedQuota || 0" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="剩余 (¥)" :value="dash.remainingQuota || 0" :precision="2" />
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

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>各工具消费分布</template>
      <v-chart v-if="dash.toolUsages && dash.toolUsages.length" :option="pieOption" style="height: 350px" />
      <el-empty v-else description="暂无消费数据" />
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>工具消费明细</template>
      <el-table :data="dash.toolUsages || []" stripe>
        <el-table-column prop="toolName" label="工具名称" />
        <el-table-column prop="amountCny" label="消费金额 (¥)" :formatter="fmtMoney" />
        <el-table-column prop="quantity" label="使用量" :formatter="fmtQty" />
        <el-table-column prop="pct" label="占比" width="120">
          <template #default="{ row }">
            <el-progress :percentage="row.pct || 0" :stroke-width="14" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { PieChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import { getEmployeeDashboard } from '../../api/dashboard';
import { useUserStore } from '../../store';

use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent]);

const userStore = useUserStore();
const loading = ref(false);
const period = ref(new Date().toISOString().slice(0, 7));
const dash = ref<any>({});

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
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{d}%' },
      data: (dash.value.toolUsages || []).map((t: any) => ({
        name: t.toolName,
        value: Number(t.amountCny)
      }))
    }
  ]
}));

function fmtMoney(_row: any, _col: any, val: number) {
  return '¥' + Number(val || 0).toFixed(2);
}
function fmtQty(_row: any, _col: any, val: number) {
  return Number(val || 0).toFixed(2);
}

async function loadData() {
  loading.value = true;
  try {
    const res = await getEmployeeDashboard(userStore.userId, period.value);
    dash.value = res.data || {};
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

onMounted(loadData);

watch(() => userStore.userId, () => loadData());
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
