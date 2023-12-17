import axios from 'axios'

// 创建axios实例, 将来基于创建出的实例进行配置和请求
// 这样做不会污染原始axios实例
const instance = axios.create({
  baseURL: 'http://localhost:9090/api',
  timeout: 5000
})

let userInfo={
  'accessToken':''
}
if(localStorage.getItem('KEY_COMMON')){
  userInfo=window.JSON.parse(localStorage.getItem('KEY_COMMON')).userInfo;
}

// 添加请求拦截器
instance.interceptors.request.use(function (config) {
  // 在发送请求之前携带token
  config.headers.Authorization = 'Bearer ' + userInfo.accessToken;
  console.log('请求url: ', config.url, '; 请求信息: ', config)
  return config
}, function (error) {
  // 处理请求错误
  return Promise.reject(error)
})

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
  // 2xx 范围内的状态码都会触发该函数。
  // axios会把响应数据多包装一层
  return response
}, function (error) {
  // 超出 2xx 范围的状态码都会触发该函数。
  // 处理响应错误
  console.error('请求出错! url: ', error.config.url, '; 详细信息: ', error)
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