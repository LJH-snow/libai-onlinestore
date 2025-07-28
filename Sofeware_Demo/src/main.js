/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-21 15:21:23
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-22 22:23:27
 * @FilePath: \AAA\Sofeware_Demo\src\main.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import './assets/main.css'
import axios from 'axios'

// 设置 axios 基础URL - 在Docker环境中使用相对路径，让Nginx代理处理
// 开发环境使用Vite代理，生产环境使用Nginx代理
if (import.meta.env.DEV) {
  // 开发环境：使用Vite代理
  axios.defaults.baseURL = ''
} else {
  // 生产环境：使用相对路径，让Nginx代理到后端
  axios.defaults.baseURL = ''
}

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// 全局 axios 请求拦截器，自动加 token
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    
    // 如果请求不是公共API，并且存在token，则添加认证头
    const publicApiPrefixes = ['/api/user/register', '/api/user/login', '/api/public/book', '/api/public/category'];

    // Check if the request URL starts with any of the public prefixes
    const isPublicApi = publicApiPrefixes.some(prefix => config.url.startsWith(prefix));

    if (!isPublicApi && token) {
        config.headers.Authorization = 'Bearer ' + token;
        console.log(`Authorization header set for: [${config.method.toUpperCase()}] ${config.url}`);
    }

    return config
  },
  error => Promise.reject(error)
)

// 添加响应拦截器来调试错误
axios.interceptors.response.use(
  response => response,
  error => {
    console.error('Axios error:', error.response?.status, error.response?.data)
    if (error.response?.status === 403) {
      console.error('403 Forbidden - Token might be invalid or expired')
      console.error('Current token:', localStorage.getItem('token'))
    }
    return Promise.reject(error)
  }
)

app.mount('#app')


