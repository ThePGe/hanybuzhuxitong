<template>
  <div class="select-container">
    <el-row :gutter="20" justify="center">
      <el-col :xs="24" :sm="22" :md="18" :lg="14">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Present /></el-icon>
              <span>服装款式与尺码登记</span>
            </div>
          </template>

          <el-alert
            v-if="!canSelect"
            title="暂无选款资格"
            type="error"
            description="您的寒衣补助申请尚未通过审核，暂时无法进行款式和尺码的登记。"
            show-icon
            :closable="false"
            class="mb-20"
          />
          <el-alert
            v-else-if="isSelectEnded"
            title="选款已结束"
            type="error"
            description="当前批次的选款登记时间已截止。如需特殊修改，请联系学校管理员。"
            show-icon
            :closable="false"
            class="mb-20"
          />
          <el-alert
            v-else
            title="温馨提示"
            type="warning"
            :description="`请在截止时间前完成选款与尺码登记。超过截止时间将无法修改，请谨慎选择尺码！(当前系统已自动为您匹配为${userGender === 'M' ? '男' : '女'}款服装)`"
            show-icon
            :closable="false"
            class="mb-20"
          />

          <el-form 
            :model="selectForm" 
            :rules="rules" 
            ref="selectFormRef" 
            label-position="top" 
            size="large"
            :disabled="!canSelect || isSelectEnded"
          >
            <el-row :gutter="40">
              <el-col :span="12">
                <el-form-item label="1. 选择款式" prop="clothingId">
                  <el-select 
                    v-model="selectForm.clothingId" 
                    placeholder="请选择服装款式" 
                    style="width: 100%;"
                    @change="handleClothingChange"
                  >
                    <el-option 
                      v-for="item in clothingList" 
                      :key="item.id" 
                      :label="item.name" 
                      :value="item.id" 
                    />
                  </el-select>
                </el-form-item>

                <el-form-item label="2. 选择尺码" prop="skuId" v-if="selectForm.clothingId">
                  <el-select v-model="selectForm.skuId" placeholder="请选择适合您的尺码" style="width: 100%;">
                    <el-option 
                      v-for="sku in currentClothing?.skus" 
                      :key="sku.id" 
                      :label="`${sku.size}码 (${sku.skuCode})`" 
                      :value="sku.id" 
                    />
                  </el-select>
                </el-form-item>
                
                <div v-if="currentClothing" class="size-recommend">
                  <el-icon><InfoFilled /></el-icon>
                  <strong>尺码参考：</strong>
                  <p>{{ currentClothing.sizeRecommend }}</p>
                </div>
              </el-col>

              <el-col :span="12">
                <div class="preview-area" v-if="currentClothing?.imageUrl">
                  <p class="preview-title">款式预览</p>
                  <el-image 
                    class="clothing-img"
                    :src="currentClothing.imageUrl" 
                    fit="cover"
                    :preview-src-list="[currentClothing.imageUrl]"
                  >
                    <template #placeholder>
                      <div class="image-slot">加载中...</div>
                    </template>
                  </el-image>
                </div>
                <div class="preview-empty" v-else>
                  <el-icon class="empty-icon"><Picture /></el-icon>
                  <p>请先在左侧选择款式</p>
                </div>
              </el-col>
            </el-row>

            <el-form-item class="submit-item">
              <el-button type="primary" :loading="loading" @click="handleSubmit" class="submit-btn" round>
                <el-icon><Check /></el-icon> 确认提交登记
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
import { getSelectionClothingList, submitSelection } from '@/api'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const selectFormRef = ref()
const loading = ref(false)
const clothingList = ref<any[]>([])

const selectForm = reactive({
  clothingId: undefined,
  skuId: undefined
})

const rules = {
  clothingId: [{ required: true, message: '请选择款式', trigger: 'change' }],
  skuId: [{ required: true, message: '请选择尺码', trigger: 'change' }]
}

const currentClothing = computed(() => {
  return clothingList.value.find(item => item.id === selectForm.clothingId)
})

const userGender = computed(() => {
  return userStore.userInfo?.gender || 'M'
})

const canSelect = computed(() => userStore.userInfo?.auditStatus === 'approved')
const isSelectEnded = computed(() => userStore.batchStatus?.isSelectEnded)

const loadClothingList = async () => {
  try {
    // 限制只能请求当前性别的服装款式
    const res = await getSelectionClothingList({ gender: userGender.value })
    clothingList.value = res as any[]
  } catch (error: any) {
    ElMessage.error('加载款式列表失败')
  }
}

const handleClothingChange = () => {
  selectForm.skuId = undefined
}

const handleSubmit = async () => {
  if (!selectFormRef.value) return
  await selectFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        await submitSelection(selectForm)
        ElMessage.success('尺码登记成功')
      } catch (error: any) {
        ElMessage.error(error.message || '登记失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadClothingList()
})
</script>

<style scoped>
.select-container {
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
  color: #67c23a;
  font-size: 22px;
}
.mb-20 {
  margin-bottom: 24px;
}
.size-recommend {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}
.size-recommend p {
  margin: 5px 0 0 0;
}
.preview-title {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
}
.preview-area {
  text-align: center;
}
.clothing-img {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.preview-empty {
  height: 300px;
  margin-top: 28px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  border-radius: 8px;
  color: #909399;
}
.empty-icon {
  font-size: 48px;
  margin-bottom: 10px;
  color: #c0c4cc;
}
.submit-item {
  margin-top: 40px;
  text-align: center;
}
.submit-btn {
  width: 240px;
  font-size: 16px;
}
</style>
