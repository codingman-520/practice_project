import request from '../utils/request';

export const sendChatMessage = (message) => {
  // 后端 DifyChatRequestDTO 期望的字段是 query
  return request.post('/ai/chat', { query: message });
};

export const startMockInterview = async (skillId) => {
  // 模拟整个面试流程，对接后端的真实单步 API
  // 1. 开始面试
  const startRes = await request.post('/interview/start', { jobPosition: skillId });
  const sessionId = startRes.data.sessionId;
  
  // 2. 连续回答3道题（自动填入模拟答案以触发评估）
  let isCompleted = false;
  while (!isCompleted) {
    const ansRes = await request.post('/interview/answer', {
      sessionId: sessionId,
      answer: '这是一个测试沙箱提供的模拟答案，用于触发AI评估流程。'
    });
    isCompleted = ansRes.data.isCompleted;
  }
  
  // 3. 获取评估报告
  const reportRes = await request.get(`/interview/report/${sessionId}`);
  
  // 包装成页面期望的格式
  return {
    code: 200,
    message: 'success',
    data: {
      score: reportRes.data.score,
      feedback: reportRes.data.feedback,
      status: 'COMPLETED'
    }
  };
};

export const getAdminInterviewHistory = (params) => {
  return request.get('/admin/interviews/history', { params });
};

export const getAdminInterviewReport = (sessionId) => {
  return request.get(`/admin/interviews/report/${sessionId}`);
};