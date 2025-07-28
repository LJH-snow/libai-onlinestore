<!--
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-16 10:51:22
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-20 16:12:16
 * @FilePath: \vuedemo\Sofeware_Demo\src\views\Orders.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="orders">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的订单</span>
        </div>
      </template>
      
      <div v-if="isLoading" class="loading-state">
        <p>正在加载订单...</p>
      </div>
      <el-tabs v-else v-model="activeTab">
        <el-tab-pane label="全部订单" name="all">
          <order-list :orders="filteredOrders" @refresh="fetchOrders" @view-detail="viewOrderDetail" />
        </el-tab-pane>
        <el-tab-pane label="待发货" name="pending">
          <order-list :orders="filteredOrders" @refresh="fetchOrders" @view-detail="viewOrderDetail" />
        </el-tab-pane>
        <el-tab-pane label="已发货" name="shipped">
          <order-list :orders="filteredOrders" @refresh="fetchOrders" @view-detail="viewOrderDetail" />
        </el-tab-pane>
        <el-tab-pane label="已完成" name="completed">
          <order-list :orders="filteredOrders" @refresh="fetchOrders" @view-detail="viewOrderDetail" />
        </el-tab-pane>
        <el-tab-pane label="已取消" name="cancelled">
          <order-list :orders="filteredOrders" @refresh="fetchOrders" @view-detail="viewOrderDetail" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import OrderList from '../components/OrderList.vue'
import axios from 'axios'
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'

const isLoading = ref(true)
// 当前激活的标签页
const activeTab = ref('all')

// 模拟订单数据
const orders = ref([])

// 根据当前标签页筛选订单
const filteredOrders = computed(() => {
  if (activeTab.value === 'all') {
    return orders.value
  }
  // 将前端状态值映射到后端状态值
  const statusMap = {
    'pending': 'PENDING',
    'shipped': 'SHIPPED', 
    'completed': 'COMPLETED',
    'cancelled': 'CANCELLED'
  }
  const backendStatus = statusMap[activeTab.value]
  return orders.value.filter(order => (order.status || '').toUpperCase() === backendStatus)
})

const router = useRouter()

async function fetchOrders() {
  isLoading.value = true
  try {
    const userStore = useUserStore()
    const userId = userStore.user?.id || localStorage.getItem('userId')
    if (!userId) {
      orders.value = []
      return
    }
    const res = await axios.get(`/api/order/user/${userId}`)
    orders.value = res.data.data || []
  } catch (error) {
    console.error("获取订单失败:", error)
    orders.value = []
  } finally {
    isLoading.value = false
  }
}

const viewOrderDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

onMounted(fetchOrders)
</script>

<style scoped>
.orders {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-state {
  padding: 40px;
  text-align: center;
  color: #909399;
}
</style> 