<template>
    <div class="subject-edit">
        <h2 class="edit-title">编辑专题</h2>
        <el-form :model="form" label-width="100px" class="edit-form">
            <!-- 标题 -->
            <el-form-item label="标题" class="form-item">
                <el-input v-model="form.subjectName" placeholder="请输入专题名称" class="input-field" clearable />
            </el-form-item>

            <!-- 简介 -->
            <el-form-item label="简介" class="form-item">
                <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入专题描述"
                    class="input-field" resize="none" />
            </el-form-item>

            <el-form-item label="专题分类" label-width="100px">
                <el-cascader placeholder="专题分类" :options="sysCategoryoptions" filterable :show-all-levels="false"
                  tag-type="success" tag-effect="light" size="large" v-model="form.categoryUid" />
              </el-form-item>

            <!-- 封面图片 -->
            <el-form-item label="专题封面" label-width="100px">
                <div class="form-group">
                    <el-upload :on-success="handleSuccessOfSubject" :on-error="handleError" :on-remove="handleRemove"
                        :limit="1" :file-list="fileList" :auto-upload="true" list-type="picture-card"
                        :http-request="customUploadRequest">
                        <el-icon>
                            <Plus />
                        </el-icon>
                    </el-upload>
                </div>
            </el-form-item>

            <!-- 保存和取消按钮 -->
            <el-form-item class="form-item">
                <el-button type="primary" @click="save" class="save-button">
                    保存
                </el-button>
               
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { ref, watch, inject, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Upload, Close } from '@element-plus/icons-vue';
import { fetchSubjectDetailApi, updateSubjectApi } from '../../api/subjectApi'
import {fetchSysCategoryApi} from '../../api/categoryApi'
import { Plus } from '@element-plus/icons-vue'

import axios from 'axios';
// 接收专题数据作为 props
const subjectUid = inject('subjectUid');
const isFresh = inject('isFresh');
const fileList = ref([])

// 专题数据
const subject = ref({});
// 表单数据
const form = ref({
  subjectName: '',
  summary: '',
  fileUid: '',
  categoryUid: ''
});
// 专题列表
const subjectOptions = ref([]);
// 系统分组
const sysCategoryoptions = ref([]);
// 存储分组链
const newArray = ref(null);

// 保存编辑
const save = () => {
    console.log('保存专题数据', form.value);
    console.log('newArray', newArray.value)
   
    // 调用 API 保存数据
    updateSubjectApi(form.value).then(response => {
        if (response.code === 200) {
            ElMessage.success('保存成功');
            isFresh.value = true;
        } else {
            ElMessage.error('保存失败');
        }
    });
    
};

// 取消编辑
const cancel = () => {
    form.value = { ...props.subject }; // 恢复原始数据
    ElMessage.info('已取消编辑');
};
// 获取专题详情
const fetchSubjectDetail = async () => {
    const data = {
        uid: subjectUid.value
    };

    const response = await fetchSubjectDetailApi(data);
    if (response.code === 200) {
        console.log('专题详情:', form.value)
        form.value = response.data[0].subject;
        console.log("form ", form.value)
        fileList.value = form.value.fileUid ? [{ url: form.value.fileUrl }] : [];
        newArray.value = form.value.categoryUid

    } else {
        ElMessage.error('获取专题详情失败');
        console.error('获取专题详情失败:', response.message);
    }
};

// 获取系统分组
const fetchConfigCategory = async () => {
  try {
    const response = await fetchSysCategoryApi();
    sysCategoryoptions.value = response.data;
  } catch (error) {
    console.error('获取系统分组数据失败:', error);
  }
};
const handleSuccessOfSubject = (response, file, fileList) => {
    console.log('上传成功:', response)
    console.log('文件:', file)
    console.log('文件列表:', fileList)
    // 将上传后返回的图片uuid保存
    console.log('上传后返回的图片uuid:', response.data)
    form.value.fileUid = response.data.uid
    
}
const handleError = (err, file, fileList) => {
  console.error('上传失败:', err)
  console.log('文件:', file)
  console.log('文件列表:', fileList)
}

const customUploadRequest = (options) => {
  const { file, onSuccess, onError } = options;
  const formData = new FormData();
  formData.append('file', file);

  axios.post('/api/blog-file/upload/uploadImage', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'Authorization': 'Bearer ' + localStorage.getItem('token') // 自定义请求头
    }
  })
    .then(response => {
      onSuccess(response.data); // 上传成功后的回调
    })
    .catch(error => {
      onError(error); // 上传失败后的回调
    });
}
// 处理图片删除
const handleRemove = async (file, fileList) => {
    form.value.fileUid = ''
};
onMounted(async () => {
    await fetchSubjectDetail();
    await fetchConfigCategory()
});
</script>

<style scoped>
.subject-edit {
    padding: 20px;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.edit-title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 20px;
}

.edit-form {
    padding: 10px;
}

.form-item {
    margin-bottom: 20px;
}

.input-field {
    width: 100%;
}

.cover-upload {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

.upload-button {
    margin-bottom: 10px;
}

.cover-preview {
    position: relative;
    width: 100%;
    max-width: 200px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.cover-image {
    width: 100%;
    display: block;
}

.remove-icon {
    position: absolute;
    top: 8px;
    right: 8px;
    background-color: rgba(255, 255, 255, 0.8);
    border-radius: 50%;
    padding: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.remove-icon:hover {
    background-color: rgba(255, 255, 255, 1);
}

.save-button,
.cancel-button {
    width: 100px;
}

.save-button {
    background-color: #409eff;
    border-color: #409eff;
}

.cancel-button {
    margin-left: 10px;
}
</style>