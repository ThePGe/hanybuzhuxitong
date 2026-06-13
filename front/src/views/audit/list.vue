<template>
  <div class="audit-list-container">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon class="title-icon"><List /></el-icon>
            <span>补助资格审核列表</span>
          </div>
          <div class="header-actions">
            <el-button type="success" @click="handleBatchApprove(true)" round>
              <el-icon><Select /></el-icon> 批量通过
            </el-button>
            <el-button type="danger" @click="handleBatchApprove(false)" round>
              <el-icon><CloseBold /></el-icon> 批量拒绝
            </el-button>
            <el-button type="primary" @click="handleSubmitToNext" round>
              <el-icon><Upload /></el-icon> 提交至上一级
            </el-button>
            <el-button v-if="userStore.role === 'school'" type="warning" @click="handleNotify" round>
              <el-icon><Message /></el-icon> 终审结果通知
            </el-button>
          </div>
        </div>
      </template>

      <div class="filter-section">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="审核状态">
            <el-select v-model="queryParams.status" placeholder="请选择状态" @change="fetchData" style="width: 200px">
              <el-option label="待审核" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchData">
              <el-icon><Search /></el-icon> 筛选
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table 
        :data="tableData" 
        v-loading="loading" 
        @selection-change="handleSelectionChange" 
        border
        stripe
        style="width: 100%"
        class="custom-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="studentId" label="学号" width="120" align="center" />
        <el-table-column prop="name" label="姓名" width="100" align="center" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === 'M' ? '' : 'danger'" size="small">
              {{ row.gender === 'M' ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="college" label="学院" width="160" />
        <el-table-column prop="major" label="专业" width="160" />
        <el-table-column prop="reason" label="申请理由" show-overflow-tooltip min-width="250" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'"
              effect="light"
              round
            >
              {{ row.status === 'approved' ? '已通过' : row.status === 'rejected' ? '已拒绝' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <template v-if="row.status === 'pending'">
              <el-button size="small" type="success" plain @click="handleSingleApprove(row, true)">通过</el-button>
              <el-button size="small" type="danger" plain @click="handleSingleApprove(row, false)">拒绝</el-button>
            </template>
            <span v-else class="text-muted">已处理</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="fetchData"
          @current-change="fetchData"
          background
        />
      </div>
    </el-card>

    <el-dialog v-model="rejectDialogVisible" title="填写拒绝理由" width="400px">
      <el-input 
        v-model="rejectReason" 
        type="textarea" 
        :rows="4" 
        placeholder="请输入拒绝理由（选填），这将会通知给申请学生。" 
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAuditList, approveApplication, submitToNext, notifyStudents } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref<any[]>([])

const queryParams = reactive({
  page: 1,
  size: 20,
  status: 'pending'
})

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentOperatingIds = ref<number[]>([])

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getAuditList(queryParams)
    tableData.value = res.records
    total.value = res.total
  } catch (error) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (val: any[]) => {
  selectedRows.value = val
}

const handleBatchApprove = async (isPass: boolean) => {
  if (selectedRows.value.length === 0) {
    return ElMessage.warning('请先在表格中勾选要操作的记录')
  }
  const ids = selectedRows.value.map(row => row.id)
  if (isPass) {
    await doApprove(ids, true)
  } else {
    currentOperatingIds.value = ids
    rejectReason.value = ''
    rejectDialogVisible.value = true
  }
}

const handleSingleApprove = async (row: any, isPass: boolean) => {
  if (isPass) {
    await doApprove([row.id], true)
  } else {
    currentOperatingIds.value = [row.id]
    rejectReason.value = ''
    rejectDialogVisible.value = true
  }
}

const confirmReject = async () => {
  await doApprove(currentOperatingIds.value, false, rejectReason.value)
  rejectDialogVisible.value = false
}

const doApprove = async (ids: number[], isPass: boolean, reason?: string) => {
  try {
    await approveApplication({ applicationIds: ids, isPass, reason })
    ElMessage.success(isPass ? '审核通过操作成功' : '审核拒绝操作成功')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleSubmitToNext = async () => {
  try {
    await ElMessageBox.confirm('确认将当前页面的审核结果提交至上一级吗？（未审核数据将按系统规则处理）', '提交确认', { type: 'warning' })
    await submitToNext({})
    ElMessage.success('提交上一级成功')
  } catch (e) {
    // cancelled
  }
}

const handleNotify = async () => {
  try {
    await ElMessageBox.confirm('确认下发通知吗？通过的学生将收到选款通知，拒绝的学生将收到拒绝理由。', '通知下发', { type: 'warning' })
    await notifyStudents()
    ElMessage.success('通知下发成功')
  } catch (e) {
    // cancelled
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.audit-list-container {
  padding: 10px;
}
.page-card {
  border-radius: 12px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-title {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}
.title-icon {
  margin-right: 8px;
  color: #409eff;
  font-size: 22px;
}
.header-actions .el-button {
  margin-left: 10px;
}
.filter-section {
  background-color: #f8f9fa;
  padding: 18px 20px 0 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}
.custom-table {
  border-radius: 8px;
  overflow: hidden;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.text-muted {
  color: #909399;
  font-size: 13px;
}
</style>
