<template>
  <div class="blog-publish">
    <h1>发表博客</h1>
    <div class="form-group">
      <Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />

      <div class="form-control">
        <input type="value" required="" v-model="blogData.title">
        <label>
          <span style="transition-delay:0ms">请</span><span style="transition-delay:50ms"></span>
          <span style="transition-delay:100ms">输</span><span style="transition-delay:150ms"></span>
          <span style="transition-delay:200ms">入</span><span style="transition-delay:50ms"></span>
          <span style="transition-delay:300ms">标</span><span style="transition-delay:150ms"></span>
          <span style="transition-delay:400ms">题</span><span style="transition-delay:50ms"></span>
        </label>
      </div>

      <Editor style="height: 500px; overflow-y: hidden;" v-model="blogData.content" :defaultConfig="editorConfig" :mode="mode"
        @onCreated="handleCreated" />

    </div>
    <div class="form-group">
      <label for="image">封面图片:</label>
      <el-upload ref="upload" :on-exceed="handleExceed" :on-success="handleSuccess" :on-error="handleError" :limit="1"
        :file-list="fileList" :auto-upload="true"  list-type="picture-card"
        :http-request="customUploadRequest">
        <el-icon>
          <Plus />
        </el-icon>
      </el-upload>
    </div>
    <div class="form-control">
        <input type="value" required="" v-model="blogData.introduction">
        <label>
          <span style="transition-delay:0ms">请</span><span style="transition-delay:50ms"></span>
          <span style="transition-delay:100ms">输</span><span style="transition-delay:150ms"></span>
          <span style="transition-delay:200ms">入</span><span style="transition-delay:50ms"></span>
          <span style="transition-delay:300ms">简</span><span style="transition-delay:150ms"></span>
          <span style="transition-delay:400ms">介</span><span style="transition-delay:50ms"></span>
        </label>
      </div>
    <div class="form-group">
      <label for="tags">标签:</label>
      <div class="m-4">
        <el-cascader placeholder="请选择标签,可搜索" :options="options" :props="props" filterable :show-all-levels="false"
          tag-type="success" tag-effect="light" size="large" v-model="blogData.tags" />
      </div>
    </div>
    <div class="form-group">
      <label for="isOriginal">是否原创:</label>
      <el-switch v-model="blogData.isOriginal" />
    </div>
    <div class="form-group" v-if="!blogData.isOriginal">
      <div class="form-control">
        <input type="value" required="" v-model="blogData.originalUrl">
        <label>
          <span style="transition-delay:0ms">请</span><span style="transition-delay:50ms"></span>
          <span style="transition-delay:100ms">输</span><span style="transition-delay:150ms"></span>
          <span style="transition-delay:200ms">入</span><span style="transition-delay:50ms"></span>
          <span style="transition-delay:300ms">转</span><span style="transition-delay:150ms"></span>
          <span style="transition-delay:400ms">载</span><span style="transition-delay:50ms"></span>
          <span style="transition-delay:400ms">地</span><span style="transition-delay:50ms"></span>
          <span style="transition-delay:400ms">址</span><span style="transition-delay:50ms"></span>
        </label>
      </div>
    </div>
    <div class="form-other">
      <div>
        <label for="commentsAllowed">是否开启评论:</label>
        <el-switch v-model="blogData.commentsAllowed" />
      </div>
      <div class="form-scope">
        <label for="catogory">可见范围: </label>
        <div class="my-2 ml-4">
          <el-radio-group v-model="blogData.visibilityScope">
            <el-radio value="1">所有人可见</el-radio>
            <el-radio value="2">仅自己可见</el-radio>
          </el-radio-group>
        </div>
      </div>
      <div>
        <label for="catogory">分组目录:</label>
        <div class="m-4">
        <el-cascader placeholder="请选择分组,可搜索" :options="categoryOptions" :props = "categoryProps" filterable :show-all-levels="true"
          tag-type="success" tag-effect="light" size="large" v-model="blogData.category" />
      </div>
      </div>
    </div>
    <el-button type="primary" plain size="large" color="#626aef" @click="publishBlog" :dark="false"
      :plain="true">发表文章</el-button>
    <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import {fetchTagsOptionsApi, fetchUserCategoriesApi, submitBlogApi} from '../../api/blogApi'
import '@wangeditor/editor/dist/css/style.css'; // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import { editorRef, toolbarConfig, editorConfig, mode, handleCreated,imgUidList } from '../../../config/wangEditorConfig';
import axios from 'axios';
import { ElMessage, genFileId } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'



// 博客对象
const blogData = ref({
  title: '第一篇博客', // 博客标题
  introduction: '第一篇博客的简介', // 博客简介
  content: '第一篇博客的内容', // 博客内容
  commentsAllowed: true, // 是否允许评论
  coverImageUid: null, // 封面图片的 UID
  isOriginal: false, // 是否原创
  originalUrl: 'http://localhost', // 原创地址
  tags: '', // 博客标签
  visibilityScope: '1',// 可见范围
  category: '' // 博客分类
});
// 封面对象
const upload = ref()
const fileList = ref([])
const uploadedFiles = ref([])
const coverImageUuid = ref(null)

// 标签对象
const options = ref([]);
// 分组对象
const categoryOptions = ref([]);

// 标签级联选择器属性
const props = {
  multiple: true,
  checkStrictly: false
};
// 分组级联选择器属性
const categoryProps = {
  checkStrictly: false
};

const successMessage = ref('');
const errorMessage = ref('');


const validateBlogData = () => {
  if (!blogData.value.title) {
    throw new Error('请填写标题');
  }
  if (!blogData.value.content) {
    throw new Error('请填写内容');
  }
  if (!blogData.value.tags) {
    throw new Error('请选择标签');
  }
  if (!blogData.value.category) {
    throw new Error('请选择分组');
  }
  if (!blogData.value.isOriginal && !blogData.value.originalUrl) {
    throw new Error('请填写转载链接');
  }
};

const publishBlog = async () => {
  try {
    validateBlogData();
    await submitBlog(); // 修正了这里缺少 await 的问题

    ElMessage({
      showClose: true,
      message: 博客发布成功,
      type: 'success',
    });

    // 清空输入字段
    blogData.value.title = '';
    blogData.value.content = '';
    blogData.value.coverImageUid = null;
    blogData.value.tags = null;
    blogData.value.isOriginal = true;
    blogData.value.originalUrl = '';
    blogData.value.category = null;
    blogData.value.coverImageUid = '';
    blogData.value.introduction = '';
  } catch (error) {
    ElMessage({
      showClose: true,
      message: error.message,
      type: 'error',
    });
   
  }
  
};

// 获取标签列表
const fetchOptions = async () => {
  try {
    const response = await fetchTagsOptionsApi(); // 假设后端 API 地址为 /api/tags
    console.log('标签列表:', response.data);
    options.value = response.data;
  } catch (error) {
    console.error('获取标签数据失败:', error);
  }
};
// 获取用户分组
const fetchCategories = async () => {
  try {
    const response = await fetchUserCategoriesApi();
    categoryOptions.value = response.data;
  } catch (error) {
    console.error('获取用户分组数据失败:', error);
  }
};

onMounted(() => {
  fetchOptions();
  fetchCategories();
});

onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
  editorRef.value.destroy();

});
// 封面上传
const handleExceed = (files) => {
  if (upload.value) {
    upload.value.clearFiles()
    const file = files[0]
    file.uid = genFileId()
    fileList.value = [file]
    upload.value.handleStart(file)
  }
}


const handleSuccess = (response, file, fileList) => {
  console.log('上传成功:', response)
  console.log('文件:', file)
  console.log('文件列表:', fileList)
  // 将上传后返回的图片uuid保存
  console.log('上传后返回的图片uuid:', response.data)
  uploadedFiles.value.push({ file, uuid: response.data.uid })
  blogData.value.coverImageUid = response.data.uid
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


const submitBlog = async () => {
  
  // 获取要删除的图片的 UUID
  //1.封面
  const filesToDelete = uploadedFiles.value.filter(f => f.uuid !== blogData.value.coverImageUid);
  const uuidsToDelete = filesToDelete.map(f => f.uuid);
  //2.插入的图片
  imgUidList.value.pop();
  uuidsToDelete.push(... imgUidList.value);
  
  // 直接使用 blogData.value 的值
  const requestData = {
    blogVO: { ...blogData.value }, // 使用解构赋值将响应式对象转换为普通对象
    uuidsToDelete: uuidsToDelete
  };

  // 发送封面图片 UUID 和要删除的图片 UUID 到后端
  try {
    const response = await submitBlogApi(requestData);
    console.log(response.data);

    if (response.ok) {
      const data = await response.json();
      console.log('博客发表成功:', data);
      successMessage.value = '博客发布成功！';
      errorMessage.value = '';
      // 清空输入字段
      blogData.value.title = '';
      blogData.value.content = '';
      blogData.value.coverImageUid = null;
      blogData.value.tags = '';
      blogData.value.isOriginal = true;
      blogData.value.originalUrl = '';
    } else {
      console.error('博客发表失败:', response.statusText);
      errorMessage.value = '博客发表失败';
      successMessage.value = '';
    }
  } catch (error) {
    console.error('博客发表失败:', error);
    errorMessage.value = error.message;
    successMessage.value = '';
  }

  // 清空文件列表
  fileList.value = [];
  uploadedFiles.value = [];
  coverImageUuid.value = null;
};
</script>


<style scoped>
.blog-publish {
  width: 100%;
  /* 铺满宽度 */
  height: 100%;
  /* 铺满高度 */
  border: 0px solid #ccc;
  padding: 20px;
  margin: 0;
  /* 移除边距 */
  display: flex;
  flex-direction: column;

  justify-content: flex-start;
  /* 子元素靠左对齐 */
  align-items: flex-start;
  /* 确保子元素在交叉轴上靠上对齐 */
  /* 垂直居中 */
  box-sizing: border-box;
  /* 包含内边距和边框在宽度和高度内 */
  flex: 1;
  background-color: transparent;
}

.form-group {

  /* 使.form-group宽度铺满.blog-publish */
  margin: 5px 0;
  /* 垂直外边距 */
  background-color: transparent;
  border: 1px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  justify-content: center;
  /* 水平居中 */

}

.form-other {
  width: 100%;
  margin: 5px 0;
  background-color: transparent;
  border: 1px solid #ccc;
  /* 添加边框 */
  padding: 10px;
  display: flex;
  flex-direction: row;
  box-sizing: border-box;
  justify-content: flex-start;
  /* 子元素靠左对齐 */
  align-items: flex-start;
  /* 确保子元素在交叉轴上靠上对齐 */
}

.form-other>div {
  flex: 1;
  /* 让每个子元素平分可用空间 */
  margin-right: 10px;
  /* 可选：为子元素之间添加间距 */
}

.form-other>div:last-child {
  margin-right: 0;
  /* 移除最后一个子元素的右侧间距 */
}

.form-scope {
  display: flex;
  flex-direction: row;
  box-sizing: border-box;
  align-items: center;
  /* 垂直对齐子元素 */
}

.success-message {
  color: green;
  margin-top: 10px;
}

.error-message {
  color: red;
  margin-top: 10px;
}

.form-control {
  position: relative;
  margin: 20px 0 40px;
  width: 100%;
}

.form-control input {
  background-color: transparent;
  border: 0;
  border-bottom: 2px #0f0f0f solid;
  display: flex;
  width: 100%;
  padding: 15px 0;
  font-size: 18px;
  color: #111010;
}

.form-control input:focus {
  outline: 0;
  border-bottom-color: rgb(18, 18, 19);
}

.form-control input:valid {
  outline: 0;
  border-bottom-color: rgb(18, 18, 19);
}

.form-control label {
  position: absolute;
  top: 15px;
  left: 0;
  pointer-events: none;
}

.form-control label span {
  display: inline-block;
  font-size: 18px;
  min-width: 5px;
  color: #181717;
  border: #ccc;
  transition: 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.form-control input:focus+label span,
.form-control input:valid+label span {
  color: rgb(141, 164, 175);
  transform: translateY(-30px);
}
</style>