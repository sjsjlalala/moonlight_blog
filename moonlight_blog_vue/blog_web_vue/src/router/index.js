import { createRouter, createWebHistory } from 'vue-router'
import ArticleEditor from '@/components/blog/ArticleEditor.vue'
import IndexHome from '@/views/IndexHome.vue'
import BlogDetail from '@/components/blog/BlogDetail.vue'
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
    {
      path: '/blog/:uid',
      name: 'BlogDetail',
      component: BlogDetail,
      props: true
    }
  ],
})

export default router
