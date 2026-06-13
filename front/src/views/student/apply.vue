<template>
  <div class="apply-container">
    <el-row justify="center">
      <el-col :xs="24" :sm="20" :md="16" :lg="12">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Edit /></el-icon>
              <span>寒衣补助申请</span>
            </div>
          </template>

          <el-alert
            v-if="isApplyEnded"
            title="申请已截止"
            type="error"
            description="当前批次的寒衣补助申请时间已截止，无法再提交或修改申请。"
            show-icon
            :closable="false"
            class="mb-20"
          />

          <el-alert
            v-if="['pending', 'approved'].includes(currentAuditStatus)"
            :title="currentAuditStatus === 'approved' ? '审核已通过' : '申请审核中'"
            :type="currentAuditStatus === 'approved' ? 'success' : 'warning'"
            :description="currentAuditStatus === 'approved' ? '您的寒衣补助申请已通过所有审核，请前往【选款与尺码登记】页面选择您需要的衣物。' : '您的寒衣补助申请正在审核流程中，请耐心等待，期间无法修改申请资料。'"
            show-icon
            :closable="false"
            class="mb-20"
          />

          <el-alert
            v-if="currentAuditStatus === 'rejected'"
            title="申请已被驳回"
            type="error"
            description="很遗憾，您的寒衣补助申请未能通过审核。如有疑问请联系辅导员。"
            show-icon
            :closable="false"
            class="mb-20"
          />

          <el-alert
            v-if="!isApplyEnded && (currentAuditStatus === undefined || currentAuditStatus === null || currentAuditStatus === 'none')"
            title="填写须知"
            type="info"
            :description="`请在规定的申请截止时间前提交。${isNewStudent ? '您是新生，申请理由为选填。' : '您是老生，必须填写详细的申请理由。'}系统将严格审核您的申请资料。`"
            show-icon
            :closable="false"
            class="mb-20"
          />

          <el-form 
            :model="applyForm" 
            :rules="rules" 
            ref="applyFormRef" 
            label-position="top" 
            size="large"
            :disabled="isApplyEnded || ['pending', 'approved'].includes(currentAuditStatus)"
          >
            <el-form-item label="申请批次" prop="batchId">
              <el-select v-model="applyForm.batchId" placeholder="请选择当前可用的申请批次" style="width: 100%;">
                <el-option label="2023学年特困寒衣补助批次" :value="1" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="申请理由说明" prop="applyReason">
              <el-input 
                v-model="applyForm.applyReason" 
                type="textarea" 
                :rows="6" 
                placeholder="请输入申请理由（老生必填），请详细说明您的家庭经济状况及需要补助的原因..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>

            <el-form-item class="submit-item">
              <el-button type="primary" :loading="loading" @click="handleSubmit" class="submit-btn" round>
                <el-icon><Check /></el-icon> 提交申请
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { submitApplication, getActiveBatchStatus, getUserInfo } from '@/api'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const applyFormRef = ref()
const loading = ref(false)

const applyForm = reactive({
  batchId: undefined,
  applyReason: ''
})

const isApplyEnded = computed(() => userStore.batchStatus.isApplyEnded)
const isNewStudent = computed(() => userStore.userInfo.isNewStudent)
const currentAuditStatus = computed(() => userStore.userInfo.auditStatus)
const progressFinalStatus = computed(() => {
  const fs = userStore.userInfo.finalStatus as number | null | undefined
  return fs ?? null
})

const rules = computed(() => {
  return {
    batchId: [{ required: true, message: '请选择申请批次', trigger: 'change' }],
    applyReason: [
      { 
        required: !isNewStudent.value, 
        message: '老生必须填写申请理由', 
        trigger: 'blur' 
      }
    ]
  }
})

const loadBatchAndStatus = async () => {
  try {
    const [batchRes, userRes] = await Promise.all([
      getActiveBatchStatus(),
      getUserInfo()
    ])
    userStore.setBatchStatus(batchRes)
    userStore.setUserInfo({ ...userStore.userInfo, ...userRes })
    
    // 默认选中当前活动批次
    if (!applyForm.batchId) {
      applyForm.batchId = (batchRes as any).id
    }
  } catch (e) {
    console.error(e)
  }
}

const handleSubmit = async () => {
  if (!applyFormRef.value) return
  await applyFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        await submitApplication(applyForm)
        ElMessage.success('申请提交成功，请耐心等待审核')
        applyForm.applyReason = ''
        // 提交后重新获取最新状态
        await loadBatchAndStatus()
      } catch (error: any) {
        ElMessage.error(error.message || '提交失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadBatchAndStatus()
})
</script>

<style scoped>
.apply-container {
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
.mb-20 {
  margin-bottom: 20px;
}
.submit-item {
  margin-top: 30px;
  text-align: center;
}
.submit-btn {
  width: 200px;
  font-size: 16px;
}
</style>
