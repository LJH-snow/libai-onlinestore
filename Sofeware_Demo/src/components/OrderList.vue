<template>
  <div class="order-list">
    <div v-if="orders.length === 0" class="empty-state">
      <el-empty description="暂无订单" />
    </div>
    
    <div v-else class="orders">
      <div v-for="order in orders" :key="order.id" class="order-item">
        <div class="order-header">
          <div class="order-info">
            <span class="order-id">订单号：{{ order.orderNumber || order.id }}</span>
            <span class="order-time">{{ formatTime(order.createdAt) }}</span>
          </div>
          <el-tag :type="getStatusType(order.status)">
            {{ getStatusLabel(order.status) }}
          </el-tag>
        </div>
        
        <div class="order-content">
          <!-- 由于后端没有items字段，显示订单基本信息 -->
          <div class="order-summary">
            <p><strong>订单状态：</strong>{{ getStatusLabel(order.status) }}</p>
            <p><strong>收货地址：</strong>{{ order.address || '地址信息不存在' }}</p>
            <p><strong>创建时间：</strong>{{ formatTime(order.createdAt) }}</p>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-total">
            合计：<span class="price">¥{{ (order.totalPrice || 0).toFixed(2) }}</span>
          </div>
          
          <div class="order-actions">
            <el-button
              v-if="order.status === 'PENDING'"
              type="danger"
              link
              @click="handleCancelOrder(order)"
            >
              取消订单
            </el-button>
            <el-button
              v-if="order.status === 'SHIPPED'"
              type="primary"
              link
              @click="handleConfirmReceipt(order)"
            >
              确认收货
            </el-button>
            <el-button
              v-if="['COMPLETED', 'CANCELLED'].includes(order.status?.toUpperCase())"
              type="primary"
              link
              @click="handleDeleteOrder(order)"
            >
              删除订单
            </el-button>
            <el-button type="info" link @click="$emit('view-detail', order.id)">查看详情</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

const props = defineProps({
  orders: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['refresh', 'view-detail'])

const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'SHIPPED': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'info'
  }
  return types[status] || 'info'
}

const getStatusLabel = (status) => {
  const statusMap = {
    'PENDING': '待发货',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const formatTime = (timestamp) => {
  if (!timestamp) return '未知时间'
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN')
}

const handleCancelOrder = (order) => {
  ElMessageBox.confirm(
    '确定要取消该订单吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    await axios.put(`/api/order/${order.id}/status?status=CANCELLED`)
    ElMessage.success('订单已取消')
    emit('refresh')
  }).catch(() => {})
}

const handleConfirmReceipt = (order) => {
  ElMessageBox.confirm(
    '确认已收到商品？',
    '提示',
    {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    await axios.put(`/api/order/${order.id}/status?status=COMPLETED`)
    ElMessage.success('确认收货成功')
    emit('refresh')
  }).catch(() => {})
}

const handleDeleteOrder = (order) => {
  ElMessageBox.confirm(
    '确定要删除该订单吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    await axios.delete(`/api/order/${order.id}`)
    ElMessage.success('订单已删除')
    emit('refresh')
  }).catch(() => {})
}
</script>

<style scoped>
.order-list {
  padding: 20px 0;
}

.empty-state {
  padding: 40px 0;
}

.order-item {
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

.order-info {
  display: flex;
  gap: 20px;
}

.order-id {
  color: #606266;
}

.order-time {
  color: #909399;
}

.order-content {
  padding: 20px;
}

.order-summary {
  margin-bottom: 20px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-top: 1px solid #ebeef5;
}

.order-total {
  color: #606266;
}

.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
}

.order-actions {
  display: flex;
  gap: 10px;
}
</style> 