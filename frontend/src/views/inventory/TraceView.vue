<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <h3 class="page-title">库存溯源</h3>
        <p class="page-desc">输入批次号、证书号或来源单号，查看库存流转轨迹。</p>
      </div>
    </div>

    <div class="toolbar">
      <el-input v-model="keyword" placeholder="请输入批次号、证书号或来源单号" clearable style="width: 320px" />
      <el-button type="primary" @click="loadData">查询溯源</el-button>
      <el-button @click="resetQuery">重置</el-button>
      <el-tag type="info">轨迹 {{ tableData.length }} 条</el-tag>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="traceType" label="轨迹类型" min-width="140" />
      <el-table-column prop="sourceType" label="来源类型" min-width="140" />
      <el-table-column prop="sourceNo" label="来源单号" min-width="180" />
      <el-table-column prop="batchNo" label="批次号" min-width="160" />
      <el-table-column prop="certificateNo" label="证书号" min-width="180" />
      <el-table-column prop="content" label="轨迹内容" min-width="220" />
      <el-table-column prop="createdAt" label="发生时间" min-width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { traceApi } from '../../api/inventory'

const keyword = ref('')
const tableData = ref([])

async function loadData() {
  if (!keyword.value) {
    tableData.value = []
    return
  }
  tableData.value = await traceApi({ keyword: keyword.value })
}

function resetQuery() {
  keyword.value = ''
  tableData.value = []
}
</script>

<style scoped>
.page-desc {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 13px;
}
</style>
