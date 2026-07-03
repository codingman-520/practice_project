export const getDashboardStats = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        message: 'success',
        data: {
          totalUsers: 1286,
          totalSkills: 156,
          totalChats: 8432,
          totalInterviews: 23567
        }
      });
    }, 400);
  });
};

export const getSkillCategoryDistribution = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        message: 'success',
        data: [
          { value: 420, name: '前端' },
          { value: 380, name: '后端' },
          { value: 220, name: '数据库' },
          { value: 150, name: 'DevOps' },
          { value: 116, name: '软技能' }
        ]
      });
    }, 400);
  });
};

export const getAiCallTrend = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        message: 'success',
        data: {
          dates: ['06-27', '06-28', '06-29', '06-30', '07-01', '07-02', '07-03'],
          calls: [2800, 3100, 3000, 3300, 3500, 3800, 4067]
        }
      });
    }, 400);
  });
};

export const getRecentActivities = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        message: 'success',
        data: [
          { id: 1, username: 'student01', actionType: '点亮技能：Vue 3 基础', triggerTime: '2026-07-03 22:15:00' },
          { id: 2, username: 'student02', actionType: '发起模拟面试：Java核心', triggerTime: '2026-07-03 22:10:00' },
          { id: 3, username: 'student03', actionType: '提问AI：如何处理跨域', triggerTime: '2026-07-03 22:05:00' },
          { id: 4, username: 'student04', actionType: '点亮技能：Spring Boot 入门', triggerTime: '2026-07-03 21:55:00' },
          { id: 5, username: 'student05', actionType: '上传简历进行诊断', triggerTime: '2026-07-03 21:50:00' }
        ]
      });
    }, 400);
  });
};
