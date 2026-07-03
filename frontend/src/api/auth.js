export const login = (username, password) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (username === 'admin' && password === 'admin123') {
        resolve({
          code: 200,
          message: 'success',
          data: {
            token: 'mock-jwt-token-xyz123',
            userInfo: {
              id: 1,
              username: 'admin',
              nickname: '管理员',
              role: 'ADMIN',
              email: 'admin@aicompanion.com'
            }
          }
        });
      } else {
        reject({
          code: 400,
          message: '用户名或密码错误'
        });
      }
    }, 400);
  });
};
