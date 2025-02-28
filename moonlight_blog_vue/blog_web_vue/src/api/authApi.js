import {service} from '../utils/request'
const loginApi = (data) => service.post('/blog-auth/auth/login', data)
const validateTokenApi =  (token) => service.get('/blog-auth/auth//validate', {params:{token}});
// 发送邮箱验证码
const sendEmailCodeApi = (data) => service.post('/blog-auth/auth/send-verification-code', data);
// 验证邮箱验证码
const validateEmailCodeApi = (data) => service.post('/blog-auth/auth/validate-verification-code', data);
// 账号注册
const registerApi = (data) => service.post('/blog-auth/auth/register', data);
// 重置密码
const resetPasswordApi = (data) => service.post('/blog-auth/auth/reset-password', data);
export {
    loginApi,
    validateTokenApi,
    sendEmailCodeApi,
    registerApi,
    resetPasswordApi,
    validateEmailCodeApi
}