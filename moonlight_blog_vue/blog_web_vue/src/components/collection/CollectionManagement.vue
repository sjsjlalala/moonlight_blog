<template>
    <div class="collection-management">
        <h2 class="title" v-if = "props.isEdit">📚 我的收藏夹</h2>

        <!-- 收藏夹列表 -->
        <el-card v-for="category in categories" :key="category.uid" class="category-card" v-if = "categories.length > 0">
            <template #header>
                <div class="category-header">
                    <h3 class="category-name">{{ category.categoryName }}</h3>
                    <!-- 收藏夹描述 -->
                    <p class="category-description">{{ category.description }}</p>
                    <div class="header-actions" >
                        <!-- 编辑收藏夹按钮 -->
                        <el-button type="text" @click="editCategory(category)" class="action-button" v-if = "props.isEdit">
                            <el-icon>
                                <Edit />
                            </el-icon>
                        </el-button>
                        <!-- 删除收藏夹按钮 -->
                        <el-button type="text" @click="deleteCategory(category)" class="action-button" v-if = "props.isEdit">
                            <el-icon>
                                <Delete />
                            </el-icon>
                        </el-button>
                        <!-- 收起/展开按钮 -->
                        <el-button type="text" @click="toggleCollapse(category)" class="action-button">
                            <el-icon>
                                <ArrowDown v-if="!category.isCollapsed" />
                                <ArrowUp v-else />
                            </el-icon>
                        </el-button>
                    </div>
                </div>
            </template>



            <!-- 博客列表 -->
            <div v-if="category.blogs && category.blogs.length > 0 && !category.isCollapsed" class="blog-list">
                <el-card v-for="blog in category.blogs" :key="blog.uid" class="blog-card">
                    <div class="blog-info" @click="goToBlog(blog.uid)">
                        <img v-if="blog.coverImageUrl" :src="blog.coverImageUrl" class="blog-cover" />
                        <div class="blog-details">
                            <h4 class="blog-title">{{ blog.title }}</h4>
                            <p class="blog-intro">{{ blog.introduction }}</p>
                            <div class="blog-stats">
                                <span>👍 {{ blog.likes }}</span>
                                <span>👁️ {{ blog.clicks }}</span>
                                <span>⭐ {{ blog.favorites }}</span>
                            </div>
                        </div>
                    </div>
                    <!-- 取消收藏按钮 -->
                    <el-button v-if = "props.isEdit" type="text" @click.stop="uncollectBlog(category, blog)" class="uncollect-button">
                        取消收藏
                    </el-button>
                </el-card>
            </div>

            <!-- 无博客提示 -->
            <el-empty v-else-if="!category.isCollapsed" description="暂无博客" class="empty-placeholder" />
        </el-card>
        <div v-else >
            <el-empty  description="暂无收藏夹" class="empty-placeholder" />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { ArrowDown, ArrowUp, Edit, Delete, Close } from "@element-plus/icons-vue";
import {
    fetchBlogCollectionApi,
    toggleBlogCollectionApi,
    updateUserCategoryApi,
    deleteUserCategoryApi

} from "../../api/blogApi";


const router = useRouter();
const categories = ref([]); // 存储收藏夹数据
const user = JSON.parse(localStorage.getItem("user"));
const props = defineProps({
    isEdit: {
        type: Boolean,
        default: true
    },
    uid: {
        type: String,
        default: '',
        required: true
    }
})

// 获取收藏夹数据
const fetchUserCategories = async () => {
    try {
        let uid = props.uid;
        if (!props.uid) {
            uid = user.uid;
        }
        
        const response = await fetchBlogCollectionApi(uid);
        if (response.code === 200) {
            // 初始化每个收藏夹的收起状态
            categories.value = response.data.map((category) => ({
                ...category,
                isCollapsed: false, // 默认展开
            }));
        } else {
            throw new Error(response.message || "获取收藏夹失败");
        }
    } catch (error) {
        ElMessage.error(error.message);
    }
};

// 跳转到博客详情页
const goToBlog = (blogId) => {
    router.push({ name: "BlogDetail", params: { uid: blogId } });
};

// 编辑收藏夹
const editCategory = async (category) => {
    try {
        const { value: categoryName } = await ElMessageBox.prompt("编辑收藏夹名称", "编辑", {
            inputValue: category.categoryName,
            confirmButtonText: "下一步",
            cancelButtonText: "取消",
        });

        if (!categoryName) return;

        const { value: description } = await ElMessageBox.prompt("编辑收藏夹描述", "编辑", {
            inputValue: category.description || "",
            confirmButtonText: "保存",
            cancelButtonText: "取消",
        });

        if (description !== null) {
            const response = await updateUserCategoryApi({
                uid: category.uid,
                categoryName,
                description,
            });

            if (response.code === 200) {
                category.categoryName = categoryName;
                category.description = description;
                ElMessage.success("收藏夹已更新");
            } else {
                throw new Error(response.message || "更新失败");
            }
        }
    } catch (error) {
        ElMessage.info("取消编辑");
    }
};


// 删除收藏夹
const deleteCategory = async (category) => {
    try {
        await ElMessageBox.confirm("确定删除该收藏夹吗？", "提示", {
            confirmButtonText: "删除",
            cancelButtonText: "取消",
            type: "warning",
        });
        const data = new FormData();
        data.append("uid", category.uid);
        const response = await  deleteUserCategoryApi(data);
        if (response.code === 200) {
            categories.value = categories.value.filter((c) => c.uid !== category.uid);
            ElMessage.success("收藏夹已删除");
        } else {
            throw new Error(response.message || "删除失败");
        }
    } catch (error) {
        ElMessage.info("取消删除");
    }
};

// 取消收藏博客
const uncollectBlog = async (category, blog) => {
    try {
        await ElMessageBox.confirm("确定取消收藏该博客吗？", "提示", {
            confirmButtonText: "取消收藏",
            cancelButtonText: "取消",
            type: "warning",
        });
        const response = await toggleBlogCollectionApi({
            blogUid: blog.uid,
            categoryUid: category.uid,
            isCollected: false,
        });
        if (response.code === 200) {
            category.blogs = category.blogs.filter((b) => b.uid !== blog.uid);
            ElMessage.success("博客已取消收藏");
        } else {
            throw new Error(response.message || "操作失败");
        }
    } catch (error) {
        ElMessage.info("取消操作");
    }
};

// 切换收起/展开状态
const toggleCollapse = (category) => {
    category.isCollapsed = !category.isCollapsed;
};

onMounted(fetchUserCategories);
</script>

<style scoped>
.collection-management {
    padding: 20px;
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    width: 800px;
    margin: auto;
}

.title {
    text-align: center;
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
}

.category-card {
    margin-bottom: 20px;
    border-radius: 12px;
    overflow: hidden;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.category-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.category-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.category-name {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin: 0;
}

.header-actions {
    display: flex;
    align-items: center;
    gap: 10px;
}

.action-button {
    font-size: 14px;
    padding: 0;
}

.category-description {
    font-size: 14px;
    color: #666;
    margin: 10px 0;
}

.blog-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.blog-card {
    border-radius: 8px;
    overflow: hidden;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
}

.blog-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.blog-info {
    width: 100%;
    display: flex;
    align-items: center;
    gap: 20px;
    cursor: pointer;
    flex: 1;
}

.blog-cover {
    width: 100px;
    height: 60px;
    object-fit: cover;
    border-radius: 6px;
}

.blog-details {
    flex: 1;
}

.blog-title {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin: 0 0 5px 0;
}

.blog-intro {
    font-size: 14px;
    color: #666;
    margin: 0 0 5px 0;
}

.blog-stats {
    display: flex;
    gap: 10px;
    font-size: 12px;
    color: #999;
}

.uncollect-button {
    font-size: 16px;
    color: #f56c6c;
    padding: 0;
    margin-left: 10px;
}

.empty-placeholder {
    margin: 20px 0;
}

.blog_card {
    display: flex;

    justify-content: space-between;
}
</style>