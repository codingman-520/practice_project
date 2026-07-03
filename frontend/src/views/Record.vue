<template>
  <div class="record-container">
    <!-- Filter Card -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="用户ID">
          <el-input v-model="queryParams.userId" placeholder="按用户ID搜索" clearable />
        </el-form-item>
        <el-form-item label="技能名称">
          <el-input v-model="queryParams.skillName" placeholder="按技能关键字搜索" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table Card -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="tableData" style="width: 100%" stripe>
        <el-table-column prop="id" label="记录ID" width="100" />
        <el-table-column prop="userId" label="学习者用户ID" width="120" />
        <el-table-column prop="skillName" label="技能名称" width="180" />
        <el-table-column label="学习日记正文">
          <template #default="scope">
            <el-tooltip :content="scope.row.content" placement="top" :show-after="500">
              <span class="ellipsis-text">{{ scope.row.content }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="学习耗时" width="160">
          <template #default="scope">
            <span style="display: flex; align-items: center; gap: 5px;">
              <el-icon><Clock /></el-icon>
              {{ scope.row.duration }} 分钟
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Search, Refresh, Clock } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getRecordList, deleteRecord } from '../api/record';

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);

const queryParams = ref({
  userId: '',
  skillName: '',
  page: 1,
  size: 5
});

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await getRecordList(queryParams.value);
    tableData.value = res.data.list;
    total.value = res.data.total;
  } catch (error) {
    ElMessage.error('加载学习记录失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchList();
});

const handleSearch = () => {
  queryParams.value.page = 1;
  fetchList();
};

const handleReset = () => {
  queryParams.value = {
    userId: '',
    skillName: '',
    page: 1,
    size: 5
  };
  fetchList();
};

const handleSizeChange = (val) => {
  queryParams.value.size = val;
  queryParams.value.page = 1;
  fetchList();
};

const handleCurrentChange = (val) => {
  queryParams.value.page = val;
  fetchList();
};

const handleDelete = (id) => {
  ElMessageBox.confirm(
    '此操作将永久删除该学习记录，是否确定？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteRecord(id);
      ElMessage.success('删除学习记录成功');
      fetchList();
    } catch (error) {
      ElMessage.error('删除学习记录失败');
    }
  }).catch(() => {});
};
</script>

<style scoped>
.record-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-card {
  border-radius: 8px;
}

.table-card {
  border-radius: 8px;
}

.ellipsis-text {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
