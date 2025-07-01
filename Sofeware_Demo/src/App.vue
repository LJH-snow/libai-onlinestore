<script setup>
import { useUserStore } from './stores/user'
import { useCartStore } from './stores/cart'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

const userStore = useUserStore()
const cartStore = useCartStore()
const router = useRouter()

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <el-config-provider>
    <div class="app-container">
      <el-container>
        <el-header>
          <nav class="nav-header">
            <div class="logo">
              <router-link to="/">在线书店</router-link>
            </div>
            <div class="nav-links">
              <router-link to="/">首页</router-link>
              <template v-if="userStore.token">
                <router-link to="/cart">
                  <el-badge :value="cartStore.totalItems" :hidden="cartStore.totalItems === 0">
                    购物车
                  </el-badge>
                </router-link>
                <router-link to="/orders">我的订单</router-link>
                <router-link to="/profile">个人中心</router-link>
                <template v-if="userStore.isAdmin">
                  <router-link to="/admin/books">管理后台</router-link>
                </template>
                <el-button @click="handleLogout">退出登录</el-button>
              </template>
              <template v-else>
                <router-link to="/login">登录</router-link>
                <router-link to="/register">注册</router-link>
              </template>
            </div>
          </nav>
        </el-header>
        
        <el-main>
          <div class="home" :key="$route.fullPath">
            <router-view v-slot="{ Component }">
              <transition name="fade" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </el-main>

        <el-footer>
          <p>&copy; 2024 在线书店系统. All rights reserved.</p>
        </el-footer>
      </el-container>
    </div>
  </el-config-provider>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
}

.nav-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo a {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  text-decoration: none;
}

.nav-links {
  display: flex;
  gap: 20px;
  align-items: center;
}

.nav-links a {
  color: #606266;
  text-decoration: none;
  font-size: 16px;
}

.nav-links a:hover {
  color: #409EFF;
}

.el-main {
  padding: 20px;
  background-color: #f5f7fa;
}

.el-footer {
  text-align: center;
  padding: 20px;
  background-color: #fff;
  color: #909399;
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
