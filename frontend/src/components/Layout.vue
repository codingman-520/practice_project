<template>
  <el-container class="layout-container">
    <!-- Sidebar -->
    <el-aside :width="appStore.isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo-container">
        <span v-show="!appStore.isCollapse">AI伴学管理平台</span>
        <span v-show="appStore.isCollapse">AI</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="layout-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :collapse="appStore.isCollapse"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>数据总览</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/skill">
          <el-icon><Share /></el-icon>
          <span>技能管理</span>
        </el-menu-item>
        <el-menu-item index="/record">
          <el-icon><Document /></el-icon>
          <span>学习记录</span>
        </el-menu-item>
        <el-menu-item index="/ai-monitor">
          <el-icon><Cpu /></el-icon>
          <span>AI服务监控</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- Header -->
      <el-header height="60px" class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="appStore.toggleSidebar">
            <component :is="appStore.isCollapse ? Expand : Fold" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-avatar-wrapper">
              <el-avatar :size="36" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="username">{{ userStore.userInfo?.nickname || '管理员' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- Main Content -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAppStore } from '../store/app';
import { useUserStore } from '../store/user';
import { ElMessage } from 'element-plus';
import {
  Odometer,
  User,
  Share,
  Document,
  Cpu,
  Expand,
  Fold
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();
const userStore = useUserStore();

const activeMenu = computed(() => route.path);

const currentRouteName = computed(() => {
  switch (route.path) {
    case '/dashboard': return '数据总览';
    case '/user': return '用户管理';
    case '/skill': return '技能管理';
    case '/record': return '学习记录';
    case '/ai-monitor': return 'AI服务监控';
    default: return '数据总览';
  }
});

const handleCommand = async (command) => {
  if (command === 'logout') {
    await userStore.logout();
    ElMessage.success('已安全退出');
    router.push('/login');
  } else if (command === 'profile') {
    ElMessage.info('个人资料暂未开放');
  }
};
</script>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo-container {
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-weight: bold;
  font-size: 16px;
  background-color: #2b3647;
  border-bottom: 1px solid #1f2d3d;
}

.layout-menu {
  border-right: none;
}

.layout-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
  color: #606266;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
  color: #606266;
}

.layout-main {
  background-color: #F5F7FA;
  padding: 20px;
  overflow-y: auto;
  box-sizing: border-box;
}
</style>
