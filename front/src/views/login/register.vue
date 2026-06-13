<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="logo-wrapper">
          <img src="https://images.unsplash.com/photo-1544441893-675973e31985?w=200&q=80" alt="logo" class="logo-icon" />
          <h1>寒衣补助系统</h1>
        </div>
        <p class="subtitle">欢迎注册加入我们</p>
      </div>
      <div class="login-right">
        <el-card class="login-card" shadow="never">
          <div class="login-header">
            <h2>注册账号</h2>
            <p>填写信息后需超管审核</p>
          </div>
          
          <el-form :model="registerForm" :rules="rules" ref="registerFormRef" size="default" label-width="80px">
            <el-form-item label="角色" prop="role">
              <el-select v-model="registerForm.role" placeholder="请选择角色" style="width: 100%">
                <el-option label="学校管理员" :value="0"></el-option>
                <el-option label="学院管理员" :value="1"></el-option>
                <el-option label="辅导员" :value="2"></el-option>
                <el-option label="学生" :value="3"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="账号" prop="username">
              <el-input v-model="registerForm.username" placeholder="学号/工号" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="registerForm.name" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="性别" prop="gender" v-if="registerForm.role === 3">
              <el-radio-group v-model="registerForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="学院" prop="collegeName" v-if="[1, 2, 3].includes(registerForm.role)">
              <el-input v-model="registerForm.collegeName" placeholder="如: 计算机学院" />
            </el-form-item>
            <el-form-item label="班级" prop="className" v-if="[2, 3].includes(registerForm.role)">
              <el-input v-model="registerForm.className" placeholder="如: 计科2001班" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleRegister" style="width: 100%">提交申请</el-button>
            </el-form-item>
            <div style="text-align: center; margin-top: 10px;">
              <el-button link type="primary" @click="router.push('/login')">已有账号？去登录</el-button>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  role: 3,
  username: '',
  password: '',
  name: '',
  gender: 1,
  collegeName: '',
  className: ''
})

const rules = {
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        await register(registerForm)
        ElMessage.success('注册申请已提交，请等待超管审核')
        router.push('/login')
      } catch (error: any) {
        ElMessage.error(error.message || '注册申请失败')
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
  height: 600px;
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
  padding: 20px 40px;
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
  margin-bottom: 20px;
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
</style>