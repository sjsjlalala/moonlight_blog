<template>
    <div class="tags-page">
        <div class="tags-container">
            <!-- 左侧标签导航栏 -->
            <div class="tags-sidebar">
                <h2>标签</h2>
                <div class="tags-list">
                    <!-- 渲染父标签及其子标签 -->
                    <div v-for="(tag, index) in tags" :key="tag.value" class="tag-item">
                        <!-- 父标签 -->
                        <span @click="fetchBlogsByTag(tag.value)"
                            :style="{ backgroundColor: tagBackgroundColors[index % tagBackgroundColors.length] }"
                            class="tag">
                            # {{ tag.label }}
                        </span>

                        <!-- 渲染子标签 -->
                        <div v-if="tag.children && tag.children.length > 0" class="child-tags">
                            <div v-for="(child, childIndex) in tag.children" :key="child.value">
                                <span @click="fetchBlogsByTag(child.value)"
                                    :style="{ backgroundColor: tagBackgroundColors[index % tagBackgroundColors.length] }"
                                    class="tag">
                                    # {{ child.label }}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 右侧博客列表 -->
            <div class="blogs-list">
                <div v-if="blogs.length > 0">
                    <BlogCard v-for="blog in blogs" :key="blog.blogVO?.uid || blog.userVO?.uid"
                        :blog="blog.blogVO || {}" :author="blog.userVO || {}" :tags="blog.tags || []" />
                </div>
                <div v-else>
                    <p>No blogs found for this tag.</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { tagBackgroundColors } from '../../static/tagConfig'
import { fetchTagsOptionsApi } from '../api/tagApi'
import { fetchBlogListByTagApi } from '../api/blogApi'
import BlogCard from '../components/blog/BlogCard.vue'
// 定义标签数据（最多两层）
const tags = ref([]);

// 响应式变量：当前选中的标签及其博客
const currentTag = ref("");
const blogs = ref([]);
const authors = ref([]);


// 根据标签名称获取博客
const fetchBlogsByTag = async (tagName) => {
    currentTag.value = tagName;
    console.log("当前标签：", tagName);
    // 获取博客数据
    const response = await fetchBlogListByTagApi(tagName);
    if (response.code === 200) {
        blogs.value = response.data;
        console.log("当前标签下的博客：", blogs.value);
    } else {
        console.error("获取博客数据失败：", response.message);
    }

};


// 获取所有标签
const fetchAllTags = async () => {
    const response = await fetchTagsOptionsApi();
    if (response.code === 200) {
        tags.value = response.data;
        if (tags.value.length > 0 && tags.value[0].children) {
            fetchBlogsByTag(tags.value[0].value);
        }
    }

}
// 在页面加载时默认显示第一个标签的博客
onMounted(() => {
    fetchAllTags();
});
</script>

<style scoped>
.tags-page {
  display: flex;
  justify-content: center;
  padding: 20px;
  background-color: #f4f4f4;
  min-height: 100vh;
  box-sizing: border-box;
}

.tags-container {
  display: grid;
  grid-template-columns: minmax(250px, 300px) minmax(600px, 1fr);
  gap: 24px;
  max-width: 1280px;
  width: 100%;
}

/* 左侧标签栏 */
.tags-sidebar {
  position: sticky;
  top: 100px;
  height: fit-content;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

/* 标签项自适应 */
.tag-item {
  display: grid;
  gap: 8px;
  margin-bottom: 12px;
}

.tag {
  display: flex;
  justify-content: center;
  padding: 10px 16px;
  border-radius: 24px;
  font-size: 14px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  box-sizing: border-box;
  min-height: 40px;
}

/* 子标签布局优化 */
.child-tags {
  display: grid;
  gap: 6px;
  margin-left: 12px;
  padding-left: 12px;
  border-left: 2px solid rgba(255, 255, 255, 0.3);
}

/* 右侧博客列表 */
.blogs-list {
  display: grid;
  gap: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  min-width: 0; /* 防止内容溢出 */
}

/* 响应式布局 */
@media (max-width: 1024px) {
  .tags-container {
    grid-template-columns: 1fr;
  }
  
  .tags-sidebar {
    position: static;
    width: 100%;
    max-width: 600px;
    margin: 0 auto;
  }
  
  .blogs-list {
    width: 100%;
    max-width: 800px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .tags-page {
    padding: 12px;
  }
  
  .tag {
    font-size: 13px;
    padding: 8px 12px;
    min-height: 36px;
  }
  
  .child-tags {
    margin-left: 8px;
    padding-left: 8px;
  }
}
</style>
