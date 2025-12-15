import { defineConfig } from 'vite'
import vue2 from '@vitejs/plugin-vue2'
import { loadEnv } from 'vite'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  // 默认使用8082端口（MySQL后端），可通过BACKEND_PORT环境变量切换端口
  const backendPort = env.VITE_BACKEND_PORT || '8082'
  
  return {
    plugins: [vue2()],
    server: {
      proxy: {
        '/api': {
          target: `http://localhost:${backendPort}`,
          changeOrigin: true
        }
      }
    }
  }
})