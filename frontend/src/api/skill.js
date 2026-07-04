import request from '../utils/request';

export const getSkillTree = () => {
  return request.get('/skills/tree');
};

export const addSkillNode = (parentId, nodeData) => {
  return request.post('/skills', { parentId, ...nodeData });
};

export const updateSkillNode = (id, nodeData) => {
  return request.put(`/skills/${id}`, nodeData);
};

export const deleteSkillNode = (id) => {
  return request.delete(`/skills/${id}`);
};