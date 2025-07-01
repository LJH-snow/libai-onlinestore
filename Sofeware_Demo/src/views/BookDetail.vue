<template>
  <div class="book-detail">
    <el-card class="detail-card">
      <div class="book-content">
        <div class="book-cover">
          <img :src="book.cover" :alt="book.title" />
        </div>
        
        <div class="book-info">
          <h1 class="book-title">{{ book.title }}</h1>
          
          <div class="book-meta">
            <p><span>作者：</span>{{ book.author }}</p>
            <p><span>出版社：</span>{{ book.publisher }}</p>
            <p><span>ISBN：</span>{{ book.isbn }}</p>
            <!-- <p><span>出版日期：</span>{{ book.publishDate }}</p> -->
            <p><span>库存：</span>{{ book.stock }}本</p>
          </div>
          
          <div class="book-price">
            <span class="price-label">价格：</span>
            <span class="price-value">¥{{ book.price }}</span>
          </div>
          
          <div class="book-actions">
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="book.stock"
              :disabled="!userStore.token"
            />
            <el-button
              type="primary"
              :disabled="!userStore.token"
              @click="addToCart"
            >
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="book-description">
        <h2>图书简介</h2>
        <p>{{ book.description }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCartStore } from '../stores/cart'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()
const quantity = ref(1)

const book = ref({})

const addToCart = () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  
  cartStore.addItem(book.value, quantity.value)
  
  ElMessage.success('已添加到购物车')
}

onMounted(() => {
  const bookId = route.params.id
  axios.get(`/api/public/book/${bookId}`)
    .then(res => {
      book.value = res.data.data
    })
    .catch(err => {
      ElMessage.error('获取图书详情失败')
      console.error(err)
    })
})
</script>

<style scoped>
.book-detail {
  padding: 20px;
}

.detail-card {
  max-width: 1200px;
  margin: 0 auto;
}

.book-content {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
}

.book-cover {
  flex: 0 0 300px;
}

.book-cover img {
  width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.book-info {
  flex: 1;
}

.book-title {
  font-size: 24px;
  margin: 0 0 20px;
  color: #303133;
}

.book-meta {
  margin-bottom: 20px;
}

.book-meta p {
  margin: 10px 0;
  color: #606266;
}

.book-meta span {
  color: #909399;
  margin-right: 10px;
}

.book-price {
  margin-bottom: 30px;
}

.price-label {
  font-size: 16px;
  color: #909399;
}

.price-value {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
}

.book-actions {
  display: flex;
  gap: 20px;
  align-items: center;
}

.book-description {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.book-description h2 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 20px;
}

.book-description p {
  color: #606266;
  line-height: 1.8;
}
</style> 