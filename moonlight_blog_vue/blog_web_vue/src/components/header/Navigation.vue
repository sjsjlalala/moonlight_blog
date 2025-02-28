<template>
  <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" :ellipsis="false"
    @select="handleSelect">
    <div class="menu-left">
      <el-menu-item index="0">
        MoonLight 博客
      </el-menu-item>
      <el-menu-item index="IndexHome">
        首页
      </el-menu-item>
      <el-menu-item index="TagView">
        标签
      </el-menu-item>
      <el-menu-item index="Subject">
        专题
      </el-menu-item>
      <el-menu-item index="FollowContent">
        关注
      </el-menu-item>
      <el-menu-item index="write-blog">
        写博客
      </el-menu-item>
    </div>

    <div class="menu-right">
      <!-- 替换为搜索框 -->
      <el-menu-item index="search">
        <el-input v-model="searchQuery" placeholder="搜索..." @keyup.enter="handleSearch" clearable />
      </el-menu-item>
      <el-sub-menu index="personInfo" @click="handleMenuClick">
        <template #title>
          <div class="avatar-wrapper">
            <!-- 判断是否有头像，如果有就显示头像，如果没有就显示默认图标 -->
            <img v-if="user && user.avatarUid" :src="user.avatarUrl" alt="头像" class="avatar" />
            <svg v-else t="1740022369961" class="icon" viewBox="0 0 1024 1024" version="1.1"
              xmlns="http://www.w3.org/2000/svg" p-id="5462" width="40" height="40">
              <path
                d="M512 256a128 128 0 1 0 0 256 128 128 0 0 0 0-256zM298.666667 384a213.333333 213.333333 0 1 1 426.666666 0A213.333333 213.333333 0 0 1 298.666667 384z"
                p-id="5463"></path>
              <path
                d="M512 85.333333C276.352 85.333333 85.333333 276.352 85.333333 512s191.018667 426.666667 426.666667 426.666667 426.666667-191.018667 426.666667-426.666667S747.648 85.333333 512 85.333333zM0 512C0 229.248 229.248 0 512 0s512 229.248 512 512-229.248 512-512 512S0 794.752 0 512z"
                p-id="5464"></path>
              <path
                d="M170.666667 832A192 192 0 0 1 362.666667 640h298.666666a192 192 0 0 1 192 192v42.666667a42.666667 42.666667 0 1 1-85.333333 0v-42.666667a106.666667 106.666667 0 0 0-106.666667-106.666667h-298.666666A106.666667 106.666667 0 0 0 256 832v42.666667a42.666667 42.666667 0 1 1-85.333333 0v-42.666667z"
                p-id="5465"></path>
            </svg>
          </div>
        </template>

        <!-- 菜单项 -->
        <el-menu-item index="UserInfo">个人信息</el-menu-item>
        <el-menu-item index="ContentManagement">内容管理</el-menu-item>
        <el-menu-item index="CommentManagement">我的评论</el-menu-item>
        <el-menu-item index="CollectionManagement">我的收藏</el-menu-item>
        <el-menu-item index="FollowList">我的关注</el-menu-item>
        <el-menu-item index="2-5" @click = "logout">退出登录</el-menu-item>
      </el-sub-menu>
      <LoginModal ref="loginModalRef" />
    </div>
  </el-menu>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import LoginModal from '../login/LoginModal.vue'
import { ElMessage, ElMessageBox } from "element-plus";
// 用户信息
const user = JSON.parse(localStorage.getItem("user"));
const loginModalRef = ref(null)
const router = useRouter()

const activeIndex = ref('1')
let searchQuery = ref('')

const handleMenuClick = (event) => {
  if (!user) {
    event.preventDefault(); // 阻止菜单展开
    loginModalRef.value.openLoginModal();// 跳转到登录页面
  }
};

const handleSelect = (key, keyPath) => {
  console.log(key, keyPath)
  if (key === 'write-blog') {
    router.push({ name: 'ArticleEditor' })
  }
  if (key === 'IndexHome') {
    router.push({ name: 'IndexHome' })
  }
  if (key === 'TagView') {
    router.push({ name: 'TagView' })
  }
  if (key === 'Subject') {
    router.push({ name: 'Subject' })
  }
  if (key === 'BlogCategory') {
    router.push({ name: 'BlogCategory' })
  }
  if (key === 'UserInfo') {
    router.push({ name: 'UserInfo' })
  }
  if (key === 'ContentManagement') {
    router.push({ name: 'ContentManagement' })
  }
  if (key === 'CommentManagement') {
    router.push({ name: 'CommentManagement' })
  }
  if (key === 'CollectionManagement') {
    router.push({ name: 'CollectionManagement' })
  }
  if (key === 'FollowContent') {
    router.push({ name: 'FollowContent' })
  }
  if (key === 'FollowList') {
    router.push({ name: 'FollowList' })
  }
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ name: 'BlogList', params: { keyword: searchQuery.value.trim() } })
  }
}
// 退出登录
const logout = () => {
  ElMessageBox.confirm("确定要退出登录吗？", "提示", {
    confirmButtonText: "退出",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    ElMessage.success("已退出");
    setTimeout(() => {
      location.reload();
    }, 1000);
  });
};
</script>

<style scoped>
.el-menu--horizontal {
  --el-menu-horizontal-height: 80px;
  background: linear-gradient(135deg, #2c3e50, #3498db);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.menu-left,
.menu-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.el-menu-demo .el-menu-item,
.el-menu-demo .el-submenu__title {
  font-size: 16px;
  color: #ecf0f1;
  padding: 0 20px;
  height: 80px;
  display: flex;
  align-items: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-menu-demo .el-menu-item:hover,
.el-menu-demo .el-submenu__title:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: #f1c40f;
  transform: translateY(-2px);
}

.el-menu-demo .el-menu-item.is-active {
  border-bottom: 3px solid #f1c40f;
  color: #f1c40f;
}

/* 搜索框优化 */
.el-input {
  width: 240px;
  border-radius: 25px;
  transition: all 0.3s ease;
}

.el-input :deep(.el-input__wrapper) {
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: none;
  padding: 0 20px;
}

.el-input :deep(.el-input__inner) {
  color: #ecf0f1;
}

.el-input:hover :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.15);
}

/* 用户菜单优化 */
.avatar-wrapper {
  position: relative;
  transition: transform 0.3s ease;
}

.avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ecf0f1;
  transition: all 0.3s ease;
}

.icon {
  width: 45px;
  height: 45px;
  fill: #ecf0f1;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  transform: scale(1.05);
}

/* 下拉菜单优化 */
.el-sub-menu :deep(.el-menu) {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  padding: 8px 0;
  margin-top: 10px;
}

.el-sub-menu .el-menu-item {
  font-size: 14px;
  color: #2c3e50;
  min-width: 160px;
  height: 40px;
  line-height: 40px;
  margin: 4px 0;
  transition: all 0.2s ease;
}

.el-sub-menu .el-menu-item:hover {
  background: #f8f9fa;
  color: #3498db;
}

.el-sub-menu .el-menu-item.is-active {
  color: #f1c40f;
}

/* 响应式优化 */
@media (max-width: 992px) {
  .el-menu--horizontal {
    padding: 0 20px;
  }
  
  .menu-left,
  .menu-right {
    gap: 15px;
  }
  
  .el-input {
    width: 180px;
  }
}

@media (max-width: 768px) {
  .el-menu-demo .el-menu-item,
  .el-menu-demo .el-submenu__title {
    font-size: 14px;
    padding: 0 12px;
  }
  
  .el-input {
    width: 140px;
  }
}
</style>