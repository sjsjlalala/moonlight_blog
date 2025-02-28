<template>
  <div class="common-layout">
    <el-header height="80px">
      <Navigation />
    </el-header>
    <div class="dynamic-layout">
      <!-- 左侧 Aside -->
      <aside v-if="showLeftAside" class="left-aside">
        <component :is="leftAsideComponent" />
      </aside>

      <!-- Main 主内容 -->
      <main class="main-content" >
        <router-view />
      </main>

      <!-- 右侧 Aside -->
      <aside v-if="showRightAside" class="right-aside" >
        <component :is="rightAsideComponent" />
      </aside>
    </div>
  </div>
</template>

<script setup>
import Navigation from '@/components/header/Navigation.vue';
import SubjectEdit from '@/components/subject/SubjectEdit.vue';

import { computed, provide, ref, watch  } from "vue";
import { useRoute } from "vue-router";
import AuthorInfo from '@/components/user/AuthorInfo.vue';
import DefalutRightAside from '@/components/aside/DefalutRightAside.vue';

const route = useRoute();

// 组件之间传参
const isEditor = ref(false); 
const subjectUid = ref('');
const isFresh = ref(false);
const author = ref({})

provide("isEditor", isEditor); // 提供的 isEditor 变量
provide("subjectUid", subjectUid);
provide("isFresh", isFresh);
provide("author", author);


const showRightAside = ref(false)
const rightAsideComponent = ref(null)

// 观察路由和编辑状态变化
watch(
  [() => route.name, isEditor], 
  ([routeName, isEditorVal]) => {
    let component = null
    
    switch(routeName) {
      case 'SubjectDetail':
        component = isEditorVal ? SubjectEdit : null
        break
      case 'BlogDetail':
        component = AuthorInfo
        break
      case 'IndexHome':
        component = DefalutRightAside
        break
      default:
        component = null
    }

    // 统一更新状态
    showRightAside.value = !!component
    rightAsideComponent.value = component
    
    // 调试日志
    console.log('右侧边栏状态:', {
      route: routeName,
      component: component?.name,
      visible: showRightAside.value
    })
  }, 
  { immediate: true } // 初始化立即执行
)

// 优化后的计算属性（可选）
const effectiveComponent = computed(() => {
  return showRightAside.value ? rightAsideComponent.value : null
})

</script>

<style scoped>

.common-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.dynamic-layout {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  gap: 30px;
  transition: all 0.3s ease;
}

.left-aside {
  width: 400px;
  flex-shrink: 0;
  /* 左侧边栏过渡效果 */
  animation: slideInLeft 0.3s ease;
}

.right-aside {
  width: 400px;
  flex-shrink: 0;
  /* 右侧边栏过渡效果 */
  animation: slideInRight 0.3s ease;
}

.main-content {
  flex: 1;
  min-width: 0;
  /* 主内容区自适应宽度 */
  width: calc(100% - 30px * 2 - 400px * (var(--aside-count)));
}

/* 动态调整主内容宽度 */
.dynamic-layout {
  --aside-count: 0;
}
.dynamic-layout .left-aside {
  --aside-count: calc(var(--aside-count) + 1);
}
.dynamic-layout .right-aside {
  --aside-count: calc(var(--aside-count) + 1);
}

/* 响应式处理 */
@media (max-width: 1200px) {
  .dynamic-layout {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .left-aside,
  .right-aside {
    width: 100%;
    max-width: 300px;
  }
}

@keyframes slideInLeft {
  from {
    transform: translateX(-20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideInRight {
  from {
    transform: translateX(20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
</style>
