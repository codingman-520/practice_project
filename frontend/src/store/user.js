import { defineStore } from 'pinia';
import { login as loginApi } from '../api/auth';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || null
  }),
  actions: {
    login(username, password) {
      return loginApi(username, password).then(res => {
        if (res.code === 200) {
          this.token = res.data.token;
          this.userInfo = res.data.userInfo;
          localStorage.setItem('token', res.data.token);
          localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo));
          return res;
        }
        throw new Error(res.message);
      });
    },
    logout() {
      this.token = '';
      this.userInfo = null;
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      return Promise.resolve();
    }
  }
});
