import { defineStore } from 'pinia';
import { login as loginApi } from '../api/auth';

export const useUserStore = defineStore('user', {
  state: () => {
    let parsedUserInfo = null;
    try {
      const userInfoStr = localStorage.getItem('userInfo');
      if (userInfoStr && userInfoStr !== 'undefined') {
        parsedUserInfo = JSON.parse(userInfoStr);
      }
    } catch (e) {
      console.error('Failed to parse userInfo from localStorage', e);
      localStorage.removeItem('userInfo');
    }
    
    return {
      token: localStorage.getItem('token') || '',
      userInfo: parsedUserInfo
    };
  },
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
