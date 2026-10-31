import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
 plugins: [vue()],
 server: {
 port:5173,
 host:true,
 proxy: {
 '/user': 'http://localhost:8080',
 '/map': 'http://localhost:8080',
 '/content': 'http://localhost:8080',
 '/interaction': 'http://localhost:8080',
 '/rewrite': 'http://localhost:8080',
 '/notification': 'http://localhost:8080',
 '/count': 'http://localhost:8080',
 '/search': 'http://localhost:8080',
 '/admin': 'http://localhost:8080'
 }
 }
})
