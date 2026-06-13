<template>
  <div class="whitelist-container">
    <el-card shadow="never" class="page-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon class="title-icon"><Lock /></el-icon>
            <span>超级管理员中心</span>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="白名单管理" name="whitelist">
          <div class="mb-20" style="display: flex; justify-content: space-between;">
            <el-alert
              title="白名单控制说明"
              type="warning"
              description="所有用户（除超管外）必须被加入白名单才能登录系统。请谨慎操作。"
              show-icon
              :closable="false"
              style="width: 70%"
            />
            <el-button type="primary" @click="handleAdd" round>
              <el-icon><Plus /></el-icon> 添加白名单用户
            </el-button>
          </div>

          <el-table 
            :data="tableData" 
            v-loading="loading" 
            border
            stripe
            style="width: 100%"
            class="custom-table"
          >
            <el-table-column type="index" label="序号" width="80" align="center" />
            <el-table-column label="允许登录的账号">
              <template #default="{ row }">
                <el-tag size="large" effect="plain" type="success">
                  <el-icon><User /></el-icon> {{ row }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center">
              <template #default="{ row }">
                <el-button size="small" type="danger" plain @click="handleRemove(row)">
                  <el-icon><Delete /></el-icon> 移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="注册申请审核" name="registerApply">
          <el-table 
            :data="applyList" 
            v-loading="applyLoading" 
            border
            stripe
            style="width: 100%"
            class="custom-table"
          >
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="username" label="账号" width="120" />
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column label="角色" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.role === 0" type="danger">学校管理员</el-tag>
                <el-tag v-else-if="row.role === 1" type="warning">学院管理员</el-tag>
                <el-tag v-else-if="row.role === 2" type="success">辅导员</el-tag>
                <el-tag v-else-if="row.role === 3" type="info">学生</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="collegeName" label="学院" width="150" />
            <el-table-column prop="className" label="班级" width="120" />
            <el-table-column prop="createTime" label="申请时间" width="180">
              <template #default="{ row }">
                {{ new Date(row.createTime).toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="{ row }">
                <el-button size="small" type="success" @click="handleReview(row, true)">通过</el-button>
                <el-button size="small" type="danger" @click="handleReview(row, false)">驳回</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" title="添加白名单用户" width="400px">
      <el-form :model="form" @submit.prevent>
        <el-form-item label="账号名称">
          <el-input 
            v-model="form.username" 
            placeholder="请输入要授权的用户账号，如：school" 
            clearable
            @keyup.enter="confirmAdd"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAdd" :loading="submitLoading">确认添加</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { getWhitelist, addWhitelist, removeWhitelist, getRegisterApplyList, reviewRegisterApply } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('whitelist')
const loading = ref(false)
const tableData = ref<string[]>([])

const applyLoading = ref(false)
const applyList = ref<any[]>([])

const dialogVisible = ref(false)
const submitLoading = ref(false)
const form = reactive({
  username: ''
})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getWhitelist()
    tableData.value = res as string[]
  } catch (error) {
    ElMessage.error('获取白名单失败')
  } finally {
    loading.value = false
  }
}

const fetchApplyList = async () => {
  applyLoading.value = true
  try {
    const res = await getRegisterApplyList()
    applyList.value = res as any[]
  } catch (error) {
    ElMessage.error('获取申请列表失败')
  } finally {
    applyLoading.value = false
  }
}

watch(activeTab, (val) => {
  if (val === 'whitelist') {
    fetchList()
  } else if (val === 'registerApply') {
    fetchApplyList()
  }
})

const handleAdd = () => {
  form.username = ''
  dialogVisible.value = true
}

const confirmAdd = async () => {
  if (!form.username.trim()) {
    return ElMessage.warning('请输入账号')
  }
  submitLoading.value = true
  try {
    await addWhitelist(form.username.trim())
    ElMessage.success('添加成功')
    dialogVisible.value = false
    fetchList()
  } catch (error: any) {
    ElMessage.error(error.message || '添加失败')
  } finally {
    submitLoading.value = false
  }
}

const handleRemove = async (username: string) => {
  try {
    await ElMessageBox.confirm(`确认将用户 [${username}] 移出白名单吗？移除后该用户将无法登录系统。`, '移除确认', {
      confirmButtonText: '确定移除',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await removeWhitelist(username)
    ElMessage.success('移除成功')
    fetchList()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('移除失败')
    }
  }
}

const handleReview = async (row: any, approve: boolean) => {
  if (approve) {
    try {
      await ElMessageBox.confirm(`确认通过账号 [${row.username}] 的注册申请？通过后该账号将自动加入白名单。`, '通过确认', {
        type: 'success'
      })
      await reviewRegisterApply({ id: row.id, approve: true })
      ElMessage.success('已通过申请')
      fetchApplyList()
    } catch (e) {
      // cancel
    }
  } else {
    try {
      const { value } = await ElMessageBox.prompt('请输入驳回理由', '驳回申请', {
        confirmButtonText: '确定驳回',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '驳回理由不能为空'
      })
      await reviewRegisterApply({ id: row.id, approve: false, rejectReason: value })
      ElMessage.success('已驳回申请')
      fetchApplyList()
    } catch (e) {
      // cancel
    }
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.whitelist-container {
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
  color: #F56C6C;
  font-size: 22px;
}
.mb-20 {
  margin-bottom: 20px;
}
.custom-table {
  border-radius: 8px;
  overflow: hidden;
}
</style>
