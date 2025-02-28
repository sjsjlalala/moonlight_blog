<template>
    <div class="subject-detail">
        <!-- 专题封面 -->
        <div class="image__preview">
            <el-image v-if = "subject.fileUid" style="width: 100%; height: 600px" :src="subject.fileUrl" :zoom-rate="1.2" :max-scale="7"
                :min-scale="0.2" :preview-src-list="srcList" :initial-index="4" fit="cover" />
        </div>

        <!-- 专题简介 -->
        <div class="subject-description">
            <h1>{{ subject.subjectName }}</h1>
            <p>{{ subject.summary }}</p>
        </div>

        <!-- 博客列表 -->
        <div v-if="!blogs" class="empty-message">
            <div>
                <p>暂无博客内容</p>
            </div>

        </div>
        <div class="blog-list">
            <div v-for="blog in blogs" :key="blog.id" class="blog-item">
                <div class="blog-card" @click="goToBlogDetail(blog.uid)">
                    <img v-if ="blog.coverImageUid" :src="blog.coverImageUrl" alt="Blog Image" class="blog-image" />
                    <div class="blog-info">
                        <h3>{{ blog.title }}</h3>
                        <p>{{ blog.description }}</p>
                        <div class="blog-stats">
                            <span>点击数: {{ blog.clicks }}</span>
                            <span>收藏数: {{ blog.favorites }}</span>
                            <span>点赞数: {{ blog.likes }}</span>
                            <span>时间: {{ formatTime(blog.createTime) }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted,  watch,inject } from 'vue';
import { fetchSubjectDetailApi } from '../../api/subjectApi'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
    uid: {
        type: String,
        required: true
    }
    ,
    isEditor: {
        type: Boolean,
        default: false
    }
})

// 注入父组件提供的 isEditor
const isEditor = inject("isEditor");
const subjectUid = inject("subjectUid");
const isFresh = inject("isFresh");



// 专题数据
const subject = ref({});
const srcList = ref([]);
// 博客列表数据
const blogs = ref([]);
// 获取专题详情
const fetchSubjectDetail = async () => {
    const data = {
        uid: props.uid
    };

    const response = await fetchSubjectDetailApi(data);
    if (response.code === 200) {
        subject.value = response.data[0].subject;
        blogs.value = response.data[0].blogs;
        srcList.value.push(subject.value.fileUid)
        console.log('专题详情:', subject.value)
        console.log(response.data)
    } else {
        console.error('获取专题详情失败:', response.message);
    }
};

// 监听 isFresh 变化
watch(isFresh, (newVal) => {
    if (newVal) {
      fetchSubjectDetail();
      isFresh.value = false;
    }
  
},{ immediate: true });
// 监听 isEditor 的变化并同步给 provide
watch(() => props.isEditor, (newVal) => {
  isEditor.value = newVal;  // 更新 provide 状态
  subjectUid.value = props.uid;
}, { immediate: true });
const goToBlogDetail = (uid) => {
    router.push({ name: 'BlogDetail', params: { uid } })
}
// 格式化时间
const formatTime = (timestamp) => {
  
  const date = new Date(timestamp);
  return date.toLocaleString();
};
onMounted(() => {
    fetchSubjectDetail();
})


</script>

<style scoped>
/* 整体布局 */
.subject-detail {
    padding: 20px;
    width: 1200px;
    margin: 0 auto;
    background-color: #f8f9fa;
}

/* 专题封面 */
.image__preview {
    border-radius: 10px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}





/* 专题简介 */
.subject-description {
    margin-top: 30px;
    text-align: center;
    color: #333;
}

.subject-description h1 {
    font-size: 36px;
    font-weight: bold;
    margin-bottom: 20px;
}

.subject-description p {
    font-size: 18px;
    line-height: 1.6;
    max-width: 800px;
    margin: 0 auto;
}

/* 博客列表 */
.blog-list {
    margin-top: 50px;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 30px;
}

.blog-item {
    display: flex;
    justify-content: center;
}

.blog-card {
    width: 100%;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    transition: transform 0.3s, box-shadow 0.3s;
}

.blog-card:hover {
    transform: translateY(-10px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.blog-image {
    width: 100%;
    height: 180px;
    object-fit: cover;
}

.blog-info {
    padding: 15px;
}

.blog-info h3 {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin-bottom: 15px;
}

.blog-info p {
    font-size: 14px;
    color: #666;
    margin-bottom: 15px;
    line-height: 1.6;
}

.blog-stats {
    font-size: 14px;
    color: #999;
}

.blog-stats span {
    display: inline-block;
    margin-right: 10px;
    margin-bottom: 5px;
    color: #777;
}

.blog-stats span:first-child {
    font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .subject-description h1 {
        font-size: 28px;
    }

    .subject-description p {
        font-size: 16px;
    }

    .blog-list {
        grid-template-columns: 1fr 1fr;
    }
}

@media (max-width: 480px) {
    .subject-description h1 {
        font-size: 24px;
    }

    .subject-description p {
        font-size: 14px;
    }

    .blog-list {
        grid-template-columns: 1fr;
    }
}

/* 空提示样式 */
.empty-message {
    width: 100%;
    text-align: center;
    font-size: 18px;
    display: flex;
    justify-content: center;
    align-items: center;

    color: #777;
    margin-top: 150px;
}
</style>