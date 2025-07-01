/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-16 10:36:34
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-16 11:49:41
 * @FilePath: \Sofeware_Demo\src\stores\user.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  const isAdmin = ref(localStorage.getItem('userRole') === 'admin')

  function setUser(userData) {
    user.value = userData
    if (userData && userData.id) {
      localStorage.setItem('userId', userData.id)
    }
  }

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setAdmin(status) {
    isAdmin.value = status
    localStorage.setItem('userRole', status ? 'admin' : 'user')
  }

  function logout() {
    user.value = null
    token.value = ''
    isAdmin.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('userRole')
    localStorage.removeItem('userId')
    // 可选：如有cart等其它用户相关localStorage，也可在此清理
  }

  return {
    user,
    token,
    isAdmin,
    setUser,
    setToken,
    setAdmin,
    logout
  }
}) 