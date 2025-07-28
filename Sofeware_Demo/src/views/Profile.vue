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
        <el-input v-model="formData.username" disabled />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="formData.phone" />
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
            {{ row.province }} {{ row.city }} {{ row.district }} {{ row.detailAddress }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteAddress(row.id)">删除</el-button>
            <el-button size="small" type="success" v-if="!row.isDefault" @click="setDefault(row.id)">设为默认</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 地址新增/编辑弹窗 -->
    <el-dialog v-model="addressDialogVisible" :title="addressDialogTitle" width="500px">
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="100px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" />
        </el-form-item>
        <el-form-item label="区/县" prop="district">
          <el-input v-model="addressForm.district" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="addressForm.detailAddress" />
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
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const userStore = useUserStore()

const formData = reactive({
  username: userStore.user?.username || '',
  email: userStore.user?.email || '',
  phone: userStore.user?.phone || ''
})

// Address Management
const addressList = ref([])
const addressDialogVisible = ref(false)
const addressDialogTitle = ref('')
const addressFormRef = ref(null)
let currentEditAddressId = null

const addressForm = reactive({
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

const addressRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区/县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const fetchAddressList = async () => {
  try {
    const userId = userStore.user?.id || localStorage.getItem('userId')
    if (!userId) {
      console.error('User ID not found, cannot fetch addresses.')
      return
    }
    const res = await axios.get(`/api/addresses/user/${userId}`)
    addressList.value = res.data.data || []
  } catch (error) {
    console.error('Fetch address list error:', error)
    ElMessage.error('获取地址列表失败')
  }
}

const openAddDialog = () => {
  addressDialogTitle.value = '新增地址'
  currentEditAddressId = null
  Object.assign(addressForm, { receiverName: '', phone: '', province: '', city: '', district: '', detailAddress: '', isDefault: false })
  addressDialogVisible.value = true
  nextTick(() => addressFormRef.value?.clearValidate())
}

const openEditDialog = (row) => {
  addressDialogTitle.value = '编辑地址'
  currentEditAddressId = row.id
  Object.assign(addressForm, row)
  addressDialogVisible.value = true
  nextTick(() => addressFormRef.value?.clearValidate())
}

const saveAddress = async () => {
  if (!addressFormRef.value) return
  await addressFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (currentEditAddressId) {
          // Update existing address
          await axios.put(`/api/addresses/${currentEditAddressId}`, addressForm)
          ElMessage.success('地址更新成功')
        } else {
          // Create new address
          await axios.post('/api/addresses', addressForm)
          ElMessage.success('地址添加成功')
        }
        addressDialogVisible.value = false
        await fetchAddressList()
      } catch (error) {
        console.error('Save address error:', error)
        ElMessage.error('保存地址失败')
      }
    }
  })
}

const deleteAddress = async (addressId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '警告', { type: 'warning' })
    await axios.delete(`/api/addresses/${addressId}`)
    ElMessage.success('地址已删除')
    await fetchAddressList()
  } catch (error) {
    console.error('Delete address error:', error.response?.data || error);
    if (error.response) {
        const errorMsg = error.response.data.error || '';
        if (errorMsg.includes('referenced by an order')) {
            ElMessage.warning('该地址已被订单引用，无法删除');
        } else if (errorMsg.includes('default address')) {
            ElMessage.warning('不能删除默认地址，请先设置其他地址为默认');
        } else {
            ElMessage.error(`删除失败: ${errorMsg}`);
        }
    } else {
        ElMessage.error('删除失败，请检查网络连接');
    }
  }
}

const setDefault = async (addressId) => {
  try {
    await axios.patch(`/api/addresses/${addressId}/default`)
    ElMessage.success('默认地址设置成功')
    await fetchAddressList()
  } catch (error) {
    console.error('Set default address error:', error)
    ElMessage.error('设置默认地址失败')
  }
}

const handleSubmit = async () => {
  try {
    await axios.put('/api/user/profile', formData)
    ElMessage.success('个人信息更新成功')
    // 更新用户store中的信息
    userStore.setUser({
      ...userStore.user,
      email: formData.email,
      phone: formData.phone
    })
  } catch (error) {
    ElMessage.error('个人信息更新失败')
    console.error('更新失败:', error)
  }
}

onMounted(() => {
  userStore.fetchUserInfo().then(() => {
    formData.username = userStore.user?.username || ''
    formData.email = userStore.user?.email || ''
    formData.phone = userStore.user?.phone || ''
  })
  fetchAddressList()
})

</script>

<style scoped>
.profile {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
</style>