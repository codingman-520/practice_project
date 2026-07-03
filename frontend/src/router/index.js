import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '../store/user';
import Layout from '../components/Layout.vue';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/User.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'skill',
        name: 'Skill',
        component: () => import('../views/Skill.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'record',
        name: 'Record',
        component: () => import('../views/Record.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'ai-monitor',
        name: 'AiMonitor',
        component: () => import('../views/AiMonitor.vue'),
        meta: { requiresAuth: true }
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Navigation Guard
router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  if (to.matched.some(record => record.meta.requiresAuth) && !userStore.token) {
    next('/login');
  } else if (to.path === '/login' && userStore.token) {
    next('/dashboard');
  } else {
    next();
  }
});

export default router;
