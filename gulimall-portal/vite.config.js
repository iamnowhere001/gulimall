import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// 开发时通过 Vite 代理把 /api 转发到网关(gulimall-gateway:88)，
// 自动携带 /api 前缀，网关再按 /api/product/**、/api/order/** 等路由到各微服务。
// 这样浏览器看到的是同源(localhost:5173)，无需后端配置 CORS。
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:88',
        changeOrigin: true
      }
    }
  }
})
