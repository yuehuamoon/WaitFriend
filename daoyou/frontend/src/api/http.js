import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({ baseURL: '', timeout:15000 })

http.interceptors.request.use((config) => {
 const token = localStorage.getItem('satoken')
 if (token) config.headers.satoken = token
 return config
})

http.interceptors.response.use(
 (response) => {
 const result = response.data
 if (result && result.success === false) {
 ElMessage.error(result.message || '操作失败')
 if (result.errorCode === '401') {
 localStorage.removeItem('satoken')
 if (location.pathname !== '/login') location.href = '/login'
 }
 return Promise.reject(new Error(result.message))
 }
 return result?.data
 },
 (error) => {
 if (error.response?.status ===401) {
 localStorage.removeItem('satoken')
 if (location.pathname !== '/login') location.href = '/login'
 }
 ElMessage.error(error.response?.data?.message || '服务暂时不可用')
 return Promise.reject(error)
 }
)

export default http
