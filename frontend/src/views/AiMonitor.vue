<template>
  <div class="ai-monitor-container">
    <el-card class="history-card" header="面试历史运行记录">
      <el-table :data="historyList" style="width: 100%" stripe size="default" v-loading="historyLoading">
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="username" label="用户账号" width="150" />
        <el-table-column prop="jobPosition" label="目标岗位" />
        <el-table-column label="状态" width="150">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'COMPLETED' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="100">
          <template #default="scope">
            <span :class="scope.row.score >= 80 ? 'score-good' : ''">{{ scope.row.score || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="viewReport(scope.row.id)" :disabled="scope.row.status !== 'COMPLETED'">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- Markdown Diagnostic Report Dialog -->
    <el-dialog
      v-model="reportVisible"
      title="AI 模拟面试诊断报告"
      width="650px"
    >
      <div v-loading="reportLoading">
        <div class="markdown-body" v-html="formattedReport"></div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="reportVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import {
  getAdminInterviewHistory,
  getAdminInterviewReport
} from '../api/ai';

const historyList = ref([]);
const historyLoading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const reportVisible = ref(false);
const reportLoading = ref(false);
const formattedReport = ref('');

const fetchHistory = async () => {
  historyLoading.value = true;
  try {
    const res = await getAdminInterviewHistory({
      page: currentPage.value,
      size: pageSize.value
    });
    historyList.value = res.data.list;
    total.value = res.data.total;
  } catch (error) {
    ElMessage.error('加载历史记录失败');
  } finally {
    historyLoading.value = false;
  }
};

onMounted(() => {
  fetchHistory();
});

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchHistory();
};

// Simple Markdown to HTML formatter to display report nicely
const formatMarkdown = (md) => {
  if (!md) return '';
  let html = md
    .replace(/^# (.*$)/gim, '<h2>$1</h2>')
    .replace(/^### (.*$)/gim, '<h4>$1</h4>')
    .replace(/^\- \*\*(.*)\*\*：(.*$)/gim, '<li><strong>$1</strong>: $2</li>')
    .replace(/^\- (.*$)/gim, '<li>$1</li>')
    .replace(/\*\*(.*)\*\*/gim, '<strong>$1</strong>')
    .replace(/\n$/gim, '<br />')
    .replace(/---/gim, '<hr class="report-hr" />');
  return html;
};

const viewReport = async (id) => {
  reportVisible.value = true;
  reportLoading.value = true;
  try {
    const res = await getAdminInterviewReport(id);
    formattedReport.value = formatMarkdown(res.data.feedback);
  } catch (error) {
    ElMessage.error('加载诊断报告失败');
    reportVisible.value = false;
  } finally {
    reportLoading.value = false;
  }
};
</script>

<style scoped>
.ai-monitor-container {
  padding: 20px;
}

.history-card {
  border-radius: 8px;
  min-height: 500px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.score-good {
  color: #67c23a;
  font-weight: bold;
}

.markdown-body {
  line-height: 1.6;
  font-size: 14px;
  color: #303133;
}

.markdown-body h2 {
  font-size: 18px;
  font-weight: bold;
  margin-top: 0;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 8px;
}

.markdown-body h4 {
  font-size: 15px;
  margin: 15px 0 8px 0;
  color: #409EFF;
}

.markdown-body li {
  margin-bottom: 6px;
}

.report-hr {
  border: 0;
  border-top: 1px dashed #ebeef5;
  margin: 15px 0;
}
</style>
