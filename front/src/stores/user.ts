import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  // 模拟全局批次状态
  const batchStatus = ref({
    isApplyEnded: false,
    isSelectEnded: false
  })

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setRole = (newRole: string) => {
    role.value = newRole
    localStorage.setItem('role', newRole)
  }

  const setUserInfo = (info: any) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  const setBatchStatus = (status: any) => {
    batchStatus.value = status
  }

  const logout = () => {
    token.value = ''
    role.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    role,
    userInfo,
    batchStatus,
    setToken,
    setRole,
    setUserInfo,
    setBatchStatus,
    logout
  }
})
