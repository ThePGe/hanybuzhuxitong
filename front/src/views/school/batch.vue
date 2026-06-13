<template>
  <div class="batch-container">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>批次管理</span>
          <el-button type="primary" @click="handleCreateBatch">创建新批次</el-button>
        </div>
      </template>

      <div class="current-batch" v-if="currentBatch">
        <h3>当前批次信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="困难认定学年">{{ currentBatch.year }}</el-descriptions-item>
          <el-descriptions-item label="困难等级">{{ currentBatch.level }}</el-descriptions-item>
          <el-descriptions-item label="学生申请截止时间">{{ currentBatch.applyEndTime }}</el-descriptions-item>
          <el-descriptions-item label="选款登记截止时间">{{ currentBatch.selectEndTime }}</el-descriptions-item>
        </el-descriptions>
        <div style="margin-top: 20px;">
          <el-button type="warning" @click="handleExtend">修改截止时间 (延期)</el-button>
        </div>
      </div>
      <el-empty v-else description="暂无活动批次" />
    </el-card>

    <el-dialog v-model="createDialogVisible" title="创建批次" width="500px">
      <el-form :model="createForm" label-width="120px">
        <el-form-item label="认定学年">
          <el-input v-model="createForm.year" placeholder="如：2023" />
        </el-form-item>
        <el-form-item label="困难等级">
          <el-select v-model="createForm.level" style="width: 100%;">
            <el-option label="特困" value="特困" />
            <el-option label="困难" value="困难" />
            <el-option label="一般困难" value="一般困难" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请截止时间">
          <el-date-picker 
            v-model="createForm.applyEndTime" 
            type="datetime" 
            placeholder="选择截止时间" 
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="选款截止时间">
          <el-date-picker 
            v-model="createForm.selectEndTime" 
            type="datetime" 
            placeholder="选择截止时间" 
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmCreate">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="extendDialogVisible" title="延期设置" width="400px">
      <el-form :model="extendForm" label-width="120px">
        <el-form-item label="申请截止时间">
          <el-date-picker 
            v-model="extendForm.applyEndTime" 
            type="datetime" 
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="选款截止时间">
          <el-date-picker 
            v-model="extendForm.selectEndTime" 
            type="datetime" 
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="extendDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmExtend">确定延期</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getBatchInfo, createBatch, updateBatchTime } from '@/api'
import { ElMessage } from 'element-plus'

const currentBatch = ref<any>(null)

const loadBatchInfo = async () => {
  try {
    const res = await getBatchInfo()
    currentBatch.value = res
  } catch (error) {
    ElMessage.error('获取批次信息失败')
  }
}

const createDialogVisible = ref(false)
const createForm = reactive({
  year: '',
  level: '',
  applyEndTime: '',
  selectEndTime: ''
})

const extendDialogVisible = ref(false)
const extendForm = reactive({
  batchId: undefined,
  applyEndTime: '',
  selectEndTime: ''
})

const handleCreateBatch = () => {
  createForm.year = ''
  createForm.level = ''
  createForm.applyEndTime = ''
  createForm.selectEndTime = ''
  createDialogVisible.value = true
}

const confirmCreate = async () => {
  try {
    await createBatch(createForm)
    ElMessage.success('批次创建成功')
    createDialogVisible.value = false
    loadBatchInfo()
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const handleExtend = () => {
  extendForm.batchId = currentBatch.value.id
  extendForm.applyEndTime = currentBatch.value.applyEndTime
  extendForm.selectEndTime = currentBatch.value.selectEndTime
  extendDialogVisible.value = true
}

const confirmExtend = async () => {
  try {
    await updateBatchTime(extendForm)
    ElMessage.success('延期成功')
    extendDialogVisible.value = false
    loadBatchInfo()
  } catch (error) {
    ElMessage.error('延期失败')
  }
}

onMounted(() => {
  loadBatchInfo()
})
</script>
