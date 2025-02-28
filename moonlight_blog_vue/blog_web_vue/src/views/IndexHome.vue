<template>
  <el-carousel :interval="4000" type="card" height="300px">
    <el-carousel-item 
      v-for="(item, index) in carouselItems" 
      :key="index"
      @click="handleCarouselClick(item.link)"
    >
      <div class="carousel-content">
        <img :src="item.image" class="carousel-image" />
        <div class="carousel-text">
          <h2 class="title">{{ item.title }}</h2>
          <p class="description">{{ item.description }}</p>
        </div>
      </div>
    </el-carousel-item>
  </el-carousel>
  <BlogList></BlogList>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import BlogList from '@/components/blog/BlogList.vue'
const router = useRouter()

// 轮播数据示例
const carouselItems = ref([
  {
    image: 'https://picsum.photos/800/400?random=1',
    title: '最新技术动态',
    description: '探索前沿技术发展趋势',
    link: '/tech-news'
  },
  {
    image: 'https://picsum.photos/800/400?random=2',
    title: '开发实践指南',
    description: '掌握最佳开发实践技巧',
    link: '/development'
  },
  {
    image: 'https://picsum.photos/800/400?random=3',
    title: '架构设计之道',
    description: '构建高可用系统架构',
    link: '/architecture'
  },
  {
    image: 'https://picsum.photos/800/400?random=4',
    title: '运维知识库',
    description: '自动化运维实践经验分享',
    link: '/operations'
  }
])

// 轮播点击处理
const handleCarouselClick = (link) => {
  if (link) {
    router.push(link)
  }
}
</script>

<style scoped>
.carousel-content {
  position: relative;
  height: 100%;
  width: 100%;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.8);
  transition: transform 0.3s ease;
}

.carousel-text {
  position: absolute;
  bottom: 20%;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  width: 80%;
}

.title {
  font-size: 2.2rem;
  margin-bottom: 1rem;
  font-weight: bold;
}

.description {
  font-size: 1.2rem;
  line-height: 1.5;
}

.el-carousel__item {
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.el-carousel__item:hover .carousel-image {
  transform: scale(1.05);
}
</style>