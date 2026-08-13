<template>
  <div class="manager-member">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>成员消费排名</span>
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
      <v-chart v-if="members.length" :option="barOption" style="height: 400px" v-loading="loading" />
      <el-empty v-else description="暂无数据" />
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header>成员明细</template>
      <el-table :data="members" stripe v-loading="loading">
        <el-table-column type="index" label="排名" width="70" />
        <el-table-column prop="username" label="姓名" />
        <el-table-column prop="amountCny" label="消费金额 (¥)" :formatter="fmtMoney" />
        <el-table-column prop="usedQuota" label="已用额度 (¥)" :formatter="fmtMoney" />
        <el-table-column prop="remainingQuota" label="剩余额度 (¥)" :formatter="fmtMoney" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { BarChart } from 'echarts/charts';
import { TooltipComponent, GridComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import { getManagerDashboard } from '../../api/dashboard';
import { useUserStore } from '../../store';

use([CanvasRenderer, BarChart, TooltipComponent, GridComponent]);

const userStore = useUserStore();
const loading = ref(false);
const period = ref(new Date().toISOString().slice(0, 7));
const members = ref<any[]>([]);

const barOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: members.value.map((m) => m.username),
    axisLabel: { rotate: 30 }
  },
  yAxis: { type: 'value', name: '¥' },
  series: [
    {
      type: 'bar',
      data: members.value.map((m) => Number(m.amountCny)),
      itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] },
      label: { show: true, position: 'top', formatter: '¥{c}' }
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
    members.value = res.data?.memberUsages || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
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
