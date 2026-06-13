<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="aside">
      <div class="logo">
        <el-icon class="logo-icon"><Orange /></el-icon>
        <span class="logo-text">寒衣补助系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        background-color="#1e222d"
        text-color="#c1c6d8"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/home">
          <el-icon><House /></el-icon>
          <span>系统首页</span>
        </el-menu-item>
        
        <template v-if="role === 'student'">
          <el-menu-item-group title="学生服务">
            <el-menu-item index="/student/apply">
              <el-icon><EditPen /></el-icon>
              <span>寒衣申请表单</span>
            </el-menu-item>
            <el-menu-item index="/student/progress">
              <el-icon><List /></el-icon>
              <span>审核进度查看</span>
            </el-menu-item>
            <el-menu-item index="/student/select">
              <el-icon><Present /></el-icon>
              <span>选款与尺码登记</span>
            </el-menu-item>
            <el-menu-item index="/student/profile">
              <el-icon><User /></el-icon>
              <span>个人中心设置</span>
            </el-menu-item>
          </el-menu-item-group>
        </template>

        <template v-if="['counselor', 'college', 'school'].includes(role)">
          <el-menu-item-group title="审核管理">
            <el-menu-item index="/audit/list">
              <el-icon><List /></el-icon>
              <span>补助资格审核</span>
            </el-menu-item>
          </el-menu-item-group>
        </template>

        <template v-if="role === 'superadmin'">
          <el-menu-item-group title="系统管理">
            <el-menu-item index="/superadmin/whitelist">
              <el-icon><Lock /></el-icon>
              <span>白名单配置</span>
            </el-menu-item>
          </el-menu-item-group>
        </template>

        <template v-if="role === 'school'">
          <el-menu-item-group title="系统配置">
            <el-menu-item index="/school/batch">
              <el-icon><Calendar /></el-icon>
              <span>发放批次管理</span>
            </el-menu-item>
            <el-menu-item index="/school/clothing">
              <el-icon><Goods /></el-icon>
              <span>服装款式与库存</span>
            </el-menu-item>
            <el-menu-item index="/school/statistics">
              <el-icon><DataLine /></el-icon>
              <span>全校数据统计</span>
            </el-menu-item>
          </el-menu-item-group>
        </template>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-dropdown">
              <el-avatar :size="32" class="avatar">{{ userInfo.name?.charAt(0) || 'U' }}</el-avatar>
              <span class="username">{{ userInfo.name || userInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main">
        <div class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const role = computed(() => userStore.role)
const userInfo = computed(() => userStore.userInfo)
const activeMenu = computed(() => route.path)
const currentRouteTitle = computed(() => route.meta.title || '')

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background-color: #f0f2f5;
}

.aside {
  background-color: #1e222d;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  background-color: #1e222d;
  border-bottom: 1px solid #101117;
  overflow: hidden;
}

.logo-icon {
  font-size: 24px;
  color: #409EFF;
  margin-right: 10px;
}

.el-menu-vertical {
  border-right: none;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 240px;
}

.header {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  z-index: 9;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 8px;
  border-radius: 4px;
  transition: background 0.3s;
}

.user-dropdown:hover {
  background: #f5f5f5;
}

.avatar {
  background-color: #409EFF;
  color: #fff;
  margin-right: 8px;
}

.username {
  font-size: 14px;
  color: #333;
  margin-right: 4px;
}

.main {
  padding: 24px;
  box-sizing: border-box;
}

.main-content {
  background-color: transparent;
  min-height: calc(100vh - 108px);
}

/* 页面切换动画 */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s cubic-bezier(0.55, 0, 0.1, 1);
}
.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
