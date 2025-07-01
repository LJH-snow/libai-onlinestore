<template>
  <div class="admin-layout">
    <el-container style="min-height: 100vh;">
      <el-aside width="200px" class="admin-aside">
        <el-menu
          :default-active="$route.path"
          class="admin-menu"
          router
        >
          <el-menu-item index="/admin/Books">
            <el-icon><Collection /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/Orders">
            <el-icon><Tickets /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/Users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="admin-header">
          <div class="header-content">
            <span class="admin-title">管理后台</span>
            <el-button type="danger" @click="handleLogout">退出登录</el-button>
          </div>
        </el-header>
        <el-main class="admin-main">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessageBox } from 'element-plus'
import { Collection, Tickets, User } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  })
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}
.admin-aside {
  background: #304156;
  color: #fff;
}
.admin-menu {
  border-right: none;
  height: 100vh;
  background: #304156;
}
.admin-menu .el-menu-item {
  color: #fff;
}
.admin-menu .el-menu-item.is-active {
  background: #409EFF;
  color: #fff;
}
.admin-header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  height: 60px;
  display: flex;
  align-items: center;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.admin-title {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}
.admin-main {
  background: #f5f7fa;
  padding: 24px;
  min-height: calc(100vh - 60px);
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 