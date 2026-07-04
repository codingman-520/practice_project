import request from '../utils/request';

export const getDashboardStats = () => {
  return request.get('/dashboard/stats');
};

export const getSkillCategoryDistribution = () => {
  return request.get('/dashboard/category-distribution');
};

export const getAiCallTrend = () => {
  return request.get('/dashboard/ai-call-trend');
};

export const getRecentActivities = () => {
  return request.get('/dashboard/recent-activities');
};