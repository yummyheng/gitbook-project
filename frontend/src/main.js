import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'

Vue.use(ElementUI)

// 创建axios实例
const axiosInstance = axios.create({
  baseURL: '/api'
})

// 添加请求拦截器
axiosInstance.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么
    console.log('Request URL:', config.url)
    console.log('Request Method:', config.method)
    console.log('Request Params:', config.params)
    console.log('Request Data:', config.data)
    return config
  },
  (error) => {
    // 处理请求错误
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

// 添加响应拦截器
axiosInstance.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    console.log('Response URL:', response.config.url)
    console.log('Response Status:', response.status)
    console.log('Response Data:', response.data)
    return response
  },
  (error) => {
    // 处理响应错误
    console.error('Response Error:', error)
    return Promise.reject(error)
  }
)

// 将axios实例挂载到Vue原型
Vue.prototype.$http = axiosInstance

import './assets/main.scss'

new Vue({
  render: (h) => h(App)
}).$mount('#app')
