<template>
    <div class="subject-blog-edit">
        <h2 class="title">管理专题博客</h2>

        <!-- 博客列表 -->
        <el-card class="blog-list">
            <el-scrollbar height="300px">
                <el-list v-if="blogs.length > 0">
                    <el-list-item v-for="blog in blogs" :key="blog.uid" class="blog-item">
                        <span class="blog-title">{{ blog.title }}</span>
                        <el-button type="danger" size="small" @click="removeBlog(blog.uid)">
                            <el-icon>
                                <Delete />
                            </el-icon>
                            <span>移除</span>
                        </el-button>
                    </el-list-item>
                </el-list>
                <el-empty v-else description="暂无博客" />
            </el-scrollbar>
        </el-card>

        <!-- 添加博客按钮 -->
        <el-button type="primary" class="add-button" @click="openAddBlogDialog">
            <el-icon>
                <Plus />
            </el-icon>
            <span>添加博客</span>
        </el-button>

        <!-- 添加博客对话框 -->
        <!-- 添加博客对话框 -->
        <el-dialog v-model="dialogVisible" title="添加博客" width="500px">
            <el-select v-model="selectedBlog" placeholder="请输入关键词搜索博客" filterable remote :remote-method="fetchBlogs"
                :loading="loading" class="blog-select" clearable @scroll="handleScroll">
                <el-option v-for="blog in availableBlogs" :key="blog.uid" :label="blog.title" :value="blog.uid" />
            </el-select>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmAddBlog">确认</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import { Delete, Plus } from '@element-plus/icons-vue';
import { fetchSubjectBlogApi, addSubjectBlogApi, deleteSubjectBlogApi } from '../../api/subjectApi.js'
import { deleteBlogApi, fetchBlogListApi } from '../../api/blogApi.js'


// 组件传参
// 接收专题数据作为 props
const subjectUid = inject('subjectUid');
const isFresh = inject('isFresh');

const loading = ref(false);
const page = ref(1); // 当前页数
const pageSize = ref(10);
const hasMore = ref(true); // 是否还有更多数据

// 示例：已挂载的博客列表（实际应从 API 获取）
const blogs = ref([]);

// 示例：可选的博客列表（实际应从 API 获取）
const availableBlogs = ref([]);

const filteredBlogs = ref([...availableBlogs.value]); // 初始显示全部博客

// 对话框状态
const dialogVisible = ref(false);

// 选择的博客 ID
const selectedBlog = ref(null);

// 打开添加博客对话框
const openAddBlogDialog = () => {
    dialogVisible.value = true;
};
// 请求后端 API 获取专题博客（带分页）
const fetchBlogs = async (query = "") => {
    if (loading.value || !hasMore.value) return;

    loading.value = true;

    try {
        const response = await fetchBlogListApi(page.value, pageSize.value, query);

        if (response.data.length > 0) {
            availableBlogs.value = page.value === 1
                ? response.data.map(item => item.blogVO)
                : [...availableBlogs.value, ...response.data.map(item => item.blogVO)];

            page.value++; // 下一页
            console.log("a博客列表", availableBlogs.value);
        } else {
            hasMore.value = false; // 没有更多数据
        }
    } catch (error) {
        console.error("获取博客失败:", error);
        ElMessage.error("获取博客失败");
    } finally {
        loading.value = false;
    }
};

const fetchSubjectBlogs = async () => {
    try {
        const response = await fetchSubjectBlogApi({
            currentPage: page.value,
            pageSize: pageSize.value,
            uid: subjectUid.value
        });
        if (response.code === 200) {
            blogs.value = response.data;
            console.log("blogs", blogs.value)
        } else {
            ElMessage.error("专题还没有博客");
        }
    } catch (error) {
        console.error("获取博客失败:", error);
        ElMessage.error("获取博客失败");
    }
};



// 监听滚动到底部，加载更多
const handleScroll = (event) => {
    const selectPanel = event.target;
    if (selectPanel.scrollHeight - selectPanel.scrollTop <= selectPanel.clientHeight + 10) {
        fetchBlogs();
    }
};

// 确认添加博客
const confirmAddBlog = async () => {
    if (!selectedBlog.value) {
        ElMessage.warning('请选择一篇博客');
        return;
    }
    console.log('selectedBlog', selectedBlog.value)
    const blogToAdd = blogs.value.find(
        (blog) => blog.uid === selectedBlog.value
    );

    if (blogToAdd) {
        ElMessage.success('博客已添加到专题，请选择其他博客');
        selectedBlog.value = null; // 清空选择
    }
    const data = new FormData();
    data.append('subjectUid', subjectUid.value);
    data.append('blogUid', selectedBlog.value);
    const response = await addSubjectBlogApi(data);
    if (response.code === 200) {
        ElMessage.success('博客添加成功, 可继续添加博客');
        fetchSubjectBlogs();
        isFresh.value = true;
        selectedBlog.value = null; // 清空选择
    } else {
        ElMessage.error('博客添加失败');
    }

};

// 移除博客
const removeBlog = (uid) => {
    ElMessageBox.confirm('确定要移除这篇博客吗？', '提示', {
        confirmButtonText: '移除',
        cancelButtonText: '取消',
        type: 'warning',
    })
        .then(() => {
            const data = new FormData();
            data.append('subjectUid', subjectUid.value);
            data.append('blogUid', uid);
            deleteSubjectBlogApi(data).then((res) => {
                if (res.code === 200) {
                    ElMessage.success("移除成功");
                    isFresh.value = true;
                    blogs.value = blogs.value.filter((blog) => blog.uid !== uid);
                }
                else {
                    ElMessage.error("移除失败");
                }
            });
        })
        .catch(() => {
            ElMessage.info('操作取消');
        });
};
onMounted(async () => {
    console.log('subjectUid', subjectUid.value)
    await fetchSubjectBlogs()
});
</script>



<style scoped>
.subject-blog-edit {
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #303133;
}

.blog-list {
    margin-bottom: 20px;
    border-radius: 8px;
}

.blog-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid #eee;
    transition: background-color 0.3s;
}

.blog-item:hover {
    background-color: #f5f7fa;
}

.blog-title {
    font-size: 14px;
    color: #606266;
}

.add-button {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    margin-top: 20px;
}

.blog-select {
    width: 100%;
}
</style>