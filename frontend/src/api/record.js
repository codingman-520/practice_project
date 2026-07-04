import request from '../utils/request';

export const getRecordList = (params = {}) => {
  return request.get('/records', { params });
};

export const deleteRecord = (id) => {
  return request.delete(`/records/${id}`);
};