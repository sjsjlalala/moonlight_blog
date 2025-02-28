import {service} from '../utils/request'
const createUserCategoryApi = (data) => {
    return  service.post('/blog-web/category/createUserCategory',data);
}
// 获取系统分类
const fetchSysCategoryApi = () => {
    return  service.get('/blog-web/category/fetchSysCategory');
} 
// 获取分组博客
const fetchCategoryBlogApi = (categoryId) => {
    return  service.get('/blog-web/category/fetchBlogListByCategory/'+ categoryId);
}
// 更新博客的分组
const updateBlogCategoryApi = (data) => {
    return  service.post('/blog-web/category/updateBlogCategory',data);
}
// 删除分组
const deleteCategoryApi = (data) => {
    return  service.post('/blog-web/category/deleteBlogCategory',data);
}

export {
    updateBlogCategoryApi,
    createUserCategoryApi,
    fetchSysCategoryApi,
    fetchCategoryBlogApi,
    deleteCategoryApi
}