
import {service} from '../utils/request'

// 获取标签列表
const  fetchTagsOptionsApi = () => {
    return  service.get('/blog-web/tag/getTags'); 
  };

// 获取用户分组
const fetchUserCategoriesApi = () => {
    return  service.get('/blog-web/category/getUserCategories'); 
};

// 发表博客
const submitBlogApi = (data) => {
    return  service.post('/blog-web/blog/submitBlog',data); 
};

// 获取所有博客列表，首页展示用
const fetchBlogListApi = (currentPage, pageSize, keyword, authorUid) => {
    const blogVO = { currentPage, pageSize, keyword, authorUid};
    return  service.post('/blog-web/blog/blogList', blogVO); 
};
// 获取博客详情
const fetchBlogDetailApi = (data) => {
    return  service.post('/blog-web/blog/blogDetailByUid',data); 
};
// 博客点赞/取消点赞
const blogToggleLikeApi = (data) => {
    return  service.post('/blog-web/blog/blogToggleLike', data); 
};
// 博客收藏/取消收藏
const blogToggleCollectionApi = (data) => {
    return  service.post('/blog-web/blog/blogToggleCollection', data); 
};
// 创建博客收藏夹
const createBlogCollectionCategoryApi = (data) => {
    return  service.post('/blog-web/category/createBlogCollectionCategory', data); 
};
// 加载博客收藏夹
const fetchBlogCollectionCategoryApi = (uid) => {
    return  service.get(`/blog-web/category/fetchBlogCollectionCategory/${uid}`); 
};
// 收藏/取消收藏博客
const toggleBlogCollectionApi = (data) => {
    return  service.post('/blog-web/category/toggleBlogCollection', data); 
};
// 加载用户的收藏博客列表
const fetchBlogCollectionApi = (uid) => {
    return  service.get(`/blog-web/category/getUserCollection/${uid}` ); 
};
// 修改收藏夹
const updateUserCategoryApi = (data) => {
    return  service.post('/blog-web/category/updateBlogCollectionCategory',data);
};
// 删除用户收藏夹
const deleteUserCategoryApi = (data) => {
    return  service.post('/blog-web/category/deleteBlogCollectionCategory',data);
};
// 根据标签id获取对应博客列表
const fetchBlogListByTagApi = (tagUid) => {
    return  service.get(`/blog-web/blog/fetchBlogListByTag/${tagUid}`); 
};
// 根据uid删除博客
const deleteBlogApi = (data) => {
    return  service.post(`/blog-web/blog/deleteBlogByUid`, data); 
};
// 登录后，获取完整博客信息
const fetchBlogLikeAndCollectionApi = (data) => {
    return  service.post('/blog-web/blog/getBlogLikeAndCollection', data);
};
// 更新博客
const updateBlogApi = (data) => {
   return service.post(`/blog-web/blog/updateBlogByUid`, data);
}
// 获取用户主页信息
const fetchUserHomeApi = (uid) => {
    return  service.get(`/blog-web/blog/getUserHomePage/${uid}`);
}


export {
    fetchTagsOptionsApi,
    fetchUserCategoriesApi,
    submitBlogApi,
    fetchBlogListApi,
    fetchBlogDetailApi,
    blogToggleLikeApi,
    blogToggleCollectionApi,
    createBlogCollectionCategoryApi,
    fetchBlogCollectionCategoryApi,
    toggleBlogCollectionApi,
    fetchBlogListByTagApi,
    deleteBlogApi,
    fetchBlogLikeAndCollectionApi,
    updateBlogApi,
    fetchBlogCollectionApi,
    updateUserCategoryApi,
    deleteUserCategoryApi,
    fetchUserHomeApi
}

