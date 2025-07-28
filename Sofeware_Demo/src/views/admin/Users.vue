<template>
  <div class="admin-users">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAddUser">
            添加用户
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchQuery"
            placeholder="搜索用户名/邮箱..."
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-select v-model="roleFilter" placeholder="用户角色" clearable @change="handleSearch">
            <el-option
              v-for="role in roles"
              :key="role.value"
              :label="role.label"
              :value="role.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            搜索
          </el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="username" label="用户名" width="120" />
        
        <el-table-column prop="email" label="邮箱" min-width="200" />
        
        <el-table-column prop="phone" label="手机号" width="120" />
        
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
            {{ row.createdAt ? new Date(row.createdAt).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEditUser(row)">
              编辑
            </el-button>
            <el-button link type="danger" @click="handleDeleteUser(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="userForm"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option
              v-for="role in roles"
              :key="role.value"
              :label="role.label"
              :value="role.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="userForm.password" type="password" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitForm">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 搜索和筛选
const searchQuery = ref('')
const roleFilter = ref('')

const roles = [
  { value: '', label: '全部角色' },
  { value: 'admin', label: '管理员' },
  { value: 'user', label: '普通用户' }
]

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 用户列表
const users = ref([])

// 表单对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

const userForm = ref({
  username: '',
  email: '',
  phone: '',
  role: '',
  password: ''
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

const getRoleType = (role) => {
  return role?.toLowerCase() === 'admin' ? 'danger' : 'info'
}

const getRoleLabel = (role) => {
  return role?.toLowerCase() === 'admin' ? '管理员' : '普通用户'
}

async function fetchUsers() {
  try {
    const res = await axios.get('/api/admin/users', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        keyword: searchQuery.value,
        role: roleFilter.value
      }
    });
    users.value = res.data.users || [];
    total.value = res.data.total || 0;
  } catch (error) {
    console.error("获取用户列表失败:", error);
    ElMessage.error('获取用户列表失败');
  }
}

const handleSearch = () => {
  fetchUsers()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchUsers()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUsers()
}

const handleStatusChange = async (user) => {
  try {
    await axios.put(`/api/admin/users/${user.id}/status`, { status: user.status })
    ElMessage.success(user.status === 1 ? '用户已启用' : '用户已禁用')
    // No need to fetch users again, the v-model already updated the UI.
  } catch (error) {
    ElMessage.error('状态更新失败')
    console.error('状态更新失败:', error.response?.data || error.message)
    // Revert the switch state on failure
    user.status = user.status === 1 ? 0 : 1;
  }
}

const handleDeleteUser = async (user) => {
  await axios.delete(`/api/admin/users/${user.id}`)
  ElMessage.success('用户已删除')
  fetchUsers()
}

const handleSubmitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await axios.post('/api/admin/users', userForm.value);
          ElMessage.success('用户添加成功');
        } else {
          await axios.put(`/api/admin/users/${userForm.value.id}`, userForm.value);
          ElMessage.success('用户更新成功');
        }
        dialogVisible.value = false;
        fetchUsers();
      } catch (error) {
        console.error("用户操作失败:", error.response?.data || error);
        const errorMsg = error.response?.data?.error || '未知错误';
        if (errorMsg.includes('username already exists')) {
          ElMessage.error('操作失败：该用户名已被使用');
        } else if (errorMsg.includes('email already exists')) {
          ElMessage.error('操作失败：该邮箱已被注册');
        } else {
          ElMessage.error(`操作失败: ${errorMsg}`);
        }
      }
    }
  });
};

const handleAddUser = () => {
  dialogType.value = 'add'
  userForm.value = {
    username: '',
    email: '',
    phone: '',
    role: '',
    password: ''
  }
  dialogVisible.value = true
}

const handleEditUser = (user) => {
  dialogType.value = 'edit'
  userForm.value = { ...user }
  dialogVisible.value = true
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-users {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 