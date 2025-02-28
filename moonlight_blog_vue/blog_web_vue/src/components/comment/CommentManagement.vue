<template>
    <div class="my-comments">
        <h2 class="title">📜 我的评论</h2>

        <!-- 搜索框 -->
        <el-input v-model="searchKeyword" placeholder="🔍 搜索评论..." prefix-icon="el-icon-search" clearable
            @input="fetchComments" class="search-box" />

        <!-- 博客评论列表 -->
        <div v-if="comments.length > 0" class="comment-list">
            
                <!-- 博客评论列表 -->
                <div v-if="comments.length > 0" class="comment-list">
                    <el-card v-for="blog in groupedComments" :key="blog.blogUid" class="blog-card">
                        <!-- 博客信息 -->
                        <template v-if="blog.blogVO">
                            <div>
                                <BlogCard :key="blog.blogVO.blogVO.uid" :blog="blog.blogVO.blogVO"
                                    :author="blog.blogVO.userVO || {}" :tags="blog.blogVO.tags || []" />
                            </div>

                        </template>
                        <template v-else>
                            <div class="blog-deleted">
                                <div class="deleted-message">
                                    <el-icon :size="20" class="icon">
                                        <Warning />
                                    </el-icon>
                                    <span>该博客已被删除</span>
                                </div>
                            </div>
                        </template>

                        <!-- 评论列表 -->
                        <div class="comments-section">
                            <div v-for="comment in blog.comments" :key="comment.uid" class="comment-item">
                                <div class="user-info">
                                    <el-avatar :src="user.avatarUrl" class="avatar" />
                                    <span class="username">{{ user.username }}</span>
                                </div>
                                <p class="comment-text">{{ comment.content }}</p>
                                <div class="comment-footer">
                                    <span class="time">{{ formatTime(comment.createTime) }}</span>
                                    <div class="actions">
                                        <el-button type="text" @click="editComment(comment)">编辑</el-button>
                                        <el-button type="text" @click="deleteComment(comment.uid)">删除</el-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </el-card>
                </div>
            
        </div>

        <!-- 无评论提示 -->
        <el-empty v-else description="暂无评论" class="empty-placeholder" />

        <!-- 分页控件 -->
        <el-pagination v-model:current-page="page" :page-size="pageSize" layout="prev, pager, next" :total="total"
            @current-change="fetchComments" class="pagination" />
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { fetchUserCommentsApi, deleteCommentApi, updateCommentApi } from "../../api/comentApi";
import { Warning } from "@element-plus/icons-vue";
import BlogCard from "../blog/BlogCard.vue";
const router = useRouter();
const comments = ref([]); // 存储评论数据
const loading = ref(false);
const page = ref(1);
const pageSize = 10;
const total = ref(0);
const searchKeyword = ref("");

// 用户信息
const user = JSON.parse(localStorage.getItem("user"));

// 获取我的评论
const fetchComments = async () => {
    loading.value = true;
    try {
        const data = {
            currentPage: page.value,
            pageSize: pageSize,
            keyword: searchKeyword.value,
        };
        const response = await fetchUserCommentsApi(data);
        comments.value = response.data;
        total.value = response.data.total;
    } catch (error) {
        ElMessage.error("获取评论失败");
    } finally {
        loading.value = false;
    }
};

// 格式化时间
const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleString();
};

// 跳转到博客详情页
const goToBlog = (blogId) => {
    router.push({ name: "BlogDetail", params: { id: blogId } });
};

// 编辑评论
const editComment = (comment) => {
    ElMessageBox.prompt("编辑评论", "编辑", {
        inputValue: comment.content,
        confirmButtonText: "保存",
        cancelButtonText: "取消",
    })
        .then(({ value }) => {
            if (value) {
                comment.content = value;
                updateCommentApi({
                    uid: comment.uid,
                    content: comment.content,
                }).then((res) => {
                    if (res.code === 200) {
                        ElMessage.success("评论编辑成功");
                    } else {
                        ElMessage.error("评论编辑失败");
                    }
                });


            }
        })
        .catch(() => {
            ElMessage.info("取消编辑");
        });
};

// 删除评论
const deleteComment = async (commentId) => {
    try {
        await ElMessageBox.confirm("确定删除该评论吗？", "提示", {
            confirmButtonText: "删除",
            cancelButtonText: "取消",
            type: "warning",
        });
        deleteCommentApi({ uid: commentId })
            .then((res) => {
                if (res.code === 200) {
                    ElMessage.success("评论已删除");
                    fetchComments();
                } else {
                    ElMessage.error("删除失败");
                }
            });


    } catch (error) {
        ElMessage.info("取消删除");
    }
};

// 按博客分组评论
const groupedComments = computed(() => {
    const grouped = {};
    comments.value.forEach((comment) => {
        const blogUid = comment.blogUid;
        if (!grouped[blogUid]) {
            grouped[blogUid] = {
                blogUid: blogUid,
                blogVO: comment.blogVO,
                comments: [],
            };
        }
        grouped[blogUid].comments.push(comment);
    });
    return Object.values(grouped);
});

onMounted(fetchComments);
</script>

<style scoped>
.blog-container {
  max-width: 90%; /* 设置容器最大宽度为父容器的 90%，可以根据需求调整 */
  margin: 0 auto; /* 让容器居中 */
  padding: 10px; /* 添加内边距以便不与页面边缘贴合 */
}

.blog-container .blog-card {
  height: 100px;
  width: 100%; /* 让 BlogCard 填满容器宽度 */
  box-sizing: border-box; /* 确保内边距不影响宽度 */
}
.my-comments {
    padding: 20px;
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    max-width: 800px;
    margin: auto;
}

.title {
    text-align: center;
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
}

.search-box {
    width: 100%;
    margin-bottom: 20px;
}

.comment-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.blog-card {
    border-radius: 12px;
    overflow: hidden;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.blog-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.blog-info {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 20px;
    background: #f8f9fa;
    cursor: pointer;
}

.blog-cover {
    width: 120px;
    height: 80px;
    object-fit: cover;
    border-radius: 8px;
}

.blog-details {
    flex: 1;
}

.blog-title {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin: 0 0 10px 0;
}

.blog-intro {
    font-size: 14px;
    color: #666;
    margin: 0 0 10px 0;
}

.blog-stats {
    display: flex;
    gap: 10px;
    font-size: 12px;
    color: #999;
}

.comments-section {
    padding: 20px;
    background: #ffffff;
}

.comment-item {
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #eee;
}

.comment-item:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;
}

.avatar {
    width: 40px;
    height: 40px;
}

.username {
    font-size: 14px;
    font-weight: bold;
    color: #333;
}

.comment-text {
    font-size: 14px;
    color: #555;
    line-height: 1.6;
    margin: 0 0 10px 0;
}

.comment-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.blog-deleted {
    padding: 16px;
    background-color: #f8f9fa;
    border-radius: 8px;
    margin-bottom: 16px;
    border: 1px solid #ebeef5;
}

.deleted-message {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #606266;
    font-size: 14px;
}

.deleted-message .icon {
    color: #e6a23c;
    /* 使用警告色 */
}

.time {
    font-size: 12px;
    color: #999;
}

.actions {
    display: flex;
    gap: 10px;
}

.empty-placeholder {
    margin-top: 40px;
}

.pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}
</style>