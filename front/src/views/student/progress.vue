<template>
  <div class="progress-container">
    <el-row justify="center">
      <el-col :xs="24" :sm="22" :md="18" :lg="14">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon"><List /></el-icon>
                <span>审核进度查看</span>
              </div>
              <el-button size="small" plain @click="loadStatus">刷新</el-button>
            </div>
          </template>

          <el-alert
            v-if="finalStatus === null || finalStatus === undefined"
            title="暂无申请记录"
            type="info"
            description="您还未提交寒衣补助申请，请先前往【寒衣申请表单】提交申请。"
            show-icon
            :closable="false"
            class="mb-20"
          />

          <template v-else-if="finalStatus === -1">
            <el-alert
              title="申请已被驳回"
              type="error"
              :description="statusText"
              show-icon
              :closable="false"
              class="mb-20"
            />
            <el-card class="reason-card" shadow="hover">
              <template #header>
                <span class="reason-title">驳回详情</span>
              </template>
              <div class="reason-content">
                <div v-if="userStore.userInfo.counselorReason" class="reason-item">
                  <span class="reason-label">辅导员意见：</span>
                  <span class="reason-text">{{ userStore.userInfo.counselorReason }}</span>
                </div>
                <div v-if="userStore.userInfo.collegeReason" class="reason-item">
                  <span class="reason-label">学院意见：</span>
                  <span class="reason-text">{{ userStore.userInfo.collegeReason }}</span>
                </div>
                <div v-if="userStore.userInfo.schoolReason" class="reason-item">
                  <span class="reason-label">学校意见：</span>
                  <span class="reason-text">{{ userStore.userInfo.schoolReason }}</span>
                </div>
                <div v-if="!userStore.userInfo.counselorReason && !userStore.userInfo.collegeReason && !userStore.userInfo.schoolReason" class="reason-item">
                  <span class="reason-text">暂无驳回理由</span>
                </div>
              </div>
            </el-card>
          </template>

          <el-alert
            v-else-if="finalStatus === 3"
            title="终审通过"
            type="success"
            description="您的申请已通过学校终审，已获得选款资格，请前往【选款与尺码登记】进行选款。"
            show-icon
            :closable="false"
            class="mb-20"
          />

          <el-alert
            v-else
            title="审核进行中"
            type="warning"
            :description="statusText"
            show-icon
            :closable="false"
            class="mb-20"
          />

          <div v-if="finalStatus !== null && finalStatus !== undefined">
            <el-steps :active="Math.max(0, finalStatus)" finish-status="success" align-center>
              <el-step title="提交申请" description="等待辅导员审核" />
              <el-step title="辅导员初审" description="等待学院复审" />
              <el-step title="学院复审" description="等待学校终审" />
              <el-step
                title="学校终审"
                :status="finalStatus === -1 ? 'error' : finalStatus === 3 ? 'success' : 'wait'"
                :description="finalStatus === -1 ? '已被驳回' : '终审通过可进行选款'"
              />
            </el-steps>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getActiveBatchStatus, getUserInfo } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)

const finalStatus = computed(() => userStore.userInfo.finalStatus as number | null | undefined)

const statusText = computed(() => {
  if (finalStatus.value === -1) return '很遗憾，您的申请未能通过审核。如有疑问请联系辅导员。'
  if (finalStatus.value === 0) return '当前进度：待辅导员审核。'
  if (finalStatus.value === 1) return '当前进度：辅导员已通过，待学院复审。'
  if (finalStatus.value === 2) return '当前进度：学院已通过，待学校终审。'
  if (finalStatus.value === 3) return '当前进度：学校终审通过。'
  return '当前进度：未知。'
})

const loadStatus = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const [batchRes, userRes] = await Promise.all([getActiveBatchStatus(), getUserInfo()])
    userStore.setBatchStatus(batchRes)
    userStore.setUserInfo({ ...userStore.userInfo, ...userRes })
  } catch (e: any) {
    ElMessage.error(e?.message || '获取进度失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStatus()
})
</script>

<style scoped>
.progress-container {
  padding: 20px 0;
}
.box-card {
  border-radius: 12px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-left {
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
.mb-20 {
  margin-bottom: 20px;
}
.reason-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border-left: 4px solid #f56c6c;
}
.reason-title {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}
.reason-content {
  padding-top: 10px;
}
.reason-item {
  margin-bottom: 12px;
  display: flex;
  flex-wrap: wrap;
}
.reason-item:last-child {
  margin-bottom: 0;
}
.reason-label {
  font-weight: 600;
  color: #606266;
  margin-right: 8px;
  flex-shrink: 0;
}
.reason-text {
  color: #303133;
  line-height: 1.6;
}
</style>
