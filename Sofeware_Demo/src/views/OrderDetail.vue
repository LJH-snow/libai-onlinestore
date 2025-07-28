<template>
  <div class="order-detail">
    <el-card v-if="order">
      <template #header>
        <h2>订单详情</h2>
      </template>
      <div class="order-info">
        <p><strong>订单号：</strong>{{ order.orderNumber }}</p>
        <p><strong>下单时间：</strong>{{ formatTime(order.createdAt) }}</p>
        <p><strong>订单状态：</strong><el-tag :type="getStatusType(order.status)">{{ getStatusLabel(order.status) }}</el-tag></p>
        <p><strong>收货地址：</strong>{{ formattedAddress }}</p>
        <p><strong>总价：</strong><span class="total-price">¥{{ order.totalPrice.toFixed(2) }}</span></p>
      </div>
      
      <h3>商品列表</h3>
      <el-table :data="order.items || []" style="width: 100%">
        <el-table-column label="商品">
          <template #default="{ row }">
            <div class="book-item">
              <img :src="row.book.cover || row.book.imageUrl" class="book-cover" alt="封面">
              <span>{{ row.book.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" />
        <el-table-column label="单价">
           <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="小计">
          <template #default="{ row }">
            ¥{{ (row.price * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-empty v-else description="正在加载订单或未找到订单..." />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const order = ref(null)

const formattedAddress = computed(() => {
  if (!order.value || !order.value.address) {
    return '地址信息不存在';
  }
  try {
    const addr = typeof order.value.address === 'string'
      ? JSON.parse(order.value.address)
      : order.value.address;

    return `${addr.province || ''} ${addr.city || ''} ${addr.district || ''} ${addr.detailAddress || ''} (${addr.receiverName || ''} 收) ${addr.phone || ''}`.trim();
  } catch (e) {
    return order.value.address;
  }
});

// --- 从 OrderList.vue 复制过来的工具函数 ---
const getStatusType = (status) => {
  const types = { 'PENDING': 'warning', 'SHIPPED': 'primary', 'COMPLETED': 'success', 'CANCELLED': 'info' };
  return types[status] || 'info';
}

const getStatusLabel = (status) => {
  const statusMap = { 'PENDING': '待发货', 'SHIPPED': '已发货', 'COMPLETED': '已完成', 'CANCELLED': '已取消' };
  return statusMap[status] || status;
}

const formatTime = (timestamp) => {
  if (!timestamp) return '未知时间';
  return new Date(timestamp).toLocaleString('zh-CN');
}
// --- ------------------------------------ ---

onMounted(async () => {
  const orderId = route.params.id
  try {
    const res = await axios.get(`/api/orders/${orderId}`)
    order.value = res.data
  } catch (err) {
    ElMessage.error('获取订单详情失败')
    order.value = null
  }
})
</script>

<style scoped>
.order-detail {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}
.order-info p {
  margin: 8px 0;
}
.total-price {
  color: #f56c6c;
  font-size: 1.2em;
  font-weight: bold;
}
.book-item {
  display: flex;
  align-items: center;
}
.book-cover {
  width: 40px;
  height: 60px;
  margin-right: 10px;
  object-fit: cover;
}
</style> 