<template>
    <div class="following-list-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-wrapper">
            <el-spin :size="40" />
            <p class="loading-text">正在加载关注列表...</p>
        </div>

        <!-- 错误提示 -->
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" show-icon class="error-alert" />

        <!-- 关注列表 -->
        <div v-if="!loading && !errorMessage">
            <div v-for="user in followingList" :key="user.uid" class="user-card">
                <div class="user-info">
                    <el-avatar :src="user.avatarUrl" :size="48" class="user-avatar" />
                    <div class="user-details">
                        <div class="user-main">
                            <h3 class="username">{{ user.username }}</h3>
                            <p class="bio">{{ user.remarks || "该用户暂时没有简介" }}</p>
                        </div>
                        <div class="user-stats">
                            <span class="stat-item">
                                <el-icon>
                                    <Notebook />
                                </el-icon>
                                {{ user.blogCount }} 篇博客
                            </span>
                            <span class="stat-item">
                                <el-icon>
                                    <Star />
                                </el-icon>
                                {{ user.likes }} 获赞
                            </span>
                        </div>
                    </div>
                </div>

                <div class="user-actions">
                    <el-button type="danger" plain size="small" @click="handleUnfollow(user.uid)">
                        <el-icon>
                            <Close />
                        </el-icon>
                        取消关注
                    </el-button>
                    <el-button type="primary" plain size="small" @click="viewProfile(user.uid)">
                        <el-icon>
                            <User />
                        </el-icon>
                        访问主页
                    </el-button>
                </div>
            </div>

            <!-- 空状态 -->
            <div v-if="followingList.length === 0" class="empty-state">
                <el-empty description="您还没有关注任何用户">
                    <el-button type="primary" @click="goToRecommend">
                        去发现用户
                    </el-button>
                </el-empty>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
    Close,
    User,
    Star,
    Notebook
} from '@element-plus/icons-vue'
import {fetchFollowListApi, getFollowStatusApi} from '@/api/followApi'
// 用户信息
const user = JSON.parse(localStorage.getItem("user"));
const router = useRouter()

// 响应式数据
const followingList = ref([])
const loading = ref(true)
const errorMessage = ref('')

// 获取关注列表
const fetchFollowingList = async () => {
    try {
        loading.value = true
        const res = await fetchFollowListApi({followerId: user.uid})

        if (res.code === 200) {
            followingList.value = res.data
        } else {
            errorMessage.value = res.message || '获取关注列表失败'
        }
    } catch (err) {
        errorMessage.value = '网络连接异常，请稍后重试'
    } finally {
        loading.value = false
    }
}

// 取消关注
const handleUnfollow = async (userId) => {
  getFollowStatusApi({
        followerId: currentUser.uid,
        followedId: userId,
        isFollowed: false
    }).then((res) => {
        if (res.code === 200) {
           ElMessage.success("取消关注成功");
        } else {
            ElMessage.error("获取关注状态失败");
        }
    });
}

// 路由跳转
const viewProfile = (userId) => {
  router.push({ name: "AuthorDetail", params: { uid: userId } });
}

const goToRecommend = () => {
    router.push('/recommend/users')
}

// 生命周期钩子
onMounted(() => {
    fetchFollowingList()
})
</script>

<style scoped>
.following-list-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px 24px;
}

.user-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  border: 1px solid #ebeef5;
  position: relative; /* 新增定位上下文 */
  transition: transform 0.2s, box-shadow 0.2s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    border-color: #dcdfe6;
  }
}

.user-info {
  display: flex;
  gap: 16px;
  padding-right: 140px; /* 给操作按钮留出空间 */
}

.user-card {
  position: relative;
  margin-bottom: 24px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  
  &::after {
    content: "";
    position: absolute;
    bottom: -12px;
    left: 50%;
    transform: translateX(-50%);
    width: 90%;
    height: 4px;
    background: linear-gradient(
      90deg,
      transparent 0%,
      #409eff 30%,
      #409eff 70%,
      transparent 100%
    );
    opacity: 0.2;
  }

  &:last-child::after {
    display: none;
  }

  &:hover::after {
    opacity: 0.4;
    background: linear-gradient(
      90deg,
      transparent 0%,
      #67c23a 30%,
      #67c23a 70%,
      transparent 100%
    );
  }
}

.user-actions {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-end;
  
  .el-button {
    width: 100px;
    justify-content: center;
    border-radius: 6px;
    padding: 8px 12px;
    
    /* 新增按钮交互效果 */
    &:hover {
      transform: scale(1.05);
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    }
    
    /* 调整图标间距 */
    .el-icon {
      margin-right: 6px;
    }
  }
}

.username {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
  margin-bottom: 6px;
}

.bio {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
  max-width: 480px;
}

.user-stats {
    margin-left: 100px;
  margin-top: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  padding: 8px 12px;
  display: inline-flex;
  gap: 24px;
  
  .stat-item {
    color: #909399;
    font-size: 12px;
    display: flex;
    align-items: center;
    
    .el-icon {
      color: #409eff;
      font-size: 14px;
      margin-right: 6px;
    }
  }
}

/* 新增加载动画 */
.loading-wrapper {
  .el-spin {
    animation: spin-fade 1.2s ease-in-out infinite;
  }
}

@keyframes spin-fade {
  0% { opacity: 0.6; transform: rotate(0deg); }
  50% { opacity: 1; }
  100% { opacity: 0.6; transform: rotate(360deg); }
}
.user-details {
    display: flex;
    flex-direction: row;
}
</style>