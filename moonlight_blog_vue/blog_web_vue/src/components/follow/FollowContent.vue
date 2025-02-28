<template>
  <div class="followed-authors">
    <el-spin v-if="loading" class="loading-spinner" />
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" class="error-message" />

    <div v-if="!loading && !errorMessage">
      <!-- 改为直接遍历所有博客 -->
      <div 
        v-for="(item, index) in blogList"
        :key="index"
        class="author-section"
      >
        <!-- 每个博客独立的作者信息 -->
        <div class="author-info">
          <el-avatar :src="item.author.avatarUrl" :size="50" class="author-avatar" />
          <div class="author-details">
            <div class="author-header">
              <h3 class="author-name">{{ item.author.username }}</h3>
              <span class="publish-time">{{ formatTimeAgo(item.blog.createTime) }}</span>
            </div>
           
          </div>
          <div class="author-actions">
            <el-button 
              type="text" 
              @click="viewProfile(item.author.uid)"
            >
              <el-icon><User /></el-icon>
              <span>查看主页</span>
            </el-button>
            <el-button 
              type="text" 
              @click="unfollowAuthor(item.author.uid)"
            >
              <el-icon><Close /></el-icon>
              <span>取消关注</span>
            </el-button>
          </div>
        </div>

        <!-- 单个博客卡片 -->
        <BlogCard
          :blog="item.blog"
          :author="item.author"
          :tags="item.tags"
          class="followed-blog-card"
        />
      </div>

      <div v-if="blogList.length === 0" class="no-followed-authors">
        <p>您还没有关注的作者博客。</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { Message, User ,Close} from "@element-plus/icons-vue";
import { fetchFollowBlogsApi,followUserApi } from '@/api/followApi';
import BlogCard from '../blog/BlogCard.vue';

const router = useRouter();
const data = ref([]); // 调整为后端返回的 data 结构
const loading = ref(false);
const errorMessage = ref('');
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'));

const rawData = ref([]); // 原始数据
const blogList = computed(() => { // 扁平化处理后的数据
  return rawData.value.flatMap(author => 
    author.blogs.map(blog => ({
      blog: blog.blogVO,
      author: author.user,
      tags: blog.tags
    }))
  );
});
// 新增时间格式化方法
const formatTimeAgo = (createTime) => {
  if (!createTime) return '';
  
  const now = new Date();
  const blogDate = new Date(createTime);
  const diff = now - blogDate;
  
  const minutes = Math.floor(diff / 60000);
  if (minutes < 0) return '即将发布';
  if (minutes < 1) return '刚刚发布';
  if (minutes < 60) return `${minutes}分钟前`;
  
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}小时前`;
  
  const days = Math.floor(hours / 24);
  return `${days}天前`;
};


const fetchFollowedAuthorsBlogs = async () => {
  loading.value = true;
  try {
    const res = await fetchFollowBlogsApi({ 
      followerId: user.value?.uid 
    });

    if (res.code === 200) {
      rawData.value = res.data; // 存储原始结构
    } else {
      errorMessage.value = res.message || '获取关注列表失败';
    }
  } catch (err) {
    errorMessage.value = '网络错误，请稍后再试';
  } finally {
    loading.value = false;
  }
};

// 取消关注逻辑优化
const unfollowAuthor = async (authorId) => {
  try {
    const res = await followUserApi({
      followerId: user.value?.uid,
      followedId: authorId,
      isFollowed: false
    });

    if (res.code === 200) {
      // 同时更新原始数据和本地状态
      rawData.value = rawData.value.filter(a => a.user.uid !== authorId);
      ElMessage.success(`已取消关注`);
    }
  } catch (err) {
    ElMessage.error('操作失败，请检查网络');
  }
};
// 查看作者主页
const viewProfile = (uid) => {
    router.push({ name: "AuthorDetail", params: { uid: uid } });
};
onMounted(() => {
  if (!user.value?.uid) {
    ElMessage.error('请先登录');
    router.push('/login');
    return;
  }
  fetchFollowedAuthorsBlogs();
});
</script>

<style scoped>
.followed-authors {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.loading-spinner {
  display: flex;
  justify-content: center;
  margin-top: 50px;
}

.error-message {
  margin-bottom: 20px;
}

.author-section {
  margin-bottom: 10px;
  background-color: #ffffff;
  border-radius: 8px;
  padding: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.author-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.author-avatar {
  border: 2px solid #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.author-details {
  flex: 1;
  display: flex;
  flex-direction: row;
}

.author-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.author-bio {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.author-actions {
  display: flex;
  gap: 10px;
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.followed-blog-card {
  margin-bottom: 16px;
}

.no-blogs {
  text-align: center;
  color: #95a5a6;
  margin-top: 16px;
}

.no-followed-authors {
  text-align: center;
  color: #95a5a6;
  margin-top: 20px;
}
</style>