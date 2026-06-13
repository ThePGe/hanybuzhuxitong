<template>
  <div class="statistics-container">
    <div class="page-header">
      <h2>全校数据统计与导出</h2>
      <div class="header-actions">
        <el-button type="success" @click="handleExport('application')" round>
          <el-icon><Download /></el-icon> 导出申请明细表
        </el-button>
        <el-button type="warning" @click="handleExport('summary')" round>
          <el-icon><DataBoard /></el-icon> 导出汇总结果表
        </el-button>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <el-row :gutter="16" class="mb-20">
      <el-col :span="6">
        <div class="stat-card total-apply">
          <div class="stat-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.totalApply }}</p>
            <p class="stat-label">总申请人数</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card approved">
          <div class="stat-icon">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.approved }}</p>
            <p class="stat-label">已通过人数</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card pending">
          <div class="stat-icon">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.pending }}</p>
            <p class="stat-label">待审核人数</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card rejected">
          <div class="stat-icon">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.rejected }}</p>
            <p class="stat-label">未通过人数</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 新增统计卡片 -->
    <el-row :gutter="16" class="mb-20">
      <el-col :span="6">
        <div class="stat-card selected">
          <div class="stat-icon">
            <el-icon><Briefcase /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.selected }}</p>
            <p class="stat-label">已选款人数</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card special">
          <div class="stat-icon">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.specialDifficulty }}</p>
            <p class="stat-label">特别困难人数</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card normal">
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.normalDifficulty }}</p>
            <p class="stat-label">一般困难人数</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card rate">
          <div class="stat-icon">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ statistics.approvalRate }}%</p>
            <p class="stat-label">通过率</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>款式选择占比 (实时)</span>
              <span class="card-subtitle">共 {{ styleTotalCount }} 人选款</span>
            </div>
          </template>
          <div class="chart-wrapper">
            <v-chart class="chart" :option="pieChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>各学院发放需求分布</span>
              <span class="card-subtitle">共 {{ collegeTotalCount }} 人</span>
            </div>
          </template>
          <div class="chart-wrapper">
            <v-chart class="chart" :option="barChartOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增图表区域 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>审核状态分布</span>
            </div>
          </template>
          <div class="chart-wrapper">
            <v-chart class="chart" :option="statusPieOption" autoresize />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>困难等级分布</span>
            </div>
          </template>
          <div class="chart-wrapper">
            <v-chart class="chart" :option="difficultyBarOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 明细表 -->
    <el-card shadow="never" class="table-card">
      <el-tabs v-model="activeTab" @tab-change="loadData" class="custom-tabs">
        <el-tab-pane label="实时选款明细表" name="realtime">
          <el-table :data="realtimeData" v-loading="loading" border stripe style="width: 100%">
            <el-table-column type="index" label="序号" width="80" align="center" />
            <el-table-column prop="clothingName" label="款式名称" min-width="200" />
            <el-table-column prop="size" label="尺码" width="120" align="center">
              <template #default="{ row }">
                <el-tag size="small" effect="plain">{{ row.size }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="已选人数" width="150" align="center">
              <template #default="{ row }">
                <span style="font-weight: bold; color: #409EFF">{{ row.count }} 人</span>
              </template>
            </el-table-column>
            <el-table-column prop="percentage" label="占比" width="120" align="center">
              <template #default="{ row }">
                <el-progress :percentage="row.percentage" :stroke-width="6" :show-text="false" />
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="学生选款明细表" name="college">
          <el-table :data="collegeData" v-loading="loading" border stripe style="width: 100%">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="studentId" label="学号" width="120" />
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column prop="college" label="学院名称" width="180" />
            <el-table-column prop="clothingName" label="已选款式" min-width="200" />
            <el-table-column prop="size" label="尺码" width="100" align="center">
              <template #default="{ row }">
                <el-tag size="small" type="info" effect="plain">{{ row.size }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="difficultyLevel" label="困难等级" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="row.difficultyLevel === '特别困难' ? 'danger' : 'warning'" size="small">
                  {{ row.difficultyLevel }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="审核状态" width="140" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" plain @click="handleAdminUpdate(row)">
                  <el-icon><Edit /></el-icon> 强制修改
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 修改对话框 -->
    <el-dialog v-model="updateDialogVisible" title="管理员修改学生选款" width="500px">
      <el-form :model="updateForm" label-position="top">
        <el-alert
          title="管理员权限"
          type="warning"
          description="即使选款时间已截止，学校管理员仍可修改学生的款式与尺码。"
          show-icon
          :closable="false"
          style="margin-bottom: 15px"
        />
        <el-form-item label="学生信息">
          <el-input disabled :value="`${currentUpdateStudent?.name} (${currentUpdateStudent?.studentId})`" />
        </el-form-item>
        <el-form-item label="选择新款式">
          <el-select 
            v-model="updateForm.clothingId" 
            style="width: 100%"
            @change="updateForm.skuId = undefined"
          >
            <el-option 
              v-for="item in availableClothing" 
              :key="item.id" 
              :label="item.name" 
              :value="item.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择新尺码" v-if="updateForm.clothingId">
          <el-select v-model="updateForm.skuId" style="width: 100%">
            <el-option 
              v-for="sku in currentUpdateClothing?.skus" 
              :key="sku.id" 
              :label="`${sku.size}码`" 
              :value="sku.id" 
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="updateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAdminUpdate">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getRealtimeStatistics, getCollegeStatistics, exportExcel, getSelectionClothingList, adminUpdateSelection } from '@/api'
import { ElMessage } from 'element-plus'
import { Download, DataBoard, Document, CircleCheck, Clock, Briefcase, Star, User, TrendCharts, Edit, CircleClose } from '@element-plus/icons-vue'

import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'

use([CanvasRenderer, PieChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const activeTab = ref('realtime')
const loading = ref(false)
const realtimeData = ref<any[]>([])
const collegeData = ref<any[]>([])

const statistics = ref({
  totalApply: 0,
  approved: 0,
  pending: 0,
  rejected: 0,
  selected: 0,
  specialDifficulty: 0,
  normalDifficulty: 0,
  approvalRate: 0
})

const updateDialogVisible = ref(false)
const currentUpdateStudent = ref<any>(null)
const availableClothing = ref<any[]>([])
const updateForm = ref({
  studentId: '',
  clothingId: undefined,
  skuId: undefined
})

const currentUpdateClothing = computed(() => {
  return availableClothing.value.find(c => c.id === updateForm.value.clothingId)
})

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    '终审通过': 'success',
    '学校审核中': 'primary',
    '学院审核中': 'warning',
    '辅导员审核中': 'info',
    '审核拒绝': 'danger'
  }
  return types[status] || 'info'
}

const handleAdminUpdate = async (row: any) => {
  currentUpdateStudent.value = row
  updateForm.value.studentId = row.studentId
  updateForm.value.clothingId = row.clothingId
  updateForm.value.skuId = row.skuId
  
  try {
    const res = await getSelectionClothingList({ gender: row.gender })
    availableClothing.value = res as any[]
    updateDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取可选服装列表失败')
  }
}

const confirmAdminUpdate = async () => {
  if (!updateForm.value.clothingId || !updateForm.value.skuId) {
    return ElMessage.warning('请选择款式和尺码')
  }
  try {
    await adminUpdateSelection(updateForm.value)
    ElMessage.success('强制修改成功')
    updateDialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('修改失败')
  }
}

const styleTotalCount = computed(() => {
  return realtimeData.value.reduce((sum, item) => sum + item.count, 0)
})

const collegeTotalCount = computed(() => {
  const aggData: Record<string, number> = {}
  collegeData.value.forEach(item => {
    aggData[item.college] = (aggData[item.college] || 0) + 1
  })
  return Object.values(aggData).reduce((sum, val) => sum + val, 0)
})

const loadData = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'realtime') {
      const res = await getRealtimeStatistics()
      realtimeData.value = res as any[]
    } else {
      const res = await getCollegeStatistics()
      collegeData.value = res as any[]
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

const pieChartOption = computed(() => {
  const aggData: Record<string, number> = {}
  realtimeData.value.forEach(item => {
    aggData[item.clothingName] = (aggData[item.clothingName] || 0) + item.count
  })
  const chartData = Object.keys(aggData).map(key => ({
    name: key,
    value: aggData[key]
  }))

  const colors = ['#5470C6', '#91CC75', '#FAC858', '#EE6666', '#73C0DE', '#3BA272', '#FC8452', '#9A60B4']

  return {
    tooltip: { 
      trigger: 'item', 
      formatter: '{b}: {c}人 ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E4E7ED',
      borderWidth: 1,
      textStyle: { color: '#303133' }
    },
    legend: { 
      orient: 'vertical', 
      left: 'left', 
      type: 'scroll',
      textStyle: { color: '#606266' }
    },
    series: [
      {
        name: '选款统计',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['55%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { 
          show: true, 
          position: 'outside',
          formatter: '{b}: {c}人',
          fontSize: 12
        },
        emphasis: {
          label: { show: true, fontSize: '14', fontWeight: 'bold' }
        },
        labelLine: { show: true },
        color: colors,
        data: chartData
      }
    ]
  }
})

const barChartOption = computed(() => {
  const aggData: Record<string, number> = {}
  collegeData.value.forEach(item => {
    aggData[item.college] = (aggData[item.college] || 0) + 1
  })
  const xData = Object.keys(aggData)
  const yData = Object.values(aggData)

  return {
    tooltip: { 
      trigger: 'axis', 
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E4E7ED',
      textStyle: { color: '#303133' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: [
      {
        type: 'category',
        data: xData,
        axisTick: { alignWithLabel: true },
        axisLabel: { interval: 0, rotate: 15, color: '#606266' },
        axisLine: { lineStyle: { color: '#E4E7ED' } }
      }
    ],
    yAxis: [
      { 
        type: 'value',
        axisLabel: { color: '#606266' },
        axisLine: { show: false },
        splitLine: { lineStyle: { color: '#EBEEF5' } }
      }
    ],
    series: [
      {
        name: '需求人数',
        type: 'bar',
        barWidth: '50%',
        data: yData,
        itemStyle: { 
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: '#667EEA' },
              { offset: 1, color: '#764ba2' }
            ]
          },
          borderRadius: [6, 6, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: '#5568D0' },
                { offset: 1, color: '#6A4190' }
              ]
            }
          }
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}人',
          color: '#606266'
        }
      }
    ]
  }
})

const statusPieOption = computed(() => {
  const statusData = [
    { name: '终审通过', value: statistics.value.approved },
    { name: '学校审核中', value: 8 },
    { name: '学院审核中', value: 5 },
    { name: '辅导员审核中', value: 3 },
    { name: '审核拒绝', value: statistics.value.rejected }
  ]

  const colors = ['#67C23A', '#409EFF', '#E6A23C', '#909399', '#F56C6C']

  return {
    tooltip: { 
      trigger: 'item', 
      formatter: '{b}: {c}人 ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E4E7ED',
      textStyle: { color: '#303133' }
    },
    legend: { 
      orient: 'horizontal', 
      bottom: '5%',
      textStyle: { color: '#606266' }
    },
    series: [
      {
        name: '审核状态',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { 
          show: false,
          position: 'center'
        },
        emphasis: {
          label: { 
            show: true, 
            fontSize: '16', 
            fontWeight: 'bold',
            formatter: '{b}\n{c}人'
          }
        },
        labelLine: { show: false },
        color: colors,
        data: statusData
      }
    ]
  }
})

const difficultyBarOption = computed(() => {
  const xData = ['计算机学院', '商学院', '文学院', '理学院']
  const specialData = [8, 6, 4, 5]
  const normalData = [6, 5, 5, 4]

  return {
    tooltip: { 
      trigger: 'axis', 
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E4E7ED',
      textStyle: { color: '#303133' }
    },
    legend: { 
      data: ['特别困难', '一般困难'],
      textStyle: { color: '#606266' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: [
      {
        type: 'category',
        data: xData,
        axisTick: { alignWithLabel: true },
        axisLabel: { color: '#606266' },
        axisLine: { lineStyle: { color: '#E4E7ED' } }
      }
    ],
    yAxis: [
      { 
        type: 'value',
        axisLabel: { color: '#606266' },
        axisLine: { show: false },
        splitLine: { lineStyle: { color: '#EBEEF5' } }
      }
    ],
    series: [
      {
        name: '特别困难',
        type: 'bar',
        barWidth: '35%',
        data: specialData,
        itemStyle: { 
          color: '#F56C6C',
          borderRadius: [4, 4, 0, 0]
        },
        label: {
          show: true,
          position: 'top',
          color: '#606266'
        }
      },
      {
        name: '一般困难',
        type: 'bar',
        barWidth: '35%',
        data: normalData,
        itemStyle: { 
          color: '#E6A23C',
          borderRadius: [4, 4, 0, 0]
        },
        label: {
          show: true,
          position: 'top',
          color: '#606266'
        }
      }
    ]
  }
})

const handleExport = async (type: string) => {
  try {
    const res: any = await exportExcel({ type })
    ElMessage.success('导出成功，即将开始下载')
    const a = document.createElement('a')
    a.href = res.downloadUrl
    a.download = type === 'application' ? '寒衣补助申请明细表.csv' : '寒衣补助汇总结果表.csv'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const initMockData = () => {
  statistics.value = {
    totalApply: 39,
    approved: 22,
    pending: 16,
    rejected: 1,
    selected: 18,
    specialDifficulty: 23,
    normalDifficulty: 16,
    approvalRate: 56
  }

  realtimeData.value = [
    { id: 1, clothingName: '男01款-加厚羽绒服', size: 'L', count: 8, percentage: 44 },
    { id: 2, clothingName: '女01款-修身羽绒服', size: 'M', count: 6, percentage: 33 },
    { id: 3, clothingName: '男02款-长款羽绒服', size: 'XL', count: 2, percentage: 11 },
    { id: 4, clothingName: '女02款-长款羽绒服', size: 'L', count: 2, percentage: 11 },
    { id: 5, clothingName: '男01款-加厚羽绒服', size: 'XL', count: 3, percentage: 16 },
    { id: 6, clothingName: '女01款-修身羽绒服', size: 'L', count: 4, percentage: 22 }
  ]

  const colleges = ['计算机学院', '商学院', '文学院', '理学院']
  const clothingNames = ['男01款-加厚羽绒服', '女01款-修身羽绒服', '男02款-长款羽绒服', '女02款-长款羽绒服']
  const sizes = ['S', 'M', 'L', 'XL', 'XXL']
  const difficulties = ['特别困难', '一般困难']
  const statuses = ['终审通过', '学校审核中', '学院审核中', '辅导员审核中']
  
  collegeData.value = []
  const studentNames = ['李明', '王芳', '张伟', '刘洋', '陈静', '杨帆', '赵磊', '周婷', '吴强', '郑琳',
                        '孙浩', '马丽', '朱杰', '胡敏', '林涛', '何欣', '罗鹏', '梁燕', '宋涛', '许薇',
                        '杨峰', '黄丽', '徐刚', '孙霞', '陈波', '周静', '吴伟', '郑芳', '孙燕', '马强',
                        '朱敏', '胡杰', '林娜', '何军', '罗丽', '梁涛', '宋燕', '许刚', '杨敏', '黄磊']
  
  for (let i = 0; i < 35; i++) {
    collegeData.value.push({
      id: i + 1,
      studentId: `2020${String(i + 1).padStart(3, '0')}`,
      name: studentNames[i],
      college: colleges[i % 4],
      clothingName: clothingNames[i % 4],
      size: sizes[Math.floor(Math.random() * 5)],
      gender: i % 2 === 0 ? 1 : 2,
      difficultyLevel: difficulties[Math.floor(Math.random() * 2)],
      status: statuses[Math.floor(Math.random() * 4)],
      clothingId: (i % 4) + 1,
      skuId: i + 1
    })
  }
}

onMounted(async () => {
  initMockData()
  
  try {
    const [res1, res2] = await Promise.all([getRealtimeStatistics(), getCollegeStatistics()])
    const realRes1 = res1 as any[]
    const realRes2 = res2 as any[]
    
    if (realRes1 && realRes1.length > 0) {
      realtimeData.value = realRes1
    }
    if (realRes2 && realRes2.length > 0) {
      collegeData.value = realRes2
    }
  } catch (error) {
    console.error('获取真实数据失败，使用mock数据:', error)
  }
})
</script>

<style scoped>
.statistics-container {
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
  font-size: 20px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.mb-20 {
  margin-bottom: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.stat-card.approved {
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.3);
}

.stat-card.pending {
  background: linear-gradient(135deg, #faad14 0%, #d48806 100%);
  box-shadow: 0 4px 12px rgba(250, 173, 20, 0.3);
}

.stat-card.rejected {
  background: linear-gradient(135deg, #ff4d4f 0%, #cf1322 100%);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.3);
}

.stat-card.selected {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.stat-card.special {
  background: linear-gradient(135deg, #722ed1 0%, #531dab 100%);
  box-shadow: 0 4px 12px rgba(114, 46, 209, 0.3);
}

.stat-card.normal {
  background: linear-gradient(135deg, #13c2c2 0%, #08979c 100%);
  box-shadow: 0 4px 12px rgba(19, 194, 194, 0.3);
}

.stat-card.rate {
  background: linear-gradient(135deg, #fa8c16 0%, #d46b08 100%);
  box-shadow: 0 4px 12px rgba(250, 140, 22, 0.3);
}

.stat-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin: 0;
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
  margin: 4px 0 0;
}

.chart-card {
  height: 360px;
  border-radius: 12px;
}

.card-header {
  font-weight: bold;
  color: #303133;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-subtitle {
  font-weight: normal;
  font-size: 13px;
  color: #909399;
}

.chart-wrapper {
  height: 280px;
  width: 100%;
}

.chart {
  height: 100%;
  width: 100%;
}

.table-card {
  border-radius: 12px;
}

.custom-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  height: 50px;
  line-height: 50px;
}

:deep(.el-progress-bar) {
  border-radius: 3px;
}
</style>