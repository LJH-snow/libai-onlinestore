/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-16 10:36:47
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-22 17:38:04
 * @FilePath: \Sofeware_Demo\src\stores\cart.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import { useUserStore } from './user'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])

  const totalItems = computed(() => {
    return items.value.reduce((total, item) => total + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return items.value.reduce((total, item) => total + item.price * item.quantity, 0)
  })

  async function fetchCart() {
    const userStore = useUserStore()
    if (!userStore.token) return
    try {
      const res = await axios.get('/api/cart')
      // 确保items总是数组
      items.value = Array.isArray(res.data) ? res.data : (res.data?.items || [])
    } catch (error) {
      console.error('获取购物车失败:', error)
      items.value = []
    }
  }

  async function addItem(book, quantity = 1) {
    const userStore = useUserStore()
    const userId = userStore.user?.id || localStorage.getItem('userId')
    if (!userId) return

    const existingItem = items.value.find(item => item.bookId === book.id)
    if (existingItem) {
      await updateQuantity(existingItem.id, existingItem.quantity + quantity)
    } else {
      const cartItem = {
        bookId: book.id,
        quantity: quantity
      }
      await axios.post('/api/cart/items', cartItem)
    }

    await fetchCart()
  }

  async function removeItem(cartItemId) {
    await axios.delete(`/api/cart/items/${cartItemId}`)
    await fetchCart()
  }

  async function updateQuantity(cartItemId, quantity) {
    const itemToUpdate = items.value.find(item => item.id === cartItemId)
    if (!itemToUpdate) return

    const updatedItem = { 
      ...itemToUpdate, 
      quantity 
    }
    
    await axios.put(`/api/cart/items/${cartItemId}`, { quantity })
    await fetchCart()
  }

  async function clearCart() {
    const userStore = useUserStore()
    if (!userStore.token) return
    await axios.delete('/api/cart/clear')
    items.value = []
  }

  return {
    items,
    totalItems,
    totalPrice,
    fetchCart,
    addItem,
    removeItem,
    updateQuantity,
    clearCart
  }
}) 