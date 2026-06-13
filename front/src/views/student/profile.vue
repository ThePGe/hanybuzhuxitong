<template>
  <div class="profile-container">
    <el-row justify="center">
      <el-col :xs="24" :sm="18" :md="12">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><User /></el-icon>
              <span>个人中心与账号设置</span>
            </div>
          </template>

          <div class="user-info-section">
            <el-avatar :size="80" class="avatar-large">{{ userInfo.name?.charAt(0) || 'U' }}</el-avatar>
            <h2 class="user-name">{{ userInfo.name || '未知用户' }}</h2>
            <p class="user-id">学号: {{ userInfo.username }}</p>
            <el-tag :type="userInfo.gender === 'M' ? '' : 'danger'" size="large" effect="plain" class="gender-tag">
              {{ userInfo.gender === 'M' ? '男生' : '女生' }}
            </el-tag>
          </div>

          <el-divider>第三方账号绑定</el-divider>

          <div class="wechat-bind-section">
            <div class="wechat-info">
              <el-icon class="wechat-icon"><ChatDotRound /></el-icon>
              <div class="wechat-text">
                <h3>微信绑定状态</h3>
                <p v-if="isBound" class="bound-text">已绑定微信账号 (用于接收通知)</p>
                <p v-else class="unbound-text">未绑定微信账号</p>
              </div>
            </div>
            
            <el-button v-if="isBound" type="danger" plain @click="handleUnbind" :loading="loading">
              解除绑定
            </el-button>
            <el-button v-else type="primary" disabled>
              请在小程序端绑定
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { unbindWechat } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const isBound = ref(true) // 假设初始状态为已绑定
const loading = ref(false)

const handleUnbind = async () => {
  try {
    await ElMessageBox.confirm(
      '解除微信绑定后，您将无法通过微信接收补助审核及领取的通知，是否确认解绑？',
      '确认解绑',
      {
        confirmButtonText: '确定解绑',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    loading.value = true
    await unbindWechat({ studentId: userInfo.value.username })
    isBound.value = false
    ElMessage.success('微信解绑成功')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '解绑失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px 0;
}
.box-card {
  border-radius: 12px;
}
.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}
.header-icon {
  margin-right: 8px;
  color: #409eff;
  font-size: 22px;
}
.user-info-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}
.avatar-large {
  font-size: 32px;
  background-color: #409EFF;
  color: white;
  margin-bottom: 16px;
}
.user-name {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}
.user-id {
  margin: 0 0 16px 0;
  color: #909399;
  font-size: 14px;
}
.gender-tag {
  border-radius: 20px;
}
.wechat-bind-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-top: 20px;
}
.wechat-info {
  display: flex;
  align-items: center;
}
.wechat-icon {
  font-size: 40px;
  color: #07c160; /* 微信绿 */
  margin-right: 15px;
}
.wechat-text h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
}
.wechat-text p {
  margin: 0;
  font-size: 13px;
}
.bound-text {
  color: #67c23a;
}
.unbound-text {
  color: #909399;
}
</style>
