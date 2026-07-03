let users = [
  { id: 1, username: 'admin', nickname: '系统管理员', email: 'admin@aicompanion.com', role: 'ADMIN', status: 1, createTime: '2026-06-01 12:00:00' },
  { id: 2, username: 'teacher_wang', nickname: '王老师', email: 'wang@aicompanion.com', role: 'TEACHER', status: 1, createTime: '2026-06-05 14:30:00' },
  { id: 3, username: 'student01', nickname: '小明', email: 'student01@aicompanion.com', role: 'STUDENT', status: 1, createTime: '2026-06-10 09:15:00' },
  { id: 4, username: 'student02', nickname: '小红', email: 'student02@aicompanion.com', role: 'STUDENT', status: 1, createTime: '2026-06-11 10:20:00' },
  { id: 5, username: 'student03', nickname: '小华', email: 'student03@aicompanion.com', role: 'STUDENT', status: 0, createTime: '2026-06-12 11:30:00' },
  { id: 6, username: 'student04', nickname: '小强', email: 'student04@aicompanion.com', role: 'STUDENT', status: 1, createTime: '2026-06-15 15:45:00' },
  { id: 7, username: 'student05', nickname: '小丽', email: 'student05@aicompanion.com', role: 'STUDENT', status: 1, createTime: '2026-06-16 16:50:00' }
];

export const getUserList = (params = {}) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      let filtered = [...users];
      if (params.username) {
        filtered = filtered.filter(u => u.username.includes(params.username) || u.nickname.includes(params.username));
      }
      if (params.role) {
        filtered = filtered.filter(u => u.role === params.role);
      }
      
      const page = params.page || 1;
      const size = params.size || 5;
      const total = filtered.length;
      const start = (page - 1) * size;
      const end = start + size;
      const list = filtered.slice(start, end);

      resolve({
        code: 200,
        message: 'success',
        data: {
          list,
          total
        }
      });
    }, 400);
  });
};

export const updateUserRole = (userId, role) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const user = users.find(u => u.id === userId);
      if (user) {
        user.role = role;
      }
      resolve({
        code: 200,
        message: 'success'
      });
    }, 400);
  });
};

export const updateUserStatus = (userId, status) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const user = users.find(u => u.id === userId);
      if (user) {
        user.status = status;
      }
      resolve({
        code: 200,
        message: 'success'
      });
    }, 400);
  });
};
