<template>
  <div class="subject-list">
    <!-- 搜索框和筛选框 -->
    <div class="filter-box">
      <el-input
        v-model="searchQuery"
        placeholder="搜索专题"
        suffix-icon="el-icon-search"
        class="search-input"
        clearable
      />
      <el-button
     
        type="primary"
        class="search-button"
        @click="fetchSubjects"
        
      >
        搜索
      </el-button>
      <el-cascader placeholder="专题分类" :options="sysCategoryoptions" filterable :show-all-levels="false"
                  tag-type="success" tag-effect="light" size="large" v-model="newArray" @change="handleCategoryChange" clearable/>
    </div>

    <!-- 专题卡片 -->
    <div class="subject-cards">
      <div
        class="subject-card"
        v-for="(subject, index) in pagedSubjects"
        :key="subject.subject.uid"
        @click = "goToDetail(subject.subject.uid)"
      >
        <div class="image-wrapper">
          <img v-if = "subject.subject.fileUid" :src="subject.subject.fileUrl" alt="subject.name" class="subject-image" />
        </div>
        <h3 class="subject-name">{{ subject.subject.subjectName }}</h3>
      </div>
    </div>

    <!-- 分页组件 -->
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="filteredSubjects.length"
      @current-change="handlePageChange"
      layout="prev, pager, next"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElInput, ElPagination, ElButton } from 'element-plus';
import { fetchSubjectDetailApi } from '../api/subjectApi';
import { fetchSysCategoryApi } from '../api/categoryApi'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
    uid: {
        type: String,
        default: '',
        required: true
    }
})
// 初始化数据
const subjects = ref([]);
const searchQuery = ref('');
const selectedFilter = ref('');
const currentPage = ref(1);
const pageSize = 9;
const sysCategoryoptions = ref([])
const newArray = ref([])


// 计算筛选后的专题
const filteredSubjects = computed(() => {
  return subjects.value.filter(subject => {
    const matchesSearch = subject.subject.subjectName.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchesFilter = selectedFilter.value ? subject.subject.subjectName.includes(selectedFilter.value) : true;
    return matchesSearch && matchesFilter;
  });
});

// 计算当前页显示的专题
const pagedSubjects = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return subjects.value.slice(start, end);
});

// 处理页码变化
const handlePageChange = (page) => {
  currentPage.value = page;
};
// 处理分类变化
const handleCategoryChange = (value) => {
  newArray.value = value;
  selectedFilter.value = value ? value[value.length - 1] : '';
  fetchSubjects();
};
// 获取专题数据
const fetchSubjects = async () => {
  const data = {
    currentPage: currentPage.value,
    pageSize: pageSize,
    keyword: searchQuery.value,
    filterWord: selectedFilter.value,
    userUid: props.uid
  };

  const response = await fetchSubjectDetailApi(data);
  if (response.code === 200) {
    subjects.value = response.data;
    console.log('专题列表:', subjects.value);
  } else {
    console.error('获取专题列表失败:', response.message);
  }
};
// 获取系统分组
const fetchConfigCategory = async () => {
  try {
    const response = await fetchSysCategoryApi();
    sysCategoryoptions.value = response.data;
  } catch (error) {
    console.error('获取系统分组数据失败:', error);
  }
};
// 跳转到专题详情
const goToDetail = (uid) => {
  router.push({ name: 'SubjectDetail', params: { uid } })
}
// 获取初始数据
onMounted(() => {
  fetchSubjects();
  fetchConfigCategory();
});
</script>

<style scoped>
.subject-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0 auto;
  padding: 40px 20px;
  background: linear-gradient(135deg, #f2f2f2, #ffffff);
  width: 1200px;
}

.filter-box {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 30px;
  justify-content: center;
  width: 100%;
}

.search-input {
  width: 300px;
  border-radius: 30px;
  background: linear-gradient(45deg, #ff7e5f, #feb47b);
  color: white;
  font-weight: bold;
  height: 38px;
}

.search-button {
  border-radius: 30px;
  background: #ff7e5f;
  color: white;
  font-weight: bold;
  padding: 0 20px;
  text-align: center;
}

.filter-select {
  width: 180px;
  border-radius: 30px;
  background-color: #f1f1f1;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.subject-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 30px;
  width: 100%;
  
}

.subject-card {
  background: linear-gradient(135deg, #ffffff, #f0f0f0);
  border-radius: 20px;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  padding: 10px;
}

.subject-card:hover {
  transform: translateY(-12px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.image-wrapper {
  width: 100%;
  height: 160px;
  position: relative;
}

.subject-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 15px;
  transition: transform 0.3s ease;
}

.subject-image:hover {
  transform: scale(1.1);
}

.subject-name {
  font-size: 18px;
  font-weight: 600;
  margin: 15px 0;
  color: #333;
}

/* 分页组件居中 */
.pagination {
  display: flex;
  justify-content: center;
  width: 100%;
  margin-top: 40px;
  padding-bottom: 20px;
}
</style>
