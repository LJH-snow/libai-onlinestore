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
    <el-dialog v-model="orderDialogVisible" title="确认订单" width="600px">
      <div v-if="selectedAddressInfo">
        <el-descriptions title="收货信息" :column="1" border>
          <template #extra>
            <el-button type="primary" link @click="addressDialogVisible = true">切换地址</el-button>
          </template>
          <el-descriptions-item label="收货人">{{ selectedAddressInfo.receiverName }}, {{ selectedAddressInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ `${selectedAddressInfo.province} ${selectedAddressInfo.city} ${selectedAddressInfo.district} ${selectedAddressInfo.detailAddress}` }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div v-else>
        <el-empty description="请选择收货地址">
          <el-button type="primary" @click="addressDialogVisible = true">选择地址</el-button>
        </el-empty>
      </div>

      <el-divider />

      <el-form label-width="80px">
          <el-form-item label="支付方式">
            <el-radio-group v-model="selectedPayment">
              <el-radio v-for="pay in paymentMethods" :key="pay" :value="pay">{{ pay }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="订单商品">
            <ul class="order-items-summary">
              <li v-for="item in cartStore.items" :key="item.id">
                <span>{{ item.title }}</span>
                <span>x {{ item.quantity }}</span>
                <span>¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              </li>
            </ul>
          </el-form-item>
          <el-form-item label="总价">
            <span style="color:#f56c6c;font-weight:bold; font-size: 1.2em;">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
          </el-form-item>
        </el-form>

      <template #footer>
        <el-button @click="orderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmOrder" :disabled="!selectedAddress || !selectedPayment">确认支付</el-button>
      </template>
    </el-dialog>

    <!-- 地址选择对话框 -->
    <el-dialog v-model="addressDialogVisible" title="选择收货地址" width="700px" append-to-body>
        <div class="address-list-container">
            <el-radio-group v-model="selectedAddress" class="address-radios">
              <el-radio
                v-for="addr in addressList"
                :key="addr.id"
                :value="addr.id"
                border
                class="address-radio"
              >
                <div class="address-info">
                  <p><strong>{{ addr.receiverName }}</strong>, {{ addr.phone }} <el-tag v-if="addr.isDefault" size="small" type="success" style="margin-left: 10px;">默认</el-tag></p>
                  <p>{{ `${addr.province} ${addr.city} ${addr.district} ${addr.detailAddress}` }}</p>
                </div>
              </el-radio>
            </el-radio-group>
        </div>
        <template #footer>
            <el-button @click="addressDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSelectAddress">确认选择</el-button>
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
const addressDialogVisible = ref(false)
const selectedAddress = ref(null)
const selectedAddressInfo = ref(null)
const selectedPayment = ref('支付宝')
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
    ElMessage.warning('购物车为空');
    return;
  }

  // 总是重置地址列表和选择状态
  addressList.value = [];
  selectedAddress.value = null;
  selectedAddressInfo.value = null;

  const userId = userStore.user?.id || localStorage.getItem('userId');
  if (!userId) {
    ElMessage.error("无法获取用户信息，请重新登录。");
    return;
  }

  try {
    const res = await axios.get(`/api/addresses/user/${userId}`);
    addressList.value = res.data.data || [];

    if (addressList.value.length === 0) {
      ElMessage.warning('您还没有收货地址，请先到个人中心添加');
      return; // 关键：如果没有地址，则不打开任何对话框
    }

    // 自动选择默认地址
    const defaultAddress = addressList.value.find(addr => addr.isDefault);
    if (defaultAddress) {
      selectedAddress.value = defaultAddress.id;
      selectedAddressInfo.value = defaultAddress;
    }

    orderDialogVisible.value = true; // 只有在获取到地址后才打开对话框

  } catch (error) {
    console.error("获取地址列表失败:", error);
    ElMessage.error("获取收货地址失败，请稍后重试。");
  }
};

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
    
    await axios.post('/api/orders', orderData)
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

const handleSelectAddress = () => {
    if (selectedAddress.value) {
        selectedAddressInfo.value = addressList.value.find(addr => addr.id === selectedAddress.value);
    }
    addressDialogVisible.value = false;
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

.address-radios {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

.address-radio {
  width: 100%;
  margin-bottom: 10px;
  height: auto;
  padding: 10px;
}

.address-info p {
  margin: 0;
  line-height: 1.4;
}

.no-address {
  padding: 10px;
  text-align: center;
  color: #909399;
  width: 100%;
}

.order-items-summary {
    list-style: none;
    padding: 0;
    margin: 0;
}

.order-items-summary li {
    display: flex;
    justify-content: space-between;
    padding: 5px 0;
}

.address-list-container {
    max-height: 50vh;
    overflow-y: auto;
}
</style> 