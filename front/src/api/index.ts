import request from '@/utils/request'

export const login = (data: any) => {
  if (data.username.startsWith('school')) {
    return request.post('/auth/sso/login', data) as Promise<any>
  }
  return request.post('/auth/login', data) as Promise<any>
}

export const register = (data: any) => request.post('/auth/register', data) as Promise<any>

// 白名单相关接口调用真实接口
export const getWhitelist = () => request.get('/superadmin/whitelist') as Promise<any>
export const addWhitelist = (username: string) => request.post('/superadmin/whitelist', { username }) as Promise<any>
export const removeWhitelist = (username: string) => request.delete(`/superadmin/whitelist/${username}`) as Promise<any>

export const getRegisterApplyList = () => request.get('/superadmin/register-apply/list') as Promise<any>
export const reviewRegisterApply = (data: any) => request.post('/superadmin/register-apply/review', data) as Promise<any>

export const getUserInfo = () => request.get('/auth/userInfo') as Promise<any>

export const unbindWechat = (data: any) => request.post('/student/unbind', data) as Promise<any>

/** 二、批次与全局配置模块 **/
export const getBatchInfo = () => request.get('/school/batch/info') as Promise<any>

export const createBatch = (data: any) => request.post('/school/batch', data) as Promise<any>

export const updateBatchTime = (data: any) => request.put('/school/batch/time', data) as Promise<any>

export const getActiveBatchStatus = () => request.get('/school/batch/active') as Promise<any>

/** 三、服装款式与SKU模块 **/
export const uploadFile = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData) as Promise<any>
}

export const createClothing = (data: any) => request.post('/school/clothing', data) as Promise<any>

export const configClothingSku = (data: any) => request.post('/school/clothing/sku/config', data) as Promise<any>

export const getClothingSkuList = (clothingId: number) => request.get(`/school/clothing/sku/${clothingId}`) as Promise<any>

export const getClothingList = (params?: any) => request.get('/school/clothing/list', { params }) as Promise<any>

/** 四、学生申请与三级审核模块 **/
export const submitApplication = (data: any) => request.post('/student/apply', data) as Promise<any>

export const getAuditList = (params: any) => request.post('/audit/list', params) as Promise<any>

export const approveApplication = (data: any) => request.post('/audit/approve', data) as Promise<any>

export const submitToNext = (data?: any) => request.post('/audit/submit', data) as Promise<any>

export const notifyStudents = () => request.post('/audit/notify') as Promise<any>

/** 五、选款与尺码登记模块 **/
export const getSelectionClothingList = (params?: any) => request.get('/student/selection/clothing/list', { params }) as Promise<any>

export const submitSelection = (data: any) => request.post('/student/selection', data) as Promise<any>

export const adminUpdateSelection = (data: any) => request.post('/school/clothing/selection/update', data) as Promise<any>

/** 六、数据统计与导出模块 **/
export const getRealtimeStatistics = () => request.get('/statistics/realtime') as Promise<any>

export const getCollegeStatistics = () => request.get('/statistics/college') as Promise<any>

export const exportExcel = (params: any) => request.get('/statistics/export', { params }) as Promise<any>

