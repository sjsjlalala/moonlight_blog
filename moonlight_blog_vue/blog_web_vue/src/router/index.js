import { createRouter, createWebHistory } from 'vue-router'
import ArticleEditor from '@/components/blog/ArticleEditor.vue'
import IndexHome from '@/views/IndexHome.vue'
import BlogDetail from '@/components/blog/BlogDetail.vue'
import TagView from '@/views/Tag.vue'

import Subject from '@/views/Subject.vue'
import SubjectDetail from '@/components/subject/SubjectDetail.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/ArticleEditor',
      name: 'ArticleEditor',
      component: ArticleEditor,
    },
    {
      path: '/',
      name: 'IndexHome',
      component: IndexHome,
    },
    {
      path: '/blog/:uid',
      name: 'BlogDetail',
      component: BlogDetail,
      props: true
    },
    {
      path: '/tagView',
      name: 'TagView',
      component: TagView,
    },
    {
      path: '/subject',
      name: 'Subject',
      component: Subject,
    },
    {
      path: '/subjectDetail/:uid',
      name: 'SubjectDetail',
      component: SubjectDetail,
      props: true
    }
    
  ],
})

export default router
