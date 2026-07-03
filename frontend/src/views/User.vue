<template>
  <div class="user-container">
    <!-- Filter Card -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryParams.role" placeholder="请选择角色" clearable style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table Card -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="tableData" style="width: 100%" stripe>
        <el-table-column prop="id" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="nickname" label="登录昵称" width="150" />
        <el-table-column prop="email" label="安全邮箱" />
        <el-table-column label="用户角色" width="160">
          <template #default="scope">
            <el-tag :type="getRoleTag(scope.row.role)">
              {{ getRoleName(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="启用状态" width="120">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="角色变更" width="180">
          <template #default="scope">
            <el-select
              v-model="scope.row.role"
              placeholder="修改角色"
              @change="(val) => handleRoleChange(scope.row, val)"
              style="width: 130px;"
            >
              <el-option label="管理员" value="ADMIN" />
              <el-option label="教师" value="TEACHER" />
              <el-option label="学生" value="STUDENT" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
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
import { Search, Refresh } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getUserList, updateUserStatus, updateUserRole } from '../api/user';

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);

const queryParams = ref({
  username: '',
  role: '',
  page: 1,
  size: 5
});

// Cache roles to allow rollback on cancel
const roleCache = new Map();

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await getUserList(queryParams.value);
    tableData.value = res.data.list;
    total.value = res.data.total;
    // Cache the roles
    tableData.value.forEach(user => {
      roleCache.set(user.id, user.role);
    });
  } catch (error) {
    ElMessage.error('加载用户列表失败');
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
    username: '',
    role: '',
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

const getRoleTag = (role) => {
  switch (role) {
    case 'ADMIN': return 'danger';
    case 'TEACHER': return '';
    case 'STUDENT': return 'success';
    default: return 'info';
  }
};

const getRoleName = (role) => {
  switch (role) {
    case 'ADMIN': return '管理员';
    case 'TEACHER': return '教师';
    case 'STUDENT': return '学生';
    default: return '未知';
  }
};

const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.status);
    ElMessage.success(`用户 [${row.username}] 状态更新成功`);
  } catch (error) {
    ElMessage.error('更新状态失败');
    row.status = row.status === 1 ? 0 : 1; // Rollback
  }
};

const handleRoleChange = (row, newRole) => {
  const oldRole = roleCache.get(row.id);
  
  ElMessageBox.confirm(
    `确定要将用户 [${row.username}] 的角色修改为 [${getRoleName(newRole)}] 吗？`,
    '角色修改确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateUserRole(row.id, newRole);
      roleCache.set(row.id, newRole); // Update cache
      ElMessage.success('用户角色修改成功');
    } catch (error) {
      ElMessage.error('修改角色失败');
      row.role = oldRole; // Rollback UI
    }
  }).catch(() => {
    // Cancelled - rollback UI
    row.role = oldRole;
  });
};
</script>

<style scoped>
.user-container {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
