<template>
  <el-col :span="24">
    <el-card class="content-card blog-card">
      <div class="card-body" @click="goToBlogDetail(blog.uid)">
        <!-- 封面图片容器 -->
        <div class="cover-container">
          <img v-if="blog.coverImageUid" :src="blog.coverImageUrl" class="cover-image" />
        </div>
        <div class="card-content">
          <div class="content-top">
            <h2 class="blog-title">{{ blog.title }}</h2>
            <p class="blog-intro">{{ blog.introduction }}</p>
          </div>
          <div class="content-bottom">
            <div class="tags">
              <el-tag v-for="tag in tags" :key="tag.uid" :type="getTagType(tag.tagName)">
                {{ tag.tagName }}
              </el-tag>
            </div>
            <div class="stats">
              <span>👍 {{ blog.likes }}</span>
              <span>👁️ {{ blog.clicks }}</span>
              <span>⭐ {{ blog.favorites }}</span>
              <span>🕒 {{ formatTime(blog.createTime) }}</span>
              <span>作者：{{ author.username }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </el-col>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

const props = defineProps({
  blog: {
    type: Object,
    required: true,
  },
  author: {
    type: Object,
    required: true,
  },
  tags: {
    type: Array,
    default: () => [],
  },
});

// 跳转到博客详情页
const goToBlogDetail = (blogId) => {
  router.push({ name: "BlogDetail", params: { uid: blogId } });
};

// 格式化时间
const formatTime = (timestamp) => {
  
  const date = new Date(timestamp);
  return date.toLocaleString();
};

// 获取标签颜色
const getTagType = (tagName) => {
  const tagColors = {
    "Python": "success",
    "JavaScript": "warning",
    "Java": "danger",
    "C++": "primary",
    "Vue": "info",
  };
  return tagColors[tagName] || "default";
};
</script>

<style scoped>
.content-card {
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 10px;
}

.blog-card {
  display: flex;
  flex-direction: row;
  align-items: stretch; /* 确保高度一致 */
}

.card-body {
  display: flex;
  width: 100%;
  padding: 20px;
  gap: 20px;
}

.cover-container {
  flex-shrink: 0; /* 防止图片被压缩 */
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  overflow: hidden; /* 防止图片溢出 */
  border-radius: 5px;
  background-color: #f5f5f5; /* 背景色，用于占位 */
}

.cover-image {
  width: 150px;
  height: 100px;
  object-fit: cover; /* 保持宽高比 */
  border-radius: 5px;
}

.card-content {
  flex: 1;
  
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* 内容均匀分布 */
  overflow: hidden; /* 防止内容溢出 */
}

.content-top {
  flex: 1;
  overflow: hidden; /* 防止内容溢出 */
}

.blog-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 5px 0;
  white-space: nowrap; /* 单行显示 */
  overflow: hidden;
  text-overflow: ellipsis; /* 溢出显示省略号 */
}

.blog-intro {
  font-size: 14px;
  color: #666;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 限制显示两行 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.content-bottom {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap; /* 标签换行 */
}

.stats {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: #666;
}
</style>