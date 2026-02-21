import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: 'http://localhost:8080', // 这里的端口要和你后端 application.yml 配置的一致
  timeout: 10000
})

// request 拦截器
service.interceptors.request.use(
  config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8'
    
    // 从 LocalStorage 取 Token
    const token = localStorage.getItem('token')
    if (token) {
      // 这里的 Authorization 要和后端 JwtInterceptor 里获取的 Header 名字保持一致
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 兼容 Blob 流 (下载文件时)
    if (response.config.responseType === 'blob') {
      return res
    }
    // 业务逻辑判断 (根据你后端的 Result 类 code=200 为成功)
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.msg || '系统错误')
      return Promise.reject(new Error(res.msg || 'Error'))
    }
  },
  error => {
    console.error('err' + error)
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    } else {
      ElMessage.error(error.message || '网络连接异常')
    }
    return Promise.reject(error)
  }
)

export default service