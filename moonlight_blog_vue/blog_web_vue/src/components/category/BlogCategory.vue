<template>
  <div style="display: flex; height: 100vh; font-family: 'Arial', sans-serif;">
    <!-- 目录栏，放置在左边 -->
    <div style="width: 250px; padding: 20px; background-color: #f4f7fc; border-right: 1px solid #e0e4e8;">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <!-- 博客分组标题 -->
        <h2 style="font-size: 18px; font-weight: bold; margin-bottom: 20px; color: #333; margin: 0;">博客分组</h2>

        <!-- 添加分组按钮 -->
        <el-button type="primary" icon="el-icon-plus" @click="DialogCategoryFormVisible = true"
          style="margin-left: auto;">
          添加分组
        </el-button>
      </div>

      <el-menu default-active="VUE学习" class="el-menu-vertical-demo" @select="handleCategorySelect"
        style="border: none;">
        <el-menu-item v-for="(category, index) in categoryOptions" :key="category.value" :index="category.value"
          style="padding: 10px 20px; margin: 5px 0; border-radius: 4px; cursor: pointer;"
          :class="{ 'active-category': selectedCategory?.label === category.label }">
          {{ category.label }}
          <!-- 删除分组按钮 -->
          <el-button type="text" size="small" style="color: #e74c3c; margin-left: 10px;"
            @click.stop="confirmDeleteCategory(category.value)">
            删除
          </el-button>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧展示区域 -->
    <div style="flex-grow: 1; padding: 20px; background-color: #ffffff;">
      <div v-if="selectedCategory">
        <div v-if="selectedCategory.blogs && selectedCategory.blogs.length > 0">
          <div v-for="(blog, blogIndex) in selectedCategory.blogs" :key="blogIndex" class="blog-container">
            <!-- 管理按钮 -->
            <div class="card-header">
            <el-dropdown class="manage-dropdown" @command="(command) => handleDropdownCommand(command, blog.blogVO)">
              <span class="manage-button">
                <el-icon :size="20"><More /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">更改分组</el-dropdown-item>
                  <el-dropdown-item command="delete">删除博客</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            </div>
            <!-- 博客卡片 -->
            <BlogCard :key="blog.blogVO?.uid " :blog="blog.blogVO || {}" :author="blog.userVO || {}"
              :tags="blog.tags || []" />
          </div>
        </div>
        <div v-else style="margin-top: 20px; color: #95a5a6;">
          <p>该分类暂无博客。</p>
        </div>
      </div>
      <div v-else style="color: #95a5a6;">
        <p>请选择一个分类查看博客。</p>
      </div>
    </div>

    <!-- 更改分组弹框 -->
    <el-dialog v-model="isEditCategoryDialogVisible" title="更改分组" width="400px" @close="resetEditCategoryDialog">
      <el-select v-model="selectedCategoryForBlog" placeholder="选择分组" style="width: 100%;">
        <el-option v-for="category in categoryOptions" :key="category.value" :label="category.label"
          :value="category.value">
        </el-option>
      </el-select>
      <span slot="footer" class="dialog-footer">
        <el-button @click="isEditCategoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changeBlogCategory">确认更改</el-button>
      </span>
    </el-dialog>

    <!-- 添加目录对话框 -->
    <el-dialog v-model="DialogCategoryFormVisible" title="添加目录" width="500px">
      <el-form :model="newCategory">
        <el-form-item label="目录名称" label-width="100px">
          <div class="el-input-group">
            <el-input v-model="newCategory.name" placeholder="请输入目录名称" />
            <el-input type="textarea" :rows="3" v-model="newCategory.description" autocomplete="off"
              placeholder="请输入目录描述" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="DialogCategoryFormVisible = false">取消</el-button>
          <el-button type="primary" @click="addSubCategory">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { fetchUserCategoriesApi, deleteBlogApi } from '../../api/blogApi'
import { fetchCategoryBlogApi, updateBlogCategoryApi, deleteCategoryApi, createUserCategoryApi } from '../../api/categoryApi'
import { formatDate } from '../../utils/dateUtils'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import BlogCard from '../blog/BlogCard.vue';
import { More } from "@element-plus/icons-vue";

const router = useRouter()
const categoryOptions = ref([]);
const currentBlog = ref({});

const selectedCategory = ref({
  value: "",
  blogs: [],
});
const hoverCard = ref(null);

// 控制更改分组对话框显示
const isEditCategoryDialogVisible = ref(false);
const selectedCategoryForBlog = ref('');

const DialogCategoryFormVisible = ref(false)
let newCategory = ref({
  parent: [],
  name: '',
  description: ''
});

// 处理目录选择
const handleCategorySelect = async (categoryName) => {
  console.log('Selected category:', categoryName);
  const response = await fetchCategoryBlogApi(categoryName);
  if (response.code === 200) {
    selectedCategory.value.value = categoryName;
    selectedCategory.value.blogs = response.data;
    console.log('获取分组博客成功:', selectedCategory.value.blogs)
  } else {
    selectedCategory.value = {};
  }

};

// 删除博客
const deleteBlog = async (blog) => {
  const data = new FormData();
  data.append('uid', blog.uid);
  const response = await deleteBlogApi(data);
  if (response.code === 200) {
    handleCategorySelect(selectedCategory.value.value);
    ElMessage({
      message: '博客删除成功',
      type: 'success',
    })
  }
  else {
    ElMessage.error('博客删除失败')
  }
};

// 打开更改分组对话框
const editCategory = (blog) => {
  currentBlog.value = blog;
  isEditCategoryDialogVisible.value = true;
};

// 更改博客的分组
const changeBlogCategory = async () => {
  const newCategoryId = selectedCategoryForBlog.value;
  const currentCategoryId = selectedCategory.value.value;
  const blogUid = currentBlog.value.uid;
  if (currentCategoryId == newCategoryId) {
    return;
  }
  const data = new FormData();
  data.append('blogUid', blogUid);
  data.append('newUid', newCategoryId);
  data.append('oldUid', currentCategoryId);
  const response = await updateBlogCategoryApi(data);
  if (response.code === 200) {
    handleCategorySelect(currentCategoryId)
  }
  // 关闭对话框
  isEditCategoryDialogVisible.value = false;
};

// 重置对话框状态
const resetEditCategoryDialog = () => {
  selectedCategoryForBlog.value = '';
};

// 下拉菜单操作
const handleDropdownCommand = (command, blog) => {
  console.log('command', command);
  if (command === 'edit') {
    editCategory(blog);
  } else if (command === 'delete') {
    deleteBlog(blog);
  }
};
// 获取用户分组
const fetchCategories = async () => {
  try {
    const response = await fetchUserCategoriesApi();
    categoryOptions.value = response.data;
    console.log('categoryOptions.value', categoryOptions.value);
  } catch (error) {
    console.error('获取用户分组数据失败:', error);
  }
};
// 跳转博客详情页
const goToBlogDetail = (uid) => {
  router.push({ name: 'BlogDetail', params: { uid } })
}
// 删除分组
const confirmDeleteCategory = (categoryUid) => {
  ElMessageBox.confirm(
    '确认删除该分组以及该分组下的所有博客吗?',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      deleteCategory(categoryUid)
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '取消删除',
      })
    })
}
const deleteCategory = async (categoryUid) => {
  console.log('删除分组')
  const data = new FormData();
  data.append('uid', categoryUid);
  const response = await deleteCategoryApi(data);
  if (response.code === 200) {
    ElMessage({
      type: 'success',
      message: '分组删除成功',
    })
    fetchCategories();
  } else {
    ElMessage.error('分组删除失败');
  }
}
const addSubCategory = async () => {
  let parentValue = '';
  if (newCategory.value.parent) {
    parentValue = newCategory.value.parent[newCategory.value.parent.length - 1];
  }
  // 校验
  try {
    if (!newCategory.value.name) {
      throw new Error('请输入子目录名称');
    }

    if (!newCategory.value.description) {
      throw new Error('请输入子目录描述');
    }
  }
  catch (err) {
    ElMessage({
      showClose: true,
      message: err.message,
      type: 'error',
    });
    return;
  }
  const newSubCategory = {
    parentUid: parentValue,
    categoryName: newCategory.value.name,
    description: newCategory.value.description
  };
  const response = await createUserCategoryApi(newSubCategory);
  if (response.code === 200) {
    newCategory.value.parent = [];
    newCategory.value.name = '';
    newCategory.value.description = '';
    fetchCategories();
    DialogCategoryFormVisible.value = false;
    ElMessage({
      showClose: true,
      message: "添加目录成功",
      type: 'success',
    });
  }
};
onMounted(() => {
  fetchCategories();
});
</script>

<style scoped>
/* 优化菜单样式 */
.el-menu-item {
  transition: background-color 0.3s ease;
}

.el-menu-item:hover {
  background-color: #ecf0f1;
}

.el-menu-item.active-category {
  background-color: #3498db;
  color: white;
}

.blog-container {
  
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

.blog-card-manage-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background-color: rgba(255, 255, 255, 0.9); /* 半透明背景 */
  border-radius: 50%; /* 圆形按钮 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* 阴影效果 */
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.blog-card-manage-btn:hover {
  background-color: rgba(255, 255, 255, 1); /* 鼠标悬停时背景变亮 */
}

.blog-card-manage-btn .el-icon {
  font-size: 16px;
  color: #666; /* 图标颜色 */
}

.blog-category-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  text-align: center;
}

.el-input-group {
  display: flex;
  align-items: center;
  flex-direction: column;
  gap: 15px;
}
</style>
