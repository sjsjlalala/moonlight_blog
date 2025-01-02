import { createRouter, createWebHistory } from 'vue-router'
import ArticleEditor from '@/components/blog/ArticleEditor.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/ArticleEditor',
      name: 'ArticleEditor',
      component: ArticleEditor,
    }
  ],
})

export default router
