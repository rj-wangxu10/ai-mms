<template>
  <div class="employee-usage">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>消费明细</span>
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
      <el-table :data="records" stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="usageDate" label="日期" width="120" />
        <el-table-column prop="toolId" label="工具ID" width="80" />
        <el-table-column prop="modelId" label="模型ID" width="80" />
        <el-table-column prop="usageQuantity" label="使用量" width="100" :formatter="fmtQty" />
        <el-table-column prop="originalAmount" label="原始金额" width="120" :formatter="fmtOrig" />
        <el-table-column prop="originalCurrency" label="币种" width="80" />
        <el-table-column prop="amountCny" label="人民币 (¥)" width="140" :formatter="fmtCny" />
        <el-table-column prop="source" label="来源" width="100" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { getUsageRecords } from '../../api/usage';
import { useUserStore } from '../../store';

const userStore = useUserStore();
const loading = ref(false);
const period = ref(new Date().toISOString().slice(0, 7));
const records = ref<any[]>([]);

function fmtQty(_r: any, _c: any, v: number) {
  return Number(v || 0).toFixed(2);
}
function fmtOrig(_r: any, _c: any, v: number) {
  return Number(v || 0).toFixed(2);
}
function fmtCny(_r: any, _c: any, v: number) {
  return '¥' + Number(v || 0).toFixed(2);
}

async function loadData() {
  loading.value = true;
  try {
    const res = await getUsageRecords({ userId: userStore.userId, period: period.value });
    records.value = res.data || [];
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
