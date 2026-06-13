import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// Request Interceptor
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response Interceptor
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // For our mock, we just return res directly if code is 200
    if (res.code === 200) {
      return res.data
    } else {
      ElMessage.error(res.msg || '请求错误')
      return Promise.reject(new Error(res.msg || 'Error'))
    }
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
