<template>
  <div class="home">
    <div class="sidebar">
      <el-card class="category-card">
        <div class="category-title">图书分类</div>
        <ul class="category-list">
          <li
            v-for="category in categories"
            :key="category.id"
            :class="{ active: selectedCategory === category.id }"
            @click="handleCategoryClick(category.id)"
          >
            {{ category.name }}
          </li>
        </ul>
      </el-card>
    </div>
    <div class="main-content">
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索图书..."
          clearable
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <el-button type="primary" icon="Search" @click="handleSearch" class="search-btn"></el-button>
      </div>
      <div class="book-list">
        <el-row :gutter="20">
          <el-col :span="8" v-for="book in filteredBooks" :key="book.id">
            <el-card class="book-card">
              <el-image :src="book.cover" fit="contain" class="book-cover" />
              <div class="book-info">
                <div class="book-title">{{ book.title }}</div>
                <div class="book-author">{{ book.author }}</div>
                <div class="book-price">¥{{ book.price }}</div>
                <el-button type="primary" @click="viewBookDetail(book.id)">查看详情</el-button>
                <el-button type="success" @click="addToCart(book)">加入购物车</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <div class="pagination">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="filteredBooks?.length || 0"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCartStore } from '../stores/cart'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const categories = ref([])
const selectedCategory = ref('')
const searchQuery = ref('')
const books = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)

const filteredBooks = computed(() => {
  let result = books.value
  if (selectedCategory.value) {
    result = result.filter(book => book.categoryId == selectedCategory.value)
  }
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase()
    result = result.filter(book =>
      book.title.toLowerCase().includes(q) ||
      book.author.toLowerCase().includes(q)
    )
  }
  return result
})

const fetchCategories = async () => {
  try {
    const res = await axios.get('/api/public/category/all')
    categories.value = [{ id: null, name: '全部' }, ...res.data.data]
  } catch (error) {
    console.error(error)
  }
}

const fetchBooks = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/public/book', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        categoryId: selectedCategory.value
      }
    })
    const data = res.data.data
    books.value = data.content
    total.value = data.totalElements
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function handleCategoryClick(categoryId) {
  selectedCategory.value = categoryId
  searchQuery.value = ''
  fetchBooks()
}

function handleSearch() {
  selectedCategory.value = ''
  fetchBooks()
}

function handleSizeChange(val) {
  pageSize.value = val
  fetchBooks()
}

function handleCurrentChange(val) {
  currentPage.value = val
  fetchBooks()
}

const viewBookDetail = (bookId) => {
  router.push(`/book/${bookId}`)
}

const addToCart = (book) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录后再加入购物车')
    router.push('/login')
    return
  }
  cartStore.addItem(book)
  ElMessage.success('已添加到购物车')
}

onMounted(() => {
  fetchCategories()
  fetchBooks()
})
</script>

<style scoped>
.home {
  display: flex;
  padding: 20px;
}
.sidebar {
  width: 220px;
  margin-right: 20px;
}
.category-card {
  padding: 0;
}
.category-title {
  font-weight: bold;
  padding: 16px 20px 8px 20px;
}
.category-list {
  list-style: none;
  padding: 0 0 16px 0;
  margin: 0;
}
.category-list li {
  padding: 10px 20px;
  cursor: pointer;
  color: #333;
  transition: background 0.2s, color 0.2s;
}
.category-list li.active,
.category-list li:hover {
  background: #409EFF;
  color: #fff;
}
.main-content {
  flex: 1;
}
.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.search-input {
  flex: 1;
}
.search-btn {
  margin-left: 10px;
}
.book-list {
  min-height: 200px;
}
.book-card {
  margin-bottom: 20px;
}
.book-cover {
  width: 100%;
  height: 180px;
  object-fit: contain;
  background: #fff;
  border-radius: 4px;
  display: block;
  margin: 0 auto;
}
.book-info {
  text-align: center;
}
.book-title {
  font-weight: bold;
  margin-bottom: 5px;
}
.book-author {
  color: #666;
  margin-bottom: 5px;
}
.book-price {
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 10px;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style> 