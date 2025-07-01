<!--
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-16 10:40:07
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-22 23:49:29
 * @FilePath: \Sofeware_Demo\src\views\Profile.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="profile">
    <h1>个人中心</h1>
    <el-form
      ref="profileForm"
      :model="formData"
      :rules="rules"
      label-width="100px"
      @submit.prevent="handleSubmit"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="formData.username" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="formData.phone" />
      </el-form-item>
      <el-form-item label="原密码" prop="oldPwd">
        <el-input v-model="formData.oldPwd" type="password" show-password placeholder="如需修改密码请输入原密码" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPwd">
        <el-input v-model="formData.newPwd" type="password" show-password placeholder="如需修改密码请输入新密码" />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPwd">
        <el-input v-model="formData.confirmPwd" type="password" show-password placeholder="请再次输入新密码" />
      </el-form-item>
      <el-form-item label="收货地址">
        <el-input
          v-model="formData.address"
          disabled
          :placeholder="formData.address ? '请在下方管理收货地址' : '暂无收货地址，请在下方添加'"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit">保存修改</el-button>
      </el-form-item>
    </el-form>

    <!-- 地址管理模块 -->
    <el-card class="address-card" style="margin-top: 32px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>收货地址管理</span>
          <el-button type="primary" size="small" @click="openAddDialog">新增地址</el-button>
        </div>
      </template>
      <el-table :data="addressList" style="width: 100%">
        <el-table-column label="地址" prop="address">
          <template #default="{ row }">
            <span v-if="row.isDefault" style="color: #409EFF; font-weight: bold;">[默认]</span>
            {{ row.address }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row, $index }">
            <el-button size="small" @click="openEditDialog(row, $index)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteAddress($index)" :disabled="!row.canDelete">删除</el-button>
            <el-button size="small" type="success" v-if="!row.isDefault" @click="setDefault($index)">设为默认</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 地址新增/编辑弹窗 -->
    <el-dialog v-model="addressDialogVisible" :title="addressDialogTitle" width="400px">
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="80px">
        <el-form-item label="收货地址" prop="address">
          <el-input v-model="addressForm.address" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const userStore = useUserStore()
const profileForm = ref(null)

const formData = reactive({
  username: '',
  email: '',
  phone: '',
  address: '',
  oldPwd: '',
  newPwd: '',
  confirmPwd: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  oldPwd: [
    { validator: (rule, value, callback) => {
      if (formData.newPwd) {
        if (!value) {
          callback(new Error('请输入原密码'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  newPwd: [
    { validator: (rule, value, callback) => {
      if (formData.oldPwd || formData.confirmPwd) {
        if (!value) {
          callback(new Error('请输入新密码'))
        } else if (value.length < 6) {
          callback(new Error('密码长度不能少于6位'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  confirmPwd: [
    { validator: (rule, value, callback) => {
      if (formData.newPwd) {
        if (!value) {
          callback(new Error('请确认新密码'))
        } else if (value !== formData.newPwd) {
          callback(new Error('两次输入的新密码不一致'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

// 地址管理相关
const addressList = ref([])

const addressDialogVisible = ref(false)
const addressDialogTitle = ref('')
const addressForm = reactive({ address: '', isDefault: false })
const addressFormRef = ref(null)
const addressRules = { address: [{ required: true, message: '请输入收货地址', trigger: 'blur' }] }
let editIndex = -1

const openAddDialog = () => {
  addressDialogTitle.value = '新增地址'
  addressForm.address = ''
  addressForm.isDefault = false
  editIndex = -1
  addressDialogVisible.value = true
  nextTick(() => addressFormRef.value && addressFormRef.value.clearValidate())
}
const openEditDialog = (row, idx) => {
  addressDialogTitle.value = '编辑地址'
  addressForm.address = row.address
  addressForm.isDefault = row.isDefault
  editIndex = idx
  addressDialogVisible.value = true
  nextTick(() => addressFormRef.value && addressFormRef.value.clearValidate())
}
const fetchAddressList = async () => {
  const userId = userStore.user?.id
  if (!userId) return
  try {
    const res = await axios.get(`/api/addresses/user/${userId}`)
    addressList.value = res.data.data
    syncDefaultAddressToForm()
  } catch (error) {
    console.error('Fetch address list error:', error)
    if (error.response?.status === 403) {
      ElMessage.error('获取地址列表失败：权限不足')
    } else {
      ElMessage.error('获取地址列表失败：' + (error.response?.data?.message || error.message))
    }
  }
}
const saveAddress = async () => {
  addressFormRef.value.validate(async (valid) => {
    if (!valid) return
    let userId = userStore.user?.id
    if (!userId) {
      await refreshUserData()
      userId = userStore.user?.id
    }
    if (!userId) {
      ElMessage.error('用户信息获取失败')
      return
    }
    try {
      const payload = { ...addressForm, userId }
      if (editIndex === -1) {
        const response = await axios.post('/api/addresses', payload)
        ElMessage.success('地址添加成功')
      } else {
        const response = await axios.put(`/api/addresses`, { ...addressList.value[editIndex], ...payload })
        ElMessage.success('地址更新成功')
      }
      addressDialogVisible.value = false
      await fetchAddressList()
    } catch (error) {
      if (error.response?.status === 403) {
        ElMessage.error('权限不足，请重新登录')
      } else {
        ElMessage.error('保存失败：' + (error.response?.data?.message || error.message))
      }
    }
  })
}
const deleteAddress = async (idx) => {
  if (!addressList.value[idx].canDelete) {
    ElMessage.error('该地址已被订单引用，无法删除');
    return;
  }
  await axios.delete(`/api/addresses/${addressList.value[idx].id}`)
  await fetchAddressList()
}
const setDefault = async (idx) => {
  const userId = userStore.user?.id
  if (!userId) return
  await axios.put(`/api/addresses/user/${userId}/default/${addressList.value[idx].id}`)
  await fetchAddressList()
}
// 同步默认地址到个人信息表单
const syncDefaultAddressToForm = () => {
  const def = addressList.value.find(addr => addr.isDefault)
  formData.address = def ? def.address : ''
}

onMounted(async () => {
  addressList.value = []
  await refreshUserData()
  await fetchAddressList()
})

watch(() => userStore.user, async (newUser, oldUser) => {
  addressList.value = []
  if (newUser?.id !== oldUser?.id) {
    await refreshUserData()
    await fetchAddressList()
  }
})

const handleSubmit = async () => {
  if (!profileForm.value) return
  await profileForm.value.validate(async (valid) => {
    if (valid) {
      try {
        const userId = userStore.user?.id || localStorage.getItem('userId')
        if (!userId) {
          ElMessage.error('用户信息获取失败')
          return
        }

        // 构造要提交的数据
        const submitData = {
          username: formData.username,
          email: formData.email,
          phone: formData.phone
        }

        // 只有填写了新密码时才提交密码相关字段
        if (formData.newPwd) {
          submitData.oldPwd = formData.oldPwd
          submitData.newPwd = formData.newPwd
          submitData.confirmPwd = formData.confirmPwd
        }

        // 提交到后端
        await axios.put(`/api/user/${userId}`, submitData)
        
        if (formData.newPwd) {
          ElMessage.success('密码修改成功！')
          // 只有在成功修改密码后才清空密码字段
          formData.oldPwd = ''
          formData.newPwd = ''
          formData.confirmPwd = ''
        } else {
          ElMessage.success('个人信息保存成功！')
        }

        // 重新获取用户数据刷新页面内容
        await refreshUserData()
        
      } catch (error) {
        ElMessage.error('保存失败：' + (error.response?.data?.message || error.message))
        console.error('保存失败:', error)
      }
    } else {
      ElMessage.error('请检查表单')
    }
  })
}

// 重新获取用户数据的函数
const refreshUserData = async () => {
  const userId = userStore.user?.id || localStorage.getItem('userId')
  if (userId) {
    try {
      const res = await axios.get(`/api/user/${userId}`)
      const user = res.data.data
      userStore.setUser(user)
      formData.username = user.username || ''
      formData.email = user.email || ''
      formData.phone = user.phone || ''
      formData.oldPwd = ''
    } catch (error) {
      console.error('刷新用户数据失败:', error)
    }
  }
}
</script>

<style scoped>
.profile {
  padding: 20px;
}

.address-card {
  max-width: 600px;
  margin: 0 auto;
}
</style> 