import {service} from '../utils/request'
// 创建专题
 const createSubjectApi = (data) => {
  return service.post('/blog-web/subject/createSubject', data);
}
// 获取用户专题
 const fetchUserSubjectApi = () => {
  return service.get('/blog-web/subject/fetchUserSubject');
}
// 获取专题详情
 const fetchSubjectDetailApi = (data) => {
  return service.post('/blog-web/subject/fetchSubjectDetail',data);
}
// 更新专题
 const updateSubjectApi = (data) => {
  return service.post('/blog-web/subject/updateSubject',data);
}
// 分页获取专题下的博客
 const fetchSubjectBlogApi = (data) => {
  return service.post('/blog-web/subject/fetchSubjectBlog',data);
}
// 给专题添加博客
 const addSubjectBlogApi = (data) => {
  return service.post('/blog-web/subject/addSubjectBlog',data);
}
// 删除专题中的博客
 const deleteSubjectBlogApi = (data) => {
  return service.post('/blog-web/subject/deleteSubjectBlog',data);
}
export {
    createSubjectApi,
    fetchUserSubjectApi,
    fetchSubjectDetailApi,
    updateSubjectApi,
    fetchSubjectBlogApi,
    addSubjectBlogApi,
    deleteSubjectBlogApi
}