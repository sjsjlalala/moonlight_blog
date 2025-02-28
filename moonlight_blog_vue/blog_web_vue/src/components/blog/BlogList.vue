<template>
  <div class="blog-list-container">
    <BlogCard v-for="blog in blogs" :key="blog.blogVO?.uid || blog.userVO?.uid" :blog="blog.blogVO || {}"
      :author="blog.userVO || {}" :tags="blog.tags || []" />
    <div v-if="loading" class="loading">加载中...</div>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="noMore" class="no-more">没有更多了</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { fetchBlogListApi } from '../../api/blogApi'
import { useRouter } from 'vue-router'
import BlogCard from './BlogCard.vue'

const router = useRouter()
const blogs = ref([])
const users = ref([])
const loading = ref(false)
const error = ref(null)
const noMore = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const props = defineProps({
  keyword: {
    type: String,
    default: ''
  },
  uid: {
    type: String,
    default: ''
  }
})



const fetchBlogs = async (page, size, keyword ) => {
  loading.value = true;
  error.value = null;

  // 清空 blogs 和 users
  if (page === 1) {
    blogs.value = [];
    users.value = [];
  }

  try {
    const response = await fetchBlogListApi(page, size, keyword, props.uid);

    if (!response || response.code !== 200) {
      throw new Error(response?.message || '获取博客列表失败');
    }

    const data = response.data || [];

    if (data.length < size) {
      noMore.value = true; // 数据不足size，说明是最后一页
    }

    if (data.length > 0) {
      blogs.value.push(...data);
    }

    console.log('博客列表', blogs.value);
    
  } catch (err) {
    error.value = err.message || '获取博客失败';
  } finally {
    loading.value = false;
  }
};


// 滚动到底端的加载方法
const load = () => {
  if (loading.value || noMore.value) return
  currentPage.value++
  fetchBlogs(currentPage.value, pageSize.value, props.keyword)
}

const goToBlogDetail = (uid) => {
  router.push({ name: 'BlogDetail', params: { uid } })
}

// 计算属性或方法来获取封面图片
const getCoverImage = (coverImageUid) => {
  return coverImageUid ? coverImageUid : import.meta.env.VITE_BLOG_LOGO;
}

// 统一提取的函数，执行 onMounted 和 keyword 变化时的逻辑
const fetchData = () => {
  console.log('keyword', props.keyword)
  if (props.keyword) {
    fetchBlogs(currentPage.value, pageSize.value, props.keyword)
  } else {
    fetchBlogs(currentPage.value, pageSize.value)
  }
}
// **计算属性：高亮 keyword**
const highlightKeyword = (text) => {
  console.log('highlightKeyword', props.keyword)

  if (!props.keyword) return text
  const regex = new RegExp(`(${props.keyword})`, 'gi') // 忽略大小写匹配
  return text.replace(regex, '<span class="highlight">$1</span>')
}

// 初始加载时调用
onMounted(() => {
  fetchData()
  window.addEventListener('scroll', handleScroll)
})

// 监听 keyword 的变化，当 keyword 不为空时重新执行 fetchData
watch(() => props.keyword, (newKeyword, oldKeyword) => {
  if (newKeyword && newKeyword !== oldKeyword) {
    fetchData()
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 滚动事件处理函数
const handleScroll = () => {
  const { scrollTop, clientHeight, scrollHeight } = document.documentElement
  if (scrollTop + clientHeight >= scrollHeight - 100) { // 100为缓冲区，可以根据需要调整
    load()
  }
}
</script>

<style scoped>
.blog-list-container {
  background-color: #ffffff;
  /* 整个组件的背景色 */
  padding: 10px;
}

.content-card {
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 20px;
  background-color: #f1eaea;
  /* 卡片背景色 */
  border: 1px solid #e6e0e0;
  /* 卡片边框 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  /* 卡片阴影 */
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}

.content-card:hover {
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
  /* 悬停时阴影加深 */
  transform: translateY(-2px);
  /* 悬停时轻微上移 */
}

.card-body {
  display: flex;
  width: 100%;
  padding: 20px;
  gap: 20px;
  cursor: pointer;
}

.cover-image {
  width: 150px;
  height: 100px;
  object-fit: cover;
  border-radius: 5px;
}

.card-content {
  flex: 1;
  padding-right: 20px;
}

.card-content h2 {
  font-size: 1.5em;
  margin: 0 0 10px 0;
  color: #333;
}

.card-content p {
  font-size: 1em;
  margin: 0 0 10px 0;
  color: #666;
}

.stats {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: #666;
  margin-top: 10px;
}

.stats span {
  display: flex;
  align-items: center;
}

.loading {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.error {
  text-align: center;
  margin-top: 20px;
  color: red;
}

.no-more {
  text-align: center;
  margin-top: 20px;
  color: #999;
}

/* 关键词高亮样式 */
::v-deep(.highlight) {
  color: red !important;
  font-weight: bold;
}
</style>