<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="logo-wrapper">
          <img src="https://images.unsplash.com/photo-1544441893-675973e31985?w=200&q=80" alt="logo" class="logo-icon" />
          <h1>寒衣补助系统</h1>
        </div>
        <p class="subtitle">温暖校园，关爱学子</p>
      </div>
      <div class="login-right">
        <el-card class="login-card" shadow="never">
          <div class="login-header">
            <h2>欢迎登录</h2>
            <p>请输入您的账号密码</p>
          </div>
          
          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" size="large">
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="账号（学号/工号）" 
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="密码（学生默认为身份证后六位）" 
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin" round>
                登 录
              </el-button>
              <div style="text-align: center; width: 100%; margin-top: 10px;">
                <el-button link type="primary" @click="router.push('/register')">没有账号？去注册</el-button>
              </div>
            </el-form-item>
          </el-form>
          
          <el-divider>测试账号说明</el-divider>
          <div class="tips">
            <el-tag type="info" effect="plain" class="tip-tag">学生: student</el-tag>
            <el-tag type="success" effect="plain" class="tip-tag">辅导员: teacher</el-tag>
            <el-tag type="warning" effect="plain" class="tip-tag">学院: college</el-tag>
            <el-tag type="danger" effect="plain" class="tip-tag">学校: school</el-tag>
            <el-tag type="primary" effect="dark" class="tip-tag">超管: 9447</el-tag>
          </div>
          <div style="text-align: center; margin-top: 10px; font-size: 12px; color: #909399;">
            所有账号测试密码默认均为: <strong>sch123456</strong><br>
            <span style="color: #F56C6C; margin-top: 4px; display: inline-block;">
              注意：学校用户(school)需由9447加入白名单才可登录
            </span>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()

const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: 'sch123456'
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        userStore.setToken(res.token)
        userStore.setRole(res.role)
        userStore.setUserInfo(res.userInfo)
        ElMessage.success('登录成功')
        router.push('/home')
      } catch (error: any) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-box {
  width: 900px;
  height: 500px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  display: flex;
  overflow: hidden;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.logo-wrapper {
  margin-bottom: 20px;
}

.logo-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 4px solid rgba(255, 255, 255, 0.3);
  margin-bottom: 10px;
}

.logo-wrapper h1 {
  font-size: 28px;
  margin: 0;
  letter-spacing: 2px;
}

.subtitle {
  font-size: 16px;
  opacity: 0.8;
  margin-bottom: 40px;
}

.login-right {
  flex: 1;
  padding: 40px;
  display: flex;
  align-items: center;
}

.login-card {
  width: 100%;
  border: none;
  background: transparent;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.login-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.login-btn {
  width: 100%;
  font-size: 16px;
  letter-spacing: 2px;
  margin-top: 10px;
}

.tips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.tip-tag {
  border-radius: 12px;
}
</style>
