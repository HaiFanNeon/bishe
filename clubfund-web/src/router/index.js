import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Layout.vue'

const routes = [
  // 登录页 (不需要 Layout)
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  
  // 主布局路由
  {
    path: '/',
    component: Layout,
    redirect: '/login', // 默认跳登录，或者根据逻辑跳首页
    children: [
      // --- 管理员路由 ---
      {
        path: '/admin/users',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', role: 0 }
      },
      {
        path: '/admin/activity',
        name: 'ActivityAudit',
        component: () => import('@/views/admin/ActivityAudit.vue'),
        meta: { title: '活动审批', role: 0 }
      },
      {
        path: '/admin/reimburse',
        name: 'ReimburseAudit',
        component: () => import('@/views/admin/ReimburseAudit.vue'),
        meta: { title: '财务核销', role: 0 }
      },
      {
        path: '/admin/stats',
        name: 'StatsDashboard',
        component: () => import('@/views/admin/StatsDashboard.vue'),
        meta: { title: '统计公示', role: 0 }
      },

      // --- 用户端路由 ---
      {
        path: '/user/home',
        name: 'UserProfile',
        component: () => import('@/views/user/UserProfile.vue'),
        meta: { title: '个人中心', role: 1 }
      },
      {
        path: '/user/activity',
        name: 'ActivityApply',
        component: () => import('@/views/user/ActivityApply.vue'),
        meta: { title: '活动申请', role: 1 }
      },
      {
        path: '/user/reimburse',
        name: 'ReimburseApply',
        component: () => import('@/views/user/ReimburseApply.vue'),
        meta: { title: '报销申请', role: 1 }
      },
      {
        path: '/user/message',
        name: 'MessageCenter',
        component: () => import('@/views/user/MessageCenter.vue'),
        meta: { title: '消息中心', role: 1 }
      }
    ]
  },
  
  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// --- 路由拦截器 (Navigation Guard) ---
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null

  // 1. 设置网页标题
  document.title = to.meta.title ? `${to.meta.title} - 社团经费系统` : '社团经费管理系统'

  // 2. 如果去的是登录页，直接放行
  if (to.path === '/login') {
    next()
    return
  }

  // 3. 如果没有 Token，强制跳回登录
  if (!token) {
    next('/login')
    return
  }

  // 4. 简单的权限校验 (防止学生访问管理员页面)
  // 如果路由配置了 meta.role，且与当前用户 role 不一致，拦截
  if (to.meta.role !== undefined && to.meta.role !== user.role) {
    // 如果是学生想去管理端，踢回学生首页
    if (user.role === 1) next('/user/home')
    // 如果是管理员想去学生端(通常允许，或者踢回管理首页)
    else next('/admin/stats')
    return
  }

  next()
})

export default router