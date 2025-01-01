// src/utils/request.js

import axios from 'axios';

// 创建一个函数来创建 Axios 实例
function createService(baseURL) {
  const service = axios.create({
    baseURL: baseURL,
    timeout: 5000, // 请求超时时间
  });

  // 请求拦截器
  service.interceptors.request.use(
    config => {
      console.log('Request:', config); // 添加请求日志
      const token = localStorage.getItem('token');
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
      }
      return config;
    },
    error => {
      console.error('Request Error:', error);
      return Promise.reject(error);
    }
  );

  // 响应拦截器
  service.interceptors.response.use(
    response => {
      console.log('Response:', response); // 添加响应日志
      return response.data;
    },
    error => {
      handleErrorResponse(error);
      return Promise.reject(error);
    }
  );

  return service;
}

// 统一错误处理函数
function handleErrorResponse(error) {
  if (error.response) {
    switch (error.response.status) {
      case 401:
        console.error('未授权，请重新登录');
        break;
      case 404:
        console.error('请求的资源不存在');
        break;
      default:
        console.error('请求失败，请稍后再试');
    }
  } else if (error.request) {
    console.error('请求失败，请检查网络连接');
  } else {
    console.error('请求配置错误');
  }
}

// 创建Axios 实例
const service = createService('/api');


export {service};
