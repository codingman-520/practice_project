<template>
  <div class="login-container">
    <el-card class="login-card" shadow="always">
      <div class="login-header">
        <h2>AI伴学平台</h2>
        <p>后台管理系统</p>
      </div>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="0">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="用户名" 
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码" 
            :prefix-icon="Lock"
            show-password
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            class="login-btn" 
            :loading="loading" 
            size="large"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../store/user';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();

const loginFormRef = ref(null);
const loading = ref(false);

const loginForm = ref({
  username: '',
  password: ''
});

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6位', trigger: 'blur' }
  ]
};

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return;
    loading.value = true;
    try {
      await userStore.login(loginForm.value.username, loginForm.value.password);
      ElMessage.success('登录成功');
      router.push('/dashboard');
    } catch (err) {
      ElMessage.error(err.message || '登录失败');
    } finally {
      loading.value = false;
    }
  });
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-card {
  width: 400px;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px 0;
}

.login-header p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  border-radius: 6px;
  margin-top: 10px;
}
</style>
