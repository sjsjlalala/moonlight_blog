import { createRouter, createWebHistory } from 'vue-router'
import ArticleEditor from '@/components/blog/ArticleEditor.vue'
import IndexHome from '@/views/IndexHome.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/ArticleEditor',
      name: 'ArticleEditor',
      component: ArticleEditor,
    },
    {
      path: '/index',
      name: 'IndexHome',
      component: IndexHome,
    },
  ],
})

export default router
