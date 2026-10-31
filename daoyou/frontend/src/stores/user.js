import { defineStore } from 'pinia'
import { userApi } from '../api'

export const useUserStore = defineStore('user', {
 state: () => ({
 token: localStorage.getItem('satoken') || '',
 userInfo: null
 }),
 getters: {
 isLoggedIn: (state) => Boolean(state.token)
 },
 actions: {
 async login(payload) {
 const data = await userApi.login(payload)
 this.token = data.token
 localStorage.setItem('satoken', data.token)
 await this.fetchMe()
 return data
 },
 async register(payload) {
 const data = await userApi.register(payload)
 this.token = data.token
 localStorage.setItem('satoken', data.token)
 await this.fetchMe()
 return data
 },
 async fetchMe() {
 if (!this.token) return null
 this.userInfo = await userApi.me()
 return this.userInfo
 },
 async logout() {
 try { await userApi.logout() } catch (_) { }
 this.token = ''
 this.userInfo = null
 localStorage.removeItem('satoken')
 }
 }
})
