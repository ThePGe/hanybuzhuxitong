<template>
  <div class="clothing-container">
    <div class="page-header">
      <h2>服装款式与库存配置</h2>
      <el-button type="primary" @click="handleCreate" size="large">
        <el-icon><Plus /></el-icon> 新增服装款式
      </el-button>
    </div>

    <div v-loading="loading">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in tableData" :key="item.id" class="mb-24">
          <el-card class="clothing-card" :body-style="{ padding: '0px' }" shadow="hover">
            <div class="image-wrapper">
              <el-image 
                :src="item.imageUrl" 
                class="clothing-image" 
                fit="cover"
                :preview-src-list="[item.imageUrl]"
              />
              <div class="gender-tag" :class="item.gender === 'M' ? 'male' : 'female'">
                {{ item.gender === 'M' ? '男款' : '女款' }}
              </div>
            </div>
            <div class="card-content">
              <h3 class="clothing-title" :title="item.name">{{ item.name }}</h3>
              <div class="size-info">
                <el-icon><InfoFilled /></el-icon>
                <span class="info-text">尺码推荐：</span>
                <p class="size-desc">{{ item.sizeRecommend || '暂无推荐' }}</p>
              </div>
              <div class="card-actions">
                <el-button type="primary" plain @click="handleConfigSku(item)" class="action-btn">
                  <el-icon><Setting /></el-icon> 配置SKU尺码
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-empty v-if="!loading && tableData.length === 0" description="暂无服装款式配置，请先添加" />
    </div>

    <el-dialog v-model="createDialogVisible" title="新增服装款式" width="500px">
      <el-form :model="form" label-width="100px" size="large">
        <el-form-item label="款式名称">
          <el-input v-model="form.name" placeholder="例如：波司登羽绒服" />
        </el-form-item>
        <el-form-item label="适用性别">
          <el-radio-group v-model="form.gender">
            <el-radio-button label="M">男款</el-radio-button>
            <el-radio-button label="F">女款</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="尺码推荐">
          <el-input v-model="form.sizeRecommend" type="textarea" :rows="3" placeholder="例如：M(160-165) L(165-170)" />
        </el-form-item>
        <el-form-item label="款式图片">
          <el-input v-model="form.imageUrl" placeholder="输入图片URL（模拟）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmCreate">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="skuDialogVisible" title="配置SKU (具体尺码库存)" width="600px">
      <el-button type="success" @click="addSkuLine" style="margin-bottom: 15px;">
        <el-icon><Plus /></el-icon> 添加尺码
      </el-button>
      <el-table :data="skuList" border size="default" stripe>
        <el-table-column label="尺码 (Size)">
          <template #default="{ row }">
            <el-input v-model="row.size" placeholder="如：XL" />
          </template>
        </el-table-column>
        <el-table-column label="商品编码 (SKU Code)">
          <template #default="{ row }">
            <el-input v-model="row.skuCode" placeholder="如：M01-XL" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ $index }">
            <el-button type="danger" link @click="skuList.splice($index, 1)">
              <el-icon><Delete /></el-icon> 移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="skuDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmConfigSku">保存配置</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getClothingList, createClothing, configClothingSku, getClothingSkuList } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<any[]>([])

const createDialogVisible = ref(false)
const form = reactive({
  name: '',
  gender: 'M',
  imageUrl: '',
  sizeRecommend: ''
})

const skuDialogVisible = ref(false)
const currentClothingId = ref()
const skuList = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getClothingList()
    tableData.value = res as any[]
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  createDialogVisible.value = true
}

const confirmCreate = async () => {
  try {
    await createClothing(form)
    ElMessage.success('创建成功')
    createDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const handleConfigSku = async (row: any) => {
  currentClothingId.value = row.id
  try {
    const res = await getClothingSkuList(row.id)
    skuList.value = res as any[]
    if (skuList.value.length === 0) {
      // 默认给几个空行
      skuList.value = [
        { size: 'S', skuCode: `SKU-${row.id}-S` },
        { size: 'M', skuCode: `SKU-${row.id}-M` },
        { size: 'L', skuCode: `SKU-${row.id}-L` },
      ]
    }
  } catch (error) {
    ElMessage.error('获取SKU列表失败')
  }
  skuDialogVisible.value = true
}

const addSkuLine = () => {
  skuList.value.push({ size: '', skuCode: '' })
}

const confirmConfigSku = async () => {
  try {
    await configClothingSku({
      clothingId: currentClothingId.value,
      skus: skuList.value
    })
    ElMessage.success('SKU配置保存成功')
    skuDialogVisible.value = false
  } catch (error) {
    ElMessage.error('配置失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.clothing-container {
  padding: 10px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-header h2 {
  margin: 0;
  color: #303133;
}
.mb-24 {
  margin-bottom: 24px;
}
.clothing-card {
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s;
}
.clothing-card:hover {
  transform: translateY(-5px);
}
.image-wrapper {
  position: relative;
  width: 100%;
  height: 240px;
  background-color: #f5f7fa;
}
.clothing-image {
  width: 100%;
  height: 100%;
}
.gender-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
  z-index: 1;
}
.gender-tag.male {
  background-color: #409EFF;
}
.gender-tag.female {
  background-color: #F56C6C;
}
.card-content {
  padding: 16px;
}
.clothing-title {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.size-info {
  font-size: 13px;
  color: #909399;
  margin-bottom: 16px;
  height: 40px;
}
.info-text {
  margin-left: 4px;
}
.size-desc {
  margin: 4px 0 0 0;
  color: #606266;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-actions {
  text-align: center;
}
.action-btn {
  width: 100%;
}
</style>
