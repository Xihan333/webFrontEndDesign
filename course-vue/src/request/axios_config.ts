import axios from 'axios'
import { ElLoading } from 'element-plus'

// 创建axios实例, 将来基于创建出的实例进行配置和请求
// 这样做不会污染原始axios实例
const instance = axios.create({
  baseURL: '',
  timeout: 5000
})

let loading:any;

// 添加请求拦截器
instance.interceptors.request.use(function (config) {
  // 在发送请求之前携带token(如果有的话)
  config.headers.Authorization = 'Bearer ' + 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMDIyMDMwMDAzIiwiaWF0IjoxNzAxOTQ5OTA4LCJleHAiOjE3MDIwMzYzMDh9.YURe0nkBVDJbW4k_XH65gBA92HN8ve7mvT-eUFethgUulGHNIOaBRNuMPSzKj-w59okGs-boLvTsQ4mgfR5_RA'
  console.log('请求url: ', config.url, '; 请求信息: ', config)
  // 开启加载动画
  loading = ElLoading.service({
    lock: true,
    text: '加载中...'
  })
  return config
}, function (error) {
  // 处理请求错误
  loading.close()
  return Promise.reject(error)
})

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
  // 2xx 范围内的状态码都会触发该函数。
  loading.close()
  // axios会把响应数据多包装一层
  return response
}, function (error) {
  // 超出 2xx 范围的状态码都会触发该函数。
  // 处理响应错误
  console.error('请求出错! url: ', error.config.url, '; 详细信息: ', error)
  loading.close()
  // 请求错误的话会返回一个AxiosError对象, 根据其中的response.status状态码判断即可
  // 省得一堆报错和try catch了
  return error
})

// 导出配置好的实例
// 此后这个instance就可当做axios的一个包含特别配置的分身来用
export default instance


/* 在组件内使用
 * import request(别名) from 本文件
 */