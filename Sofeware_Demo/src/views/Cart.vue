<template>
  <div class="cart">
    <el-card class="cart-card">
      <template #header>
        <div class="card-header">
          <h2>我的购物车</h2>
        </div>
      </template>
      
      <div v-if="cartStore.items.length === 0" class="empty-cart">
        <el-empty description="购物车是空的" />
        <el-button type="primary" @click="$router.push('/')">
          去购物
        </el-button>
      </div>
      
      <template v-else>
        <el-table :data="cartStore.items" style="width: 100%">
          <el-table-column label="商品" min-width="400">
            <template #default="{ row }">
              <div class="book-info">
                <img :src="row.cover" :alt="row.title" class="book-cover" />
                <div class="book-details">
                  <h3>{{ row.title }}</h3>
                  <p class="price">¥{{ row.price }}</p>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="数量" width="200">
            <template #default="{ row }">
              <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="99"
                @change="(value) => updateQuantity(row.id, value)"
              />
            </template>
          </el-table-column>
          
          <el-table-column label="小计" width="150">
            <template #default="{ row }">
              <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button
                type="danger"
                link
                @click="removeItem(row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="cart-footer">
          <div class="cart-total">
            <span>总计：</span>
            <span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
          </div>
          
          <div class="cart-actions">
            <el-button @click="clearCart">清空购物车</el-button>
            <el-button type="primary" @click="checkout">
              结算 ({{ cartStore.totalItems }}件)
            </el-button>
          </div>
        </div>
      </template>
    </el-card>

    <!-- 订单确认对话框 -->
    <el-dialog v-model="orderDialogVisible" title="确认订单" width="500px">
      <div>
        <el-form label-width="80px">
          <el-form-item label="收货地址">
            <el-select v-model="selectedAddress" placeholder="请选择收货地址" style="width: 100%">
              <el-option v-for="addr in addressList" :key="addr.id" :label="addr.address" :value="addr.id" />
              <template #empty>
                <div style="padding: 10px; text-align: center;">
                  <span>还没有收货地址，请去添加</span>
                </div>
              </template>
            </el-select>
          </el-form-item>
          <el-form-item label="支付方式">
            <el-radio-group v-model="selectedPayment">
              <el-radio v-for="pay in paymentMethods" :key="pay" :value="pay">{{ pay }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="订单商品">
            <ul>
              <li v-for="item in cartStore.items" :key="item.id">
                {{ item.title }} × {{ item.quantity }}
              </li>
            </ul>
          </el-form-item>
          <el-form-item label="总价">
            <span style="color:#f56c6c;font-weight:bold;">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="orderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmOrder" :disabled="!selectedAddress || !selectedPayment">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useCartStore } from '../stores/cart'
import { ElMessageBox, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'

const cartStore = useCartStore()
const router = useRouter()
const userStore = useUserStore()

onMounted(() => {
  cartStore.fetchCart()
})

const orderDialogVisible = ref(false)
const selectedAddress = ref('')
const selectedPayment = ref('')
const addressList = ref([])
const paymentMethods = ref(['微信支付', '支付宝', '银行卡'])

const updateQuantity = (bookId, quantity) => {
  cartStore.updateQuantity(bookId, quantity)
}

const removeItem = (bookId) => {
  ElMessageBox.confirm(
    '确定要从购物车中删除该商品吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    cartStore.removeItem(bookId)
    ElMessage.success('已从购物车中删除')
  }).catch(() => {})
}

const clearCart = () => {
  ElMessageBox.confirm(
    '确定要清空购物车吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    cartStore.clearCart()
    ElMessage.success('购物车已清空')
  }).catch(() => {})
}

const checkout = async () => {
  if (cartStore.items.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  addressList.value = []
  // 优先从 store 获取 userId，如果不存在则从 localStorage 回退
  const userId = userStore.user?.id || localStorage.getItem('userId')
  
  if (userId) {
    try {
      const res = await axios.get(`/api/addresses/user/${userId}`)
      addressList.value = res.data.data || []
    } catch (error) {
      console.error("获取地址列表失败:", error);
      ElMessage.error("获取收货地址失败，请稍后重试。")
    }
  } else {
    // 如果到处都找不到 userId，提示错误
    ElMessage.error("无法获取用户信息，请重新登录。")
    return // 阻止打开对话框
  }
  
  orderDialogVisible.value = true
}

const confirmOrder = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  
  console.log('Selected address ID:', selectedAddress.value)
  console.log('Selected address type:', typeof selectedAddress.value)
  
  try {
    const userId = userStore.user?.id || localStorage.getItem('userId')
    const addressId = selectedAddress.value
    
    console.log('User ID:', userId)
    console.log('Address ID:', addressId)
    
    if (!addressId) {
      ElMessage.error('地址ID不能为空')
      return
    }
    
    // 生成订单号：YYYYMMDD + 时间戳的后几位，确保唯一性
    const now = new Date()
    const year = now.getFullYear()
    const month = (now.getMonth() + 1).toString().padStart(2, '0')
    const day = now.getDate().toString().padStart(2, '0')
    const orderNumber = `${year}${month}${day}${Date.now()}`

    // 准备订单商品列表
    const orderItems = cartStore.items.map(item => ({
      bookId: item.bookId,
      quantity: item.quantity,
      price: item.price
    }));
    
    // 构造与后端 OrderCreationDTO 完全匹配的数据结构
    const orderData = {
      orderNumber: orderNumber,
      userId: parseInt(userId), // 确保 userId 是数字
      addressId: addressId,
      totalPrice: cartStore.totalPrice,
      status: 'PENDING',
      items: orderItems // 将商品列表包含进去
    }
    
    console.log('Order data being sent:', orderData)
    
    await axios.post('/api/order', orderData)
    orderDialogVisible.value = false
    await cartStore.clearCart()
    selectedAddress.value = ''
    selectedPayment.value = ''
    ElMessage.success('支付成功，订单已提交！')
    router.push('/orders')
  } catch (err) {
    ElMessage.error('下单失败')
    console.error('Order creation error:', err)
    console.error('Error response:', err.response?.data)
  }
}
</script>

<style scoped>
.cart {
  padding: 20px;
}

.cart-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.empty-cart {
  text-align: center;
  padding: 40px 0;
}

.book-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.book-cover {
  width: 80px;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
}

.book-details h3 {
  margin: 0 0 10px;
  font-size: 16px;
  color: #303133;
}

.price {
  color: #f56c6c;
  font-size: 16px;
  margin: 0;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 40px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.cart-total {
  font-size: 16px;
}

.total-price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
  margin-left: 10px;
}

.cart-actions {
  display: flex;
  gap: 20px;
}
</style> 