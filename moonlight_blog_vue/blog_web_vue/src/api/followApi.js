import {service} from '../utils/request'
// 关注/ 取关
const followUserApi = (data) => service.post('/blog-web/follow/followUser', data)
// 获取关注状态
const getFollowStatusApi = (data) => service.post('/blog-web/follow/getFollowStatus', data)
// 获取所有关注作者的博客
const fetchFollowBlogsApi = (data) => service.post('/blog-web/follow/getFollowBlog', data)
// 获取关注列表
const fetchFollowListApi = (data) => service.post('/blog-web/follow/getFollowList', data)
export {
    followUserApi,
    getFollowStatusApi,
    fetchFollowBlogsApi,
    fetchFollowListApi
}