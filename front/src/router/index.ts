import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/index.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/login/register.vue')
    },
    {
      path: '/',
      name: 'layout',
      component: () => import('../layout/index.vue'),
      redirect: '/home',
      children: [
        {
          path: 'home',
          name: 'home',
          component: () => import('../views/home/index.vue'),
          meta: { title: '首页' }
        },
        // 学生端路由
        {
          path: 'student/apply',
          name: 'student-apply',
          component: () => import('../views/student/apply.vue'),
          meta: { title: '寒衣申请', role: ['student'] }
        },
        {
          path: 'student/select',
          name: 'student-select',
          component: () => import('../views/student/select.vue'),
          meta: { title: '选款登记', role: ['student'] }
        },
        {
          path: 'student/profile',
          name: 'student-profile',
          component: () => import('../views/student/profile.vue'),
          meta: { title: '个人中心', role: ['student'] }
        },
        {
          path: 'student/progress',
          name: 'student-progress',
          component: () => import('../views/student/progress.vue'),
          meta: { title: '审核进度', role: ['student'] }
        },
        // 审核路由（辅导员、学院、学校）
        {
          path: 'audit/list',
          name: 'audit-list',
          component: () => import('../views/audit/list.vue'),
          meta: { title: '补助审核', role: ['counselor', 'college', 'school'] }
        },
        // 超级管理员路由
        {
          path: 'superadmin/whitelist',
          name: 'superadmin-whitelist',
          component: () => import('../views/superadmin/whitelist.vue'),
          meta: { title: '白名单配置', role: ['superadmin'] }
        },
        // 学校管理员专用路由
        {
          path: 'school/batch',
          name: 'school-batch',
          component: () => import('../views/school/batch.vue'),
          meta: { title: '批次管理', role: ['school'] }
        },
        {
          path: 'school/clothing',
          name: 'school-clothing',
          component: () => import('../views/school/clothing.vue'),
          meta: { title: '服装与SKU', role: ['school'] }
        },
        {
          path: 'school/statistics',
          name: 'school-statistics',
          component: () => import('../views/school/statistics.vue'),
          meta: { title: '数据统计', role: ['school'] }
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.path !== '/login' && !userStore.token) {
    next('/login')
  } else {
    // 简单的权限校验
    if (to.meta.role && !(to.meta.role as string[]).includes(userStore.role)) {
      next('/home') // 无权限则跳首页
    } else {
      next()
    }
  }
})

export default router
