import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router' // 引入路由以便跳转

const service = axios.create({
  baseURL: 'http://localhost:8080', // 根据实际后端端口调整
  timeout: 10000
})

// request 拦截器
service.interceptors.request.use(
  config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8'
    
    // 从 LocalStorage 取 Token
    const token = localStorage.getItem('token')
    if (token) {
      // 注意：Key是 'Authorization'，Value通常需要 'Bearer ' 前缀吗？
      // 看你后端的 JwtUtils 解析逻辑，如果后端没去掉 Bearer，这里就不用加。
      // 根据之前提供的后端代码，是直接解析的，所以不需要加 'Bearer ' 前缀。
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
    // 兼容 Blob 流 (如导出Excel)
    if (response.config.responseType === 'blob') {
      return res
    }
    // 业务逻辑判断
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.msg || '系统错误')
      return Promise.reject(new Error(res.msg || 'Error'))
    }
  },
  error => {
    console.error('err' + error)
    let { message } = error
    if (error.response && error.response.status === 401) {
      // 401 说明 Token 过期或无效
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    } else {
      ElMessage.error(message || '网络连接异常')
    }
    return Promise.reject(error)
  }
)

export default service