<template>
  <div class="ai-monitor-container">
    <el-row :gutter="16">
      <!-- Left Panel: AI Chat Dialog -->
      <el-col :span="13">
        <el-card class="chat-card">
          <template #header>
            <div class="chat-header">
              <span class="chat-title">AI 对话模拟沙箱</span>
              <span class="status-indicator">
                <span class="breathing-light"></span>
                在线
              </span>
            </div>
          </template>

          <div class="chat-messages" ref="chatMessagesRef">
            <div
              v-for="(msg, index) in chatList"
              :key="index"
              :class="['message-item', msg.role === 'user' ? 'message-user' : 'message-ai']"
            >
              <div class="message-bubble">
                <div v-if="msg.loading" class="skeleton-loader">
                  <span>AI正在检索解析</span>
                  <span class="dot-flashing"></span>
                </div>
                <div v-else class="message-text">{{ msg.text }}</div>
                <div class="message-time">{{ msg.time }}</div>
              </div>
            </div>
          </div>

          <div class="chat-input-area">
            <el-input
              v-model="inputMsg"
              placeholder="请输入测试消息，按回车发送..."
              @keyup.enter="handleSend"
              :disabled="chatSending"
            >
              <template #append>
                <el-button type="primary" :loading="chatSending" @click="handleSend">发送</el-button>
              </template>
            </el-input>
          </div>
        </el-card>
      </el-col>

      <!-- Right Panel -->
      <el-col :span="11">
        <div class="right-panel-wrapper">
          <!-- Top Right: Mock Interview Trigger Sandbox -->
          <el-card class="sandbox-card" header="模拟面试在线触发沙箱">
            <div class="interview-trigger">
              <el-form :inline="true" class="demo-form-inline">
                <el-form-item label="测试技能分类">
                  <el-select v-model="testSkillId" placeholder="选择技能" style="width: 160px;">
                    <el-option label="前端开发" value="FRONTEND" />
                    <el-option label="后端开发" value="BACKEND" />
                    <el-option label="职业软技能" value="SOFT_SKILLS" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="success" :loading="interviewing" @click="handleTriggerInterview">开始模拟测试</el-button>
                </el-form-item>
              </el-form>

              <!-- Result Showcase -->
              <div v-if="interviewResult" class="interview-result-box">
                <div class="result-header">测试评估结果</div>
                <div class="result-body">
                  <div class="result-item">
                    <span class="res-label">面试得分：</span>
                    <span class="res-val score-high">{{ interviewResult.score }} 分</span>
                  </div>
                  <div class="result-item">
                    <span class="res-label">评估状态：</span>
                    <span class="res-val"><el-tag type="success">COMPLETED</el-tag></span>
                  </div>
                  <div class="result-item">
                    <span class="res-label">诊断反馈：</span>
                    <span class="res-val">{{ interviewResult.feedback }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- Bottom Right: Interview History -->
          <el-card class="history-card" header="面试历史运行记录">
            <el-table :data="historyList" style="width: 100%" stripe size="small" v-loading="historyLoading">
              <el-table-column prop="username" label="用户" width="90" />
              <el-table-column prop="jobPosition" label="目标岗位" />
              <el-table-column label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 'COMPLETED' ? 'success' : 'warning'" size="small">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="score" label="得分" width="70">
                <template #default="scope">
                  <span :class="scope.row.score >= 80 ? 'score-good' : ''">{{ scope.row.score || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="90" fixed="right">
                <template #default="scope">
                  <el-button link type="primary" size="small" @click="viewReport(scope.row.id)" :disabled="scope.row.status !== 'COMPLETED'">查看详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-col>
    </el-row>

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
import { ref, onMounted, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import {
  sendChatMessage,
  startMockInterview,
  getInterviewHistory,
  getInterviewReport
} from '../api/ai';

const chatMessagesRef = ref(null);

const chatList = ref([
  { role: 'ai', text: '您好！我是您的 AI 智能伴学助手，有什么可以帮您解答的吗？比如您可以输入“如何学习 Vue”或“什么是 Docker”。', time: new Date().toLocaleTimeString(), loading: false }
]);
const inputMsg = ref('');
const chatSending = ref(false);

const testSkillId = ref('FRONTEND');
const interviewing = ref(false);
const interviewResult = ref(null);

const historyList = ref([]);
const historyLoading = ref(false);

const reportVisible = ref(false);
const reportLoading = ref(false);
const rawReport = ref('');
const formattedReport = ref('');

const fetchHistory = async () => {
  historyLoading.value = true;
  try {
    const res = await getInterviewHistory();
    historyList.value = res.data;
  } catch (error) {
    ElMessage.error('加载历史记录失败');
  } finally {
    historyLoading.value = false;
  }
};

onMounted(() => {
  fetchHistory();
});

const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight;
    }
  });
};

const handleSend = async () => {
  if (!inputMsg.value.trim()) return;
  const userText = inputMsg.value;
  inputMsg.value = '';
  chatSending.value = true;

  // Append user bubble
  chatList.value.push({
    role: 'user',
    text: userText,
    time: new Date().toLocaleTimeString(),
    loading: false
  });
  scrollToBottom();

  // Append thinking bubble
  const aiThinkingIndex = chatList.value.push({
    role: 'ai',
    text: '',
    time: new Date().toLocaleTimeString(),
    loading: true
  }) - 1;
  scrollToBottom();

  try {
    const res = await sendChatMessage(userText);
    chatList.value[aiThinkingIndex] = {
      role: 'ai',
      text: res.data.reply,
      time: res.data.timestamp || new Date().toLocaleTimeString(),
      loading: false
    };
  } catch (error) {
    chatList.value[aiThinkingIndex] = {
      role: 'ai',
      text: '网络异常，请重试。',
      time: new Date().toLocaleTimeString(),
      loading: false
    };
  } finally {
    chatSending.value = false;
    scrollToBottom();
  }
};

const handleTriggerInterview = async () => {
  interviewing.value = true;
  interviewResult.value = null;
  try {
    const res = await startMockInterview(testSkillId.value);
    interviewResult.value = res.data;
    ElMessage.success('模拟面试评估完成！');
    fetchHistory(); // Refresh history
  } catch (error) {
    ElMessage.error('模拟面试测试失败');
  } finally {
    interviewing.value = false;
  }
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
    const res = await getInterviewReport(id);
    rawReport.value = res.data.report;
    formattedReport.value = formatMarkdown(res.data.report);
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
  display: flex;
  flex-direction: column;
}

.chat-card {
  border-radius: 8px;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-title {
  font-weight: bold;
  color: #303133;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #67C23A;
}

.breathing-light {
  width: 8px;
  height: 8px;
  background-color: #67C23A;
  border-radius: 50%;
  box-shadow: 0 0 8px #67C23A;
  animation: breathe 2s infinite ease-in-out;
}

@keyframes breathe {
  0%, 100% {
    opacity: 0.4;
    transform: scale(0.9);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
}

.chat-messages {
  height: 400px;
  overflow-y: auto;
  background-color: #f5f7f8;
  padding: 15px;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 15px;
}

.message-item {
  display: flex;
  width: 100%;
}

.message-user {
  justify-content: flex-end;
}

.message-ai {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 12px;
  position: relative;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.message-user .message-bubble {
  background-color: #409EFF;
  color: #ffffff;
  border-bottom-right-radius: 2px;
}

.message-ai .message-bubble {
  background-color: #ffffff;
  color: #303133;
  border-bottom-left-radius: 2px;
}

.message-text {
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.message-time {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.35);
  margin-top: 6px;
  text-align: right;
}

.message-user .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.chat-input-area {
  margin-top: 10px;
}

.right-panel-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sandbox-card {
  border-radius: 8px;
}

.interview-trigger {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.interview-trigger .el-form-item {
  margin-bottom: 0;
}

.interview-result-box {
  background-color: #fcfcfd;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 15px;
  margin-top: 10px;
}

.result-header {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
  border-bottom: 1px solid #f2f6fc;
  padding-bottom: 8px;
  margin-bottom: 10px;
}

.result-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.result-item {
  font-size: 14px;
  line-height: 1.4;
}

.res-label {
  color: #909399;
  font-weight: 500;
}

.res-val {
  color: #303133;
}

.score-high {
  color: #e6a23c;
  font-weight: bold;
  font-size: 18px;
}

.score-good {
  color: #67c23a;
  font-weight: bold;
}

.history-card {
  border-radius: 8px;
}

/* Skeleton Loading Animation */
.dot-flashing {
  position: relative;
  width: 6px;
  height: 6px;
  border-radius: 5px;
  background-color: #409eff;
  color: #409eff;
  animation: dot-flashing 1s infinite linear;
  display: inline-block;
  margin-left: 8px;
}

@keyframes dot-flashing {
  0% {
    background-color: #409eff;
  }
  50%, 100% {
    background-color: rgba(64, 159, 255, 0.2);
  }
}

.skeleton-loader {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #909399;
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
