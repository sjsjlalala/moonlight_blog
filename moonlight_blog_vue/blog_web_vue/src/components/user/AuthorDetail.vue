<template>
    <div class="author-profile">
        <!-- 作者信息头部 -->
        <div class="author-header">
            <el-avatar :src="author.avatarUrl" :size="120" class="author-avatar" />
            <h1 class="author-name">{{ author.username }}</h1>
            <p class="author-bio">{{ author.remarks || "这个人很懒，什么都没有写～" }}</p>
        </div>

        <!-- 作者统计信息 -->
        <div class="author-stats">
            <div class="stat-item">
                <span class="stat-value">{{ author.blogCount || 0 }}</span>
                <span class="stat-label">博客</span>
            </div>
            <div class="stat-item">
                <span class="stat-value">{{ author.commentCount || 0 }}</span>
                <span class="stat-label">评论</span>
            </div>
            <div class="stat-item">
                <span class="stat-value">{{ author.likes || 0 }}</span>
                <span class="stat-label">获赞</span>
            </div>
            <div class="stat-item">
                <span class="stat-value">{{ author.favorites || 0 }}</span>
                <span class="stat-label">收藏</span>
            </div>
            <div class="stat-item">
                <span class="stat-value">{{ author.visitCount || 0 }}</span>
                <span class="stat-label">访问</span>
            </div>
            
        </div>

        <!-- 操作按钮 -->
        <div class="author-actions">
            <el-button v-if="!isCurrentUser" :type="isFollowing ? 'default' : 'primary'" @click="toggleFollow">
                {{ isFollowing ? "已关注" : "关注" }}
            </el-button>
        </div>

        <!-- 使用 el-tabs 实现切换功能 -->
        <el-tabs v-model="activeTab" type="card" class="author-tabs" @tab-click="handleTabChange">

            <el-tab-pane label="所有博客" name="allBlogs">
                <div class="author-blogs">
                    <BlogList :uid="props.uid"></BlogList>
                </div>
            </el-tab-pane>

            <el-tab-pane label="专题" name="subjects">
                <Subject :uid = "props.uid"></Subject>
            </el-tab-pane>

            <el-tab-pane label="收藏" name="collections">
                <CollectionManagement :is-edit="false" :uid = "props.uid"></CollectionManagement>
            </el-tab-pane>

        </el-tabs>
    </div>
</template>


<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Message, Share, Warning} from "@element-plus/icons-vue";
import {fetchUserHomeApi} from "../../api/blogApi";
import BlogList from "../blog/BlogList.vue";
import CollectionManagement from "../collection/CollectionManagement.vue";
import Subject from "../../views/Subject.vue";
import {getFollowStatusApi} from "../../api/followApi";
const author = ref({});
// 作者信息
const props = defineProps({
    uid: {
        type: String,
        required: true,
    },
});

const router = useRouter();
const activeTab = ref("allBlogs");

// 当前登录用户
const currentUser = JSON.parse(localStorage.getItem("user")) || {};
const isCurrentUser = computed(() => currentUser.uid === author.uid);
const isFollowing = ref(false);

// 切换关注状态
const toggleFollow = () => {
    isFollowing.value = !isFollowing.value;
    ElMessage.success(isFollowing.value ? "关注成功" : "取消关注");
};

// 发送私信
const sendMessage = () => {
    ElMessage.info("私信功能开发中～");
};

// 分享主页
const shareProfile = () => {
    ElMessage.info("分享功能开发中～");
};

// 举报主页
const reportProfile = () => {
    ElMessage.info("举报功能开发中～");
};

// 查看博客详情
const viewBlog = (blogId) => {
    router.push({ name: "BlogDetail", params: { id: blogId } });
};

// 查看专题详情
const viewSubject = (subjectId) => {
    router.push({ name: "SubjectDetail", params: { id: subjectId } });
};

// 格式化时间
const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleDateString();
};


// 获取用户信息
const fetchUserInfo = async () => {
    const response = await fetchUserHomeApi(props.uid);
    if (response.code === 200) {
        author.value = response.data;
        isFollowing.value = response.data.isFollowing;
        console.log("author", author.value)
    } else {
        ElMessage.error(response.message);
    }

};
// 获取关注状态
const getFollowStatus = () => {
    getFollowStatusApi({
        followerId: currentUser.uid,
        followedId: props.uid,
    }).then((res) => {
        if (res.code === 200) {
            isFollowing.value = res.data.isFollowed;
        } else {
            ElMessage.error("获取关注状态失败");
        }
    });
}

onMounted(async () => {
   await fetchUserInfo()
   getFollowStatus()
});
</script>

<style scoped>
/* 作者信息 */
.author-profile {
    max-width: 900px;
    margin: 0 auto;
    padding: 24px;
    background: #fbf8f8;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
   
}

/* 头像、名称、简介 */
.author-header {
    text-align: center;
    margin-bottom: 32px;
}

.author-avatar {
    border: 5px solid #ffffff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.author-name {
    font-size: 32px;
    font-weight: bold;
    color: #333;
    margin: 16px 0;
}

.author-bio {
    font-size: 16px;
    color: #777;
}

/* 操作按钮 */
.author-actions {
    display: flex;
    justify-content: center;
    gap: 16px;
    margin-top: 16px;
}

/* 统计信息 */
.author-stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    margin-top: 32px;
}

.stat-item {
    text-align: center;
    padding: 20px;
    background: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #333;
}

.stat-label {
    font-size: 14px;
    color: #888;
}

/* 标签 */
.author-tags {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 24px;
}

.tag {
    font-size: 14px;
}

/* Tab 切换 */
.author-tabs {
    margin-top: 32px;
}

.el-tabs__header {
    margin-bottom: 16px;
}

.el-tab-pane {
    padding: 20px;
}

/* 卡片样式 */
.el-card {
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.el-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.subject-card, .blog-card {
    cursor: pointer;
    background: #f9f9f9;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.subject-title, .blog-title {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin-bottom: 8px;
}

.subject-summary, .blog-summary {
    font-size: 14px;
    color: #777;
    margin-bottom: 16px;
}

.subject-footer, .blog-footer {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #999;
}

.subject-footer span, .blog-footer span {
    color: #777;
}

/* Tab 的选项卡样式 */
.el-tabs__item {
    font-size: 16px;
    font-weight: 500;
    color: #666;
    transition: color 0.3s ease, transform 0.3s ease;
}

.el-tabs__item.is-active {
    color: #409eff;
    transform: scale(1.1);
}

.el-tabs__item:hover {
    color: #409eff;
    cursor: pointer;
}

/* Tab 选项卡切换过渡效果 */
.el-tabs__content {
    transition: opacity 0.3s ease;
}

</style>
