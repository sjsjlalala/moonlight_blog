<template>
    <div class="content-manager">
        <div class="filter-section">
            <el-input v-model="searchQuery" placeholder="搜索博客或专题" clearable class="search-box" />
            <el-date-picker v-model="selectedDate" type="daterange" range-separator="至" start-placeholder="开始日期"
                end-placeholder="结束日期" class="date-picker" />
            <el-button type="primary" @click="filterContent">筛选</el-button>
        </div>
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <!-- 博客 -->
            <el-tab-pane label="博客" name="blogs">
                <div class="blog-list" v-if ="filteredBlogs.length > 0">
                    <div v-for="blog in filteredBlogs" :key="blog.blogVO.uid" class="blog-container">
                        <div class="card-header">
                            <el-dropdown class="manage-dropdown">
                                <el-button type="text" class="manage-button">
                                    <el-icon :size="20">
                                        <More />
                                    </el-icon>
                                </el-button>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item @click="editContent(blog.blogVO)">编辑</el-dropdown-item>
                                        <el-dropdown-item @click="deleteContent(blog.blogVO)" divided>删除</el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                        <BlogCard :key="blog.blogVO?.uid" :blog="blog.blogVO || {}"
                            :author="blog.userVO || {}" :tags="blog.tags || []" />
                    </div>
                </div>
                <!-- 无评论提示 -->
                <el-empty v-else description="还未发表过博客" class="empty-placeholder" />
            </el-tab-pane>

            <!-- 专题 -->
            <el-tab-pane label="专题" name="topics">
                
                    <div v-for="topic in filteredTopics" :key="topic.uid" :span="24" v-if = "filteredTopics.length > 0">
                        <el-card class="content-card topic-card" @click="goToDetail(topic.uid)">
                            <div class="card-header">
                                <el-dropdown>
                                    <el-button type="text" class="manage-button">
                                        <el-icon :size="20">
                                            <More />
                                        </el-icon>
                                    </el-button>
                                    <template #dropdown>
                                        <el-dropdown-menu>
                                            <el-dropdown-item @click="editSubject(topic.uid)">编辑</el-dropdown-item>
                                            <el-dropdown-item @click="deleteSubject(topic.uid)"
                                                divided>删除</el-dropdown-item>
                                        </el-dropdown-menu>
                                    </template>
                                </el-dropdown>
                            </div>
                            <div class="card-body">
                                <img v-if="topic.fileUid" :src="topic.fileUrl" class="cover-image" />
                                <div class="card-content">
                                    <h3>{{ topic.subjectName }}</h3>
                                    <p>{{ topic.summary }}</p>
                                    <div class="stats">
                                        <span>🕒 {{ topic.createTime }}</span>
                                    </div>
                                </div>
                            </div>
                        </el-card>
                    </div>
                    <!-- 无评论提示 -->
                <el-empty v-else description="还未发表过专题" class="empty-placeholder" />
                
            </el-tab-pane>

            <!-- 我的分组 -->
            <el-tab-pane label="我的分组" name="groups">
                <BlogCategory />
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import BlogCategory from "../category/BlogCategory.vue";
import { fetchBlogListApi, deleteBlogApi } from "../../api/blogApi";
import { fetchSubjectDetailApi } from "../../api/subjectApi";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from 'vue-router';
import BlogCard from "../blog/BlogCard.vue";
import { More } from "@element-plus/icons-vue";

const router = useRouter();
const activeTab = ref("blogs");
const searchQuery = ref("");
const selectedDate = ref(null);

const currentPage = ref(1)
const pageSize = ref(10)

const user = JSON.parse(localStorage.getItem("user"));

const users = ref([])

const blogs = ref([]);

const topics = ref([]);

const groups = ref([
    { id: 1, title: "学习小组", description: "前端学习交流群", coverImage: "group1.jpg", time: "2024-01-30" },
    { id: 2, title: "技术分享", description: "技术交流与分享", coverImage: "group2.jpg", time: "2024-02-05" }
]);

const filterContent = () => {
    console.log("Filtering content by", searchQuery.value, selectedDate.value);
};

const filteredBlogs = computed(() => {
    return blogs.value.filter(blog =>
        blog.blogVO?.title.includes(searchQuery.value) &&
        (!selectedDate.value || (blog.blogVO?.createTime >= selectedDate.value[0] && blog.blogVO?.createTime <= selectedDate.value[1]))
    );
});

const filteredTopics = computed(() => {
    return topics.value.filter(topic =>
        topic.subjectName
            .includes(searchQuery.value) &&
        (!selectedDate.value || (topic.time >= selectedDate.value[0] && topic.time <= selectedDate.value[1]))
    );
});

const filteredGroups = computed(() => {
    return groups.value.filter(group =>
        group.title.includes(searchQuery.value) &&
        (!selectedDate.value || (group.time >= selectedDate.value[0] && group.time <= selectedDate.value[1]))
    );
});
const fetchBlogs = async (pageSize, currentPage, keyword) => {
    // 清空 blogs 和 users
    if (currentPage === 1) {
        blogs.value = [];
        users.value = [];
    }

    try {
        const response = await fetchBlogListApi(currentPage, pageSize, keyword, user.uid);

        if (!response || response.code !== 200) {
            throw new Error(response?.message || '获取博客列表失败');
        }

        const data = response.data || [];

        if (data.length > 0) {
            blogs.value.push(...data);
        }

        console.log('博客列表', blogs.value);
    } catch (err) {
        ElMessage.error(err.message || '获取博客失败');
    }

};
// 获取专题数据
const fetchSubjects = async (pageSize, currentPage, keyword) => {
    const data = {
        currentPage: currentPage.value,
        pageSize: pageSize,
        keyword: searchQuery.value,
        userUid: user.uid
    };
    topics.value = [];
    const response = await fetchSubjectDetailApi(data);
    if (response.code === 200) {

        response.data.map((item) => {
            topics.value.push(item.subject)
        })
        console.log('专题列表:', topics.value);

    } else {
        console.error('获取专题列表失败:', response.message);
        ElMessage.error('获取专题列表失败');
    }
};
// 处理标签页切换事件
const handleTabChange = (tabName) => {
    console.log("切换到标签页:", tabName);
    if (tabName === "blogs") {
        console.log("加载博客数据...");
        fetchBlogs(pageSize.value, currentPage.value, searchQuery.value);
    } else if (tabName === "topics") {
        console.log("加载专题数据...");
        fetchSubjects(pageSize.value, currentPage.value, searchQuery.value);
    } else if (tabName === "groups") {
        console.log("加载分组数据...");
        // 这里可以添加加载分组数据的逻辑
    }
};
const editContent = (blog) => {
    // 跳转到编辑博客页面，传递博客的 id
    console.log("编辑博客:", blog.uid)
    router.push({ name: 'ArticleEditor', params: { id: blog.uid } });
};
const deleteContent = (blog) => {
    const data = new FormData();
    data.append('uid', blog.uid);
    deleteBlogApi(data).then((res) => {
        if (res.code === 200) {
            ElMessage.success("删除成功");
            fetchBlogs(currentPage.value, pageSize.value, keyword.value);
        } else {
            ElMessage.error("删除失败");
        }
    });
};
// 编辑专题
const editSubject = (uid) => {
    router.push({ name: "SubjectDetail", params: { uid: uid }, query: { isEditor: true } });
};
const goToBlogDetail = (uid) => {
    router.push({ name: 'BlogDetail', params: { uid } })
}
// 跳转到专题详情
const goToDetail = (uid) => {
    router.push({ name: 'SubjectDetail', params: { uid } })
}
onMounted(() => {
    fetchBlogs(pageSize.value, currentPage.value, searchQuery.value);
});
</script>

<style scoped>
.content-manager {
    padding: 20px;
    width: 1200px;
}

.filter-section {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
}

.search-box {
    flex: 1;
}

.date-picker {
    width: 300px;
}

.blog-list {
    display: flex;
    flex-direction: column;
    gap: 0px;
}

.blog-container {
    position: relative; /* 相对定位，用于管理按钮的绝对定位 */
    margin-bottom: 20px; /* 博客卡片之间的间距 */
}

.card-header {
    position: absolute; /* 绝对定位 */
    top: 10px; /* 距离顶部 10px */
    right: 10px; /* 距离右侧 10px */
    z-index: 1; /* 确保按钮在卡片上方 */
}

.manage-button {
    padding: 0;
    border: none;
    background: transparent;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
}

.manage-button:hover {
    background-color: rgba(0, 0, 0, 0.05);
    border-radius: 50%;
}

.content-card {
    border-radius: 10px;
    overflow: hidden;
    margin-bottom: 20px;
}
.empty-placeholder {
    margin-top: 40px;
}
.blog-card,
.topic-card,
.group-card {
    display: flex;
    flex-direction: row;
    align-items: center;
    position: relative;
}

.card-body {
    display: flex;
    width: 100%;
    padding: 20px;
    gap: 20px;
}

.card-content {
    flex: 1;
    padding-right: 20px;
}

.cover-image {
    width: 150px;
    height: 100px;
    object-fit: cover;
    border-radius: 5px;
}

.stats {
    display: flex;
    gap: 10px;
    font-size: 12px;
    color: #666;
    margin-top: 10px;
}
</style>