import {service} from '../utils/request'
// 获取标签列表
const fetchTagsOptionsApi = () => {
    return service.get('/blog-web/tag/getTags');
};

export {
    fetchTagsOptionsApi

}
