import {service} from '../utils/request'
// 更新用户信息
const updateUserApi = (data) => {
    return service.post('/blog-web/user/updateUserInfo', data);
};
// 修改邮箱
const updateEmailApi = (data) => {
    return service.post('/blog-web/user/updateUserEmail', data);
};
export {
    updateUserApi,
    updateEmailApi

}