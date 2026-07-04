import request from '../utils/request';

export const getUserList = (params = {}) => {
  return request.get('/users', { params });
};

export const updateUserRole = (userId, role) => {
  return request.put(`/users/${userId}/role`, { role });
};

export const updateUserStatus = (userId, status) => {
  return request.put(`/users/${userId}/status`, { status });
};