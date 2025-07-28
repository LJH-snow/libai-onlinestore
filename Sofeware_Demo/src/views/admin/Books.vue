<template>
  <div class="admin-books">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书管理</span>
          <el-button type="primary" @click="handleAddBook">
            添加图书
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchQuery"
            placeholder="搜索图书..."
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
          <el-select v-model="categoryFilter" placeholder="选择分类" clearable @change="handleSearch">
            <el-option :key="''" label="全部" :value="''" />
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            搜索
          </el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="books" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image
              :src="row.cover"
              :preview-src-list="[row.cover]"
              fit="cover"
              class="book-cover"
            />
          </template>
        </el-table-column>
        
        <el-table-column prop="title" label="书名" min-width="200" />
        
        <el-table-column prop="author" label="作者" width="120" />
        
        <el-table-column prop="isbn" label="ISBN" width="150" />

        <el-table-column prop="publisher" label="出版社" width="150" />
        
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price.toFixed(2) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="stock" label="库存" width="100" />
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'on' ? 'success' : 'info'">
              {{ row.status === 'on' ? '在售' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEditBook(row)">
              编辑
            </el-button>
            <el-button
              link
              :type="row.status === 'on' ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'on' ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" @click="handleDeleteBook(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 图书编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑图书' : '添加图书'"
      width="600px"
    >
      <el-form
        ref="bookForm"
        :model="currentBook"
        :rules="bookRules"
        label-width="100px"
      >
        <el-form-item label="书名" prop="title">
          <el-input v-model="currentBook.title" />
        </el-form-item>
        
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="currentBook.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="作者" prop="author">
          <el-input v-model="currentBook.author" />
        </el-form-item>
        
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="currentBook.publisher" />
        </el-form-item>
        
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="currentBook.isbn" />
        </el-form-item>
        
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="currentBook.price"
            :precision="2"
            :step="0.1"
            :min="0"
          />
        </el-form-item>
        
        <el-form-item label="库存" prop="stock">
          <el-input-number
            v-model="currentBook.stock"
            :min="0"
            :precision="0"
          />
        </el-form-item>
        
        <el-form-item label="封面" prop="cover">
          <el-input v-model="currentBook.cover" placeholder="输入图片URL" />
        </el-form-item>
        
        <el-form-item label="简介" prop="description">
          <el-input
            v-model="currentBook.description"
            type="textarea"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">
            取消
          </el-button>
          <el-button type="primary" @click="saveBook">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 搜索和筛选
const searchQuery = ref('')
const categoryFilter = ref('')
const categories = ref([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 图书列表
const books = ref([])

// 图书编辑
const dialogVisible = ref(false)
const isEditing = ref(false)
const bookForm = ref(null)

const currentBook = reactive({
  id: null,
  title: '',
  author: '',
  publisher: '',
  isbn: '',
  price: 0,
  stock: 0,
  cover: '',
  description: '',
  categoryId: null
})

const bookRules = {
  title: [
    { required: true, message: '请输入书名', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: '请输入出版社', trigger: 'blur' }
  ],
  isbn: [
    { required: true, message: '请输入ISBN', trigger: 'blur' },
    { pattern: /\S/, message: 'ISBN不能为空白字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存', trigger: 'blur' }
  ],
  cover: [
    { required: true, message: '请输入封面图片URL', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入图书简介', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ]
}

const fetchCategories = async () => {
  try {
    const res = await axios.get('/api/categories')
    categories.value = res.data // 后端直接返回数组
  } catch (error) {
    ElMessage.error('获取分类列表失败')
    console.error('获取分类列表失败:', error)
  }
}

const fetchBooks = async () => {
  try {
    const res = await axios.get('/api/admin/books', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        title: searchQuery.value,
        categoryId: categoryFilter.value
      }
    });
    // 后端应该返回分页数据格式
    books.value = res.data.books || [];
    total.value = res.data.total || 0;
  } catch (error) {
    console.error("获取书籍列表失败:", error);
    ElMessage.error('获取书籍列表失败');
  }
};

const handleSearch = () => {
  fetchBooks()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchBooks()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchBooks()
}

const handleAddBook = () => {
  isEditing.value = false
  const newBook = {
    id: null,
    title: '',
    author: '',
    publisher: '',
    isbn: '',
    price: 0,
    stock: 0,
    cover: '',
    description: '',
    categoryId: null,
    status: 'on'
  }
  Object.assign(currentBook, newBook)
  dialogVisible.value = true
}

const handleEditBook = (book) => {
  isEditing.value = true
  Object.assign(currentBook, book)
  if (!('status' in currentBook) || !currentBook.status) {
    currentBook.status = 'on'
  }
  dialogVisible.value = true
}

const handleToggleStatus = async (book) => {
  const newStatus = book.status === 'on' ? 'off' : 'on'
  try {
    // 修正 URL，并使用正确的后台接口
    await axios.put(`/api/admin/books/${book.id}/status`, { status: newStatus })
    ElMessage.success('状态更新成功')
    await fetchBooks() // 重新获取列表
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleDeleteBook = async (book) => {
  await axios.delete(`/api/admin/books/${book.id}`)
  ElMessage.success('图书已删除')
  fetchBooks()
}

const saveBook = async () => {
  if (!bookForm.value) return
  try {
    // Trim input fields before validation
    currentBook.isbn = currentBook.isbn.trim();

    await bookForm.value.validate()
    if (!currentBook.status) {
      currentBook.status = 'on'
    }
    if (isEditing.value) {
      await axios.put(`/api/admin/books/${currentBook.id}`, currentBook)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/admin/books', currentBook)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    await fetchBooks()
  } catch (error) {
    console.error('表单验证或提交失败:', error.response?.data || error)
    ElMessage.error('操作失败，请检查表单输入或查看控制台日志');
  }
}

onMounted(() => {
  fetchBooks()
  fetchCategories()
})
</script>

<style scoped>
.admin-books {
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

.book-cover {
  width: 60px;
  height: 80px;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 