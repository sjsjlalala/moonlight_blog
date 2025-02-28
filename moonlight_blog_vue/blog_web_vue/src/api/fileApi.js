import {service} from '../utils/request'
// 删除文件
const deleteFileApi = (uuids) => {
    const formData = new URLSearchParams();
    uuids.forEach(uuid => formData.append('uuids', uuid));
    return service.post('/blog-file/file/deleteFile', formData, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    });
};
// 上传文件
const uploadFileApi = (data) => {
    return service.post('/blog-file/file/uploadFile', data, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
};
export {
    deleteFileApi,
    uploadFileApi
}