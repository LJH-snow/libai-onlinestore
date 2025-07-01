<template>
  <div class="admin-orders">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
        </div>
      </template>
      
      <el-form :inline="true" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchQuery"
            placeholder="搜索订单号/用户名..."
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
          <el-select v-model="statusFilter" placeholder="订单状态" clearable @change="handleSearch">
            <el-option :key="''" label="全部订单" :value="''" />
            <el-option
              v-for="status in orderStatus"
              :key="status.value"
              :label="status.label"
              :value="status.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            搜索
          </el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="orders.filter(order => order.items && order.items.length)" style="width: 100%">
        <el-table-column prop="orderNumber" label="订单号" width="180" />
        
        <el-table-column prop="username" label="用户名" width="120" />
        
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <template v-if="row.items && row.items.length">
              <div v-for="item in row.items" :key="item.id" class="order-item">
              <el-image
                :src="item.bookCover"
                :preview-src-list="[item.bookCover]"
                fit="cover"
                class="book-cover"
              />
              <div class="item-info">
                <p class="title">{{ item.bookTitle }}</p>
                <p class="price">¥{{ item.price }} × {{ item.quantity }}</p>
              </div>
            </div>
            </template>
          </template>
        </el-table-column>
        
        <el-table-column prop="totalPrice" label="总金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalPrice ? row.totalPrice.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ row.createdAt ? new Date(row.createdAt).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewOrder(row)">查看</el-button>
            <el-button v-if="row.status && row.status.toLowerCase() === 'pending'" link type="success" @click="handleShipOrder(row)">发货</el-button>
            <el-button v-if="row.status && row.status.toLowerCase() === 'shipped'" link type="warning" @click="handleCompleteOrder(row)">完成</el-button>
            <el-button v-if="row.status && ['pending', 'shipped'].includes(row.status.toLowerCase())" link type="danger" @click="handleCancelOrder(row)">取消</el-button>
            <el-button link type="info" @click="() => openReturnDialog(row)">退货</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="orders.filter(order => order.items && order.items.length).length"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="订单详情"
      width="800px"
    >
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-section">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">
              {{ currentOrder.id }}
            </el-descriptions-item>
            <el-descriptions-item label="用户名">
              {{ currentOrder.username }}
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ currentOrder.createdAt ? new Date(currentOrder.createdAt).toLocaleString() : '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(currentOrder.status)">
                {{ getStatusLabel(currentOrder.status) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="detail-section">
          <h3>商品信息</h3>
          <el-table :data="currentOrder.items" style="width: 100%">
            <el-table-column label="商品" min-width="300">
              <template #default="{ row }">
                <div class="book-info">
                  <el-image
                    :src="row.bookCover"
                    :preview-src-list="[row.bookCover]"
                    fit="cover"
                    class="book-cover"
                  />
                  <div class="book-details">
                    <p class="title">{{ row.bookTitle }}</p>
                    <p class="price">¥{{ row.price }}</p>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column prop="quantity" label="数量" width="100" />
            
            <el-table-column label="小计" width="120">
              <template #default="{ row }">
                ¥{{ (row.price * row.quantity).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="detail-section">
          <h3>收货信息</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="收货人">
              {{ currentOrder.consignee || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ currentOrder.phone || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="收货地址">
              {{ currentOrder.fullAddress || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="detail-section">
          <h3>订单金额</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="商品总额">
              ¥{{ currentOrder.totalPrice ? currentOrder.totalPrice.toFixed(2) : '0.00' }}
            </el-descriptions-item>
            <el-descriptions-item label="运费">
              ¥0.00
            </el-descriptions-item>
            <el-descriptions-item label="实付金额">
              ¥{{ currentOrder.totalPrice ? currentOrder.totalPrice.toFixed(2) : '0.00' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>

    <!-- 退货对话框 -->
    <el-dialog v-model="returnDialogVisible" title="退货处理" width="400px">
      <el-form label-width="80px">
        <el-form-item label="退货原因" required>
          <el-input v-model="returnReason" placeholder="请输入退货原因" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="returnRemark" placeholder="可填写退货备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReturnConfirm">确定退货</el-button>
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
const statusFilter = ref('')

const orderStatus = [
  { value: 'pending', label: '待发货' },
  { value: 'shipped', label: '已发货' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' }
]

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 订单列表
const orders = ref([])

// 订单详情
const dialogVisible = ref(false)
const currentOrder = ref(null)

// 退货对话框
const returnDialogVisible = ref(false)
const returnReason = ref('')
const returnRemark = ref('')
const returnOrder = ref(null)

const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    shipped: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return types[status] || 'info'
}

const getStatusLabel = (status) => {
  const statusMap = {
    pending: '待发货',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消',
    failed: '失败',
    cancelled: '已取消'
  }
  return statusMap[status?.toLowerCase()] || status || '-';
}

async function fetchOrders() {
  let params = {
    page: currentPage.value - 1,
    size: pageSize.value
  }
  if (searchQuery.value.trim()) params.keyword = searchQuery.value.trim()
  if (statusFilter.value) params.status = statusFilter.value
  const res = await axios.get('/api/order/all', { params })
  orders.value = res.data.data.content || []
  total.value = Number(res.data.data.totalElements) || 0
}

const handleSearch = () => {
  fetchOrders()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchOrders()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchOrders()
}

const handleViewOrder = async (order) => {
  const res = await axios.get(`/api/order/${order.id}`)
  currentOrder.value = res.data.data
  dialogVisible.value = true
}

const handleShipOrder = async (order) => {
  await axios.put(`/api/order/${order.id}/status`, null, { params: { status: 'shipped' } })
  ElMessage.success('发货成功')
  fetchOrders()
}

const handleCompleteOrder = async (order) => {
  await axios.put(`/api/order/${order.id}/status`, null, { params: { status: 'completed' } })
  ElMessage.success('订单已完成')
  fetchOrders()
}

const handleCancelOrder = async (order) => {
  await axios.put(`/api/order/${order.id}/status`, null, { params: { status: 'cancelled' } })
  ElMessage.success('订单已取消')
  fetchOrders()
}

const handleDeleteOrder = async (order) => {
  await axios.delete(`/api/order/${order.id}`)
  ElMessage.success('订单已删除')
  fetchOrders()
}

function openReturnDialog(order) {
  returnOrder.value = order
  returnReason.value = ''
  returnRemark.value = ''
  returnDialogVisible.value = true
}

async function handleReturnConfirm() {
  if (!returnReason.value) {
    ElMessage.error('请填写退货原因')
    return
  }
  await axios.put(`/api/order/${returnOrder.value.id}/status`, null, { params: { status: 'cancelled' } })
  returnDialogVisible.value = false
  ElMessage.success('订单已退货')
  fetchOrders() // 退货后刷新订单列表
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.admin-orders {
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

.order-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
}

.order-item:not(:last-child) {
  border-bottom: 1px solid #ebeef5;
}

.book-cover {
  width: 60px;
  height: 80px;
  border-radius: 4px;
}

.item-info {
  flex: 1;
}

.item-info .title {
  margin: 0 0 5px;
  font-size: 14px;
}

.item-info .price {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.order-detail {
  padding: 20px;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h3 {
  margin: 0 0 15px;
  font-size: 16px;
  color: #303133;
}

.book-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.book-details .title {
  margin: 0 0 5px;
  font-size: 14px;
}

.book-details .price {
  margin: 0;
  color: #666;
  font-size: 14px;
}
</style> 