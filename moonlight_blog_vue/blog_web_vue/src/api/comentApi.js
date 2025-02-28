import {service} from '../utils/request'

//获取博客评论
const fetchCommentsApi = (blogUid) => {
    const data = {blogUid}
    return  service.post('/blog-web/comment/getCommentByBlogUid', data); 
};
// 评论
const submitCommentApi = (data) => {
    return  service.post('/blog-web/comment/submitComment',data); 
};
// 评论点赞
const commentToggleLikeApi = (data) => {
    return  service.post('/blog-web/comment/toggleLike',data); 
};
//登录后，获取完整博客评论
const fetchCommentsAfterLoginApi = (blogUid) => {
    const data = {blogUid}
    return  service.post('/blog-web/comment/getCommentByBlogUidAfterLogin', data); 
};
// 获取用户的评论
const fetchUserCommentsApi = (data) => {
    return  service.post('/blog-web/comment/getUserComment', data); 
};
// 删除评论
const deleteCommentApi = (data) => {
    return  service.post('/blog-web/comment/deleteComment', data); 
};
// 更新评论
const updateCommentApi = (data) => {
    return  service.post('/blog-web/comment/updateComment', data); 
};
export{
    fetchCommentsApi,
    submitCommentApi,
    commentToggleLikeApi,
    fetchCommentsAfterLoginApi,
    fetchUserCommentsApi,
    deleteCommentApi,
    updateCommentApi
}



 