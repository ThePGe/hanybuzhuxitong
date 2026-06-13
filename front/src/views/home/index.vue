<template>
  <div class="home">
    <el-card>
      <h2>欢迎使用寒衣补助系统</h2>
      <p>当前登录角色：{{ roleMap[userStore.role] || userStore.role }}</p>
      <p>请通过左侧菜单进行操作。</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const roleMap: Record<string, string> = {
  student: '学生',
  counselor: '辅导员',
  college: '学院管理员',
  school: '学校管理员'
}
</script>
