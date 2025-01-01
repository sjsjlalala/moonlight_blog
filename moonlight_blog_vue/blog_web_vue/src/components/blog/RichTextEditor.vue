<template>
  <div class="blog-publish">
    <h1>发表博客</h1>

    <div class="form-group">
      <Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />

      <div class="form-control">
        <input type="value" required="">
        <label>
          <span style="transition-delay:0ms">请</span><span style="transition-delay:50ms"></span>
          <span style="transition-delay:100ms">输</span><span style="transition-delay:150ms"></span>
          <span style="transition-delay:200ms">入</span><span style="transition-delay:50ms"></span>
          <span style="transition-delay:300ms">标</span><span style="transition-delay:150ms"></span>
          <span style="transition-delay:400ms">题</span><span style="transition-delay:50ms"></span>
        </label>
      </div>

      <Editor style="height: 500px; overflow-y: hidden;" v-model="htmlValue" :defaultConfig="editorConfig" :mode="mode"
        @onCreated="handleCreated" />

    </div>
    <div class="form-group">
      <label for="coverImage">封面图片:</label>
      <input @change="handleFileChange" id="coverImage" type="file" accept="image/*" />
    </div>
    <div class="form-group">
      <label for="tags">标签:</label>
      <input v-model="blogData.tags" id="tags" type="text" placeholder="请输入博客标签，多个标签用逗号分隔" />
    </div>
    <div class="form-group">
      <label for="isOriginal">是否原创:</label>
      <input v-model="blogData.isOriginal" id="isOriginal" type="checkbox" />
    </div>
    <div class="form-group">
      <label for="reprintUrl">转载地址:</label>
      <input v-model="blogData.originalUrl" id="reprintUrl" type="text" placeholder="请输入转载地址（如果是转载）" />
    </div>
    <button @click="publishBlog">发布</button>
    <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import {editorRef, toolbarConfig, editorConfig, mode, handleCreated} from '../../../config/wangEditorConfig'




// 博客对象
const blogData = ref({
  title: '', // 博客标题
  introduction: '', // 博客简介
  content: '', // 博客内容
  commentsAllowed: '', // 是否允许评论
  coverImageUid: null, // 封面图片的 UID
  isOriginal: '', // 是否原创
  originalUrl: '', // 原创地址
  tags: '' // 博客标签
});

const htmlValue = ref('')
// 模拟 ajax 异步获取内容
onMounted(() => {
  setTimeout(() => {
    blogData.content= htmlValue.value
  }, 1500)

})

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})


const successMessage = ref('');
const errorMessage = ref('');

const handleFileChange = (event) => {
  blogData.value.coverImage = event.target.files[0];
};

const publishBlog = async () => {
  try {
    // 在这里可以调用后端 API 进行博客的发布操作
    // 例如使用 axios 发送请求
    // 假设我们有一个名为 postBlog 的 API 来发布博客
    // const response = await axios.post('/api/blogs', this.blogData);
    // 以下是模拟的成功和失败情况
    if (blogData.value.title && blogData.value.content && blogData.value.tags) {
      successMessage.value = '博客发布成功！';
      errorMessage.value = '';
      // 清空输入字段
      blogData.value.title = '';
      blogData.value.content = '';
      blogData.value.coverImage = null;
      blogData.value.tags = '';
      blogData.value.isOriginal = true;
      blogData.value.reprintUrl = '';
    } else {
      throw new Error('请填写所有必填项');
    }
  } catch (error) {
    errorMessage.value = error.message;
    successMessage.value = '';
  }
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
  
  justify-content: center;
  /* 水平居中 */
  align-items: center;
  /* 垂直居中 */
  box-sizing: border-box;
  /* 包含内边距和边框在宽度和高度内 */
  flex: 1;
  background-color: transparent;
}

.form-group {
  width: 100%;
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