<template>
    <div class="author-info">
        <!-- 作者头像和用户名 -->
        <div class="author-header">
            <el-avatar :src="author.avatarUrl" :size="100" class="author-avatar" />
            <h3 class="author-name">{{ author.username }}</h3>
            <p class="author-bio">{{ author.remarks || "这个人很懒，什么都没有写～" }}</p>
        </div>



        <!-- 统计信息 -->
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
            <div class="author-actions-other">
                
                <el-button type="text" @click="viewProfile">
                    <el-icon>
                        <User />
                    </el-icon>
                    <span>查看主页</span>
                </el-button>
            </div>
        </div>

        <!-- 社交链接 -->
        <div v-if="author.socialLinks && author.socialLinks.length > 0" class="author-social">
            <a v-for="link in author.socialLinks" :key="link.name" :href="link.url" target="_blank" class="social-link">
                <el-icon :size="20">
                    <component :is="link.icon" />
                </el-icon>
            </a>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, inject, onMounted,watch } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Message, User } from "@element-plus/icons-vue";
import { followUserApi, getFollowStatusApi } from '../../api/followApi'
// 组件间传参
const author = inject("author");

const router = useRouter();

// 当前登录用户
const currentUser = JSON.parse(localStorage.getItem("user")) || {};

// 是否是自己
const isCurrentUser = computed(() => currentUser.uid === author.value.uid);

// 是否已关注
const isFollowing = ref(false);

// 切换关注状态
const toggleFollow = () => {
    isFollowing.value = !isFollowing.value;
    followUserApi({
        followerId: currentUser.uid,
        followedId: author.value.uid,
        isFollowed: isFollowing.value
    }).then((res) => {
        if (res.code === 200) {
           
            ElMessage.success(isFollowing.value ? "关注成功" : "取消关注");
        } else {
            isFollowing.value = !isFollowing.value;
            ElMessage.error("操作失败");
        }
    });
};
// 获取关注状态
const getFollowStatus = () => {
    getFollowStatusApi({
        followerId: currentUser.uid,
        followedId: author.value.uid,
        isFollowed: isFollowing.value
    }).then((res) => {
        if (res.code === 200) {
            isFollowing.value = res.data.isFollowed;
        } else {
            ElMessage.error("获取关注状态失败");
        }
    });
}


// 查看作者主页
const viewProfile = () => {
    router.push({ name: "AuthorDetail", params: { uid: author.value.uid } });
};
// 监听 author.value.uid 的变化
watch(
    () => author.value.uid,
    (newUid) => {
        if (newUid && !isCurrentUser.value) {
            getFollowStatus();
        }
    }
);
onMounted(() => {
    
})
</script>

<style scoped>
.author-info {
    position: fixed;
    /* 固定位置 */
    margin-top: 80px;
    padding: 20px;
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    width: 280px;
    text-align: center;
    border: 3px solid #c9c8c8;
    /* 添加黑色边框 */
}

.author-header {
    margin-bottom: 16px;
}

.author-avatar {
    margin-bottom: 12px;
    border: 3px solid #ffffff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.author-name {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin: 0 0 8px 0;
}

.author-bio {
    font-size: 14px;
    color: #666;
    margin: 0;
}

.author-tags {
    margin: 16px 0;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
}

.tag {
    font-size: 12px;
}

.author-stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    margin: 16px 0;
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8px;
    background: #f8f9fa;
    border-radius: 8px;
}

.stat-value {
    font-size: 16px;
    font-weight: bold;
    color: #333;
}

.stat-label {
    font-size: 12px;
    color: #999;
}

.author-actions {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin: 16px 0;
}

.author-actions .el-button {
    width: 100%;
}

.author-social {
    display: flex;
    justify-content: center;
    gap: 12px;
    margin-top: 12px;
}

.social-link {
    color: #666;
    transition: color 0.3s ease;
}

.social-link:hover {
    color: #409eff;
}

.author-actions-other {
    display: flex;
    justify-content: space-between;
    gap: 8px;
}
</style>