<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>用户登录</h2>
        </div>
      </template>
      
      <el-form
        ref="loginForm"
        :model="formData"
        :rules="rules"
        label-width="80px"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="账号" prop="account">
          <el-input
            v-model="formData.account"
            placeholder="请输入邮箱/用户名/手机号"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">
            登录
          </el-button>
        </el-form-item>
        
        <div class="form-footer">
          <router-link to="/register">还没有账号？立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()
const loginForm = ref(null)
const loading = ref(false)

const formData = reactive({
  account: '',
  password: ''
})

const rules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginForm.value) return
  
  loading.value = true
  try {
    await loginForm.value.validate()
    const res = await axios.post('/api/login', formData)
    const responseData = res.data
    userStore.setUser(responseData.user)
    userStore.setToken(responseData.token)
    userStore.setAdmin(responseData.user.role && responseData.user.role.toUpperCase() === 'ADMIN')
    await nextTick()
    router.push('/')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  background-color: #f5f7fa;
}

.login-card {
  width: 100%;
  max-width: 400px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.submit-btn {
  width: 100%;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
}

.form-footer a {
  color: #409EFF;
  text-decoration: none;
}

.form-footer a:hover {
  text-decoration: underline;
}
</style> 
