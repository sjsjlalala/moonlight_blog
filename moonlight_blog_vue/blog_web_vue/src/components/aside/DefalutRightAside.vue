<template>
    <div class="right-aside">
      <!-- 热门博客 -->
      <el-card class="section-card hot-blogs">
        <template #header>
          <div class="card-header">
            <el-icon><Fire /></el-icon>
            <span>热门博客 TOP5</span>
          </div>
        </template>
        <div 
          v-for="blog in hotBlogs"
          :key="blog.id"
          class="blog-item"
          @click="viewBlog(blog.id)"
        >
          <div class="blog-title">{{ blog.title }}</div>
          <div class="blog-meta">
            <span class="author">{{ blog.author }}</span>
            <span class="views">
              <el-icon><View /></el-icon>
              {{ blog.views }}
            </span>
          </div>
        </div>
      </el-card>
  
      <!-- 推荐专题 -->
      <el-card class="section-card topics">
        <template #header>
          <div class="card-header">
            <el-icon><Collection /></el-icon>
            <span>推荐专题</span>
          </div>
        </template>
        <el-tag
          v-for="topic in recommendTopics"
          :key="topic.id"
          class="topic-tag"
          effect="plain"
          @click="viewTopic(topic.id)"
        >
          {{ topic.name }}
          <span class="count">({{ topic.count }})</span>
        </el-tag>
      </el-card>
  
      <!-- 友情链接 -->
      <el-card class="section-card friend-links">
        <template #header>
          <div class="card-header">
            <el-icon><Link /></el-icon>
            <span>友情链接</span>
          </div>
        </template>
        <div 
          v-for="link in friendLinks"
          :key="link.id"
          class="link-item"
        >
          <a :href="link.url" target="_blank">{{ link.name }}</a>
        </div>
      </el-card>
  
      <!-- 版权信息 -->
      <div class="copyright">
        © 2024 TechBlog. All rights reserved.<br>
        备案号：京ICP备12345678号
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  // 模拟数据
  const hotBlogs = ref([
    { id: 1, title: 'Vue3 性能优化深度解析', author: '前端达人', views: 2568 },
    { id: 2, title: '微服务架构设计指南', author: '架构师联盟', views: 1893 },
    { id: 3, title: 'Python 数据分析实战', author: '数据科学家', views: 1567 }
  ])
  
  const recommendTopics = ref([
    { id: 1, name: '前端框架', count: 42 },
    { id: 2, name: '云原生', count: 35 },
    { id: 3, name: '人工智能', count: 28 }
  ])
  
  const friendLinks = ref([
    { id: 1, name: '技术社区', url: 'https://example.com' },
    { id: 2, name: '开发者文档', url: 'https://docs.example.com' }
  ])
  
  // 方法
  const viewBlog = (id) => {
    console.log('查看博客:', id)
  }
  
  const viewTopic = (id) => {
    console.log('查看专题:', id)
  }
  </script>
  
  <style scoped>
  .right-aside {
    width: 320px;
    padding: 16px;
  }
  
  .section-card {
    margin-bottom: 20px;
    transition: transform 0.2s;
  
    &:hover {
      transform: translateY(-2px);
    }
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 500;
  }
  
  /* 热门博客样式 */
  .hot-blogs .blog-item {
    padding: 12px 0;
    border-bottom: 1px solid #eee;
    cursor: pointer;
  
    &:last-child {
      border: none;
    }
  
    &:hover {
      background: #f8f9fa;
    }
  }
  
  .blog-title {
    font-size: 14px;
    line-height: 1.4;
    margin-bottom: 4px;
  }
  
  .blog-meta {
    display: flex;
    justify-content: space-between;
    color: #909399;
    font-size: 12px;
  }
  
  /* 专题标签样式 */
  .topic-tag {
    margin: 4px;
    padding: 8px 12px;
    transition: all 0.3s;
  
    &:hover {
      transform: scale(1.05);
    }
  }
  
  .count {
    color: #409eff;
    margin-left: 4px;
  }
  
  /* 友情链接样式 */
  .friend-links .link-item {
    padding: 8px 0;
    
    a {
      color: #606266;
      text-decoration: none;
      
      &:hover {
        color: #409eff;
        text-decoration: underline;
      }
    }
  }
  
  /* 版权信息 */
  .copyright {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #eee;
    color: #909399;
    font-size: 12px;
    line-height: 1.6;
    text-align: center;
  }
  </style>