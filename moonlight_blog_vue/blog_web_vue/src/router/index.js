import { createRouter, createWebHistory } from 'vue-router'
import ArticleEditor from '@/components/blog/ArticleEditor.vue'
import IndexHome from '@/views/IndexHome.vue'
import BlogDetail from '@/components/blog/BlogDetail.vue'
import TagView from '@/views/Tag.vue'

import Subject from '@/views/Subject.vue'
import SubjectDetail from '@/components/subject/SubjectDetail.vue'
import BlogCategory from '@/components/category/BlogCategory.vue'
import BlogList from '@/components/blog/BlogList.vue'
import Login from '@/views/Login.vue'
import UserInfo from '@/components/user/UserInfo.vue'
import ContentManagement from '@/components/user/ContentManagement.vue'
import SubjectEdit from '@/components/subject/SubjectEdit.vue'
import SubjectBlogEdit from '@/components/subject/SubjectBlogEdit.vue'
import CommentManagement from '@/components/comment/CommentManagement.vue'
import CollectionManagement from '@/components/collection/CollectionManagement.vue'
import AuthorInfo from '@/components/user/AuthorInfo.vue'
import AuthorDetail from '@/components/user/AuthorDetail.vue'
import FollowContent from '@/components/follow/FollowContent.vue'
import FollowList from '@/components/follow/FollowList.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/ArticleEditor/:id?',
      name: 'ArticleEditor',
      component: ArticleEditor,
      props: true
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
      props: route => ({
        uid: route.params.uid,
        isEditor: route.query.isEditor === 'true' // 确保 isEditor 是布尔值
      })
    },
    {
      path: '/blogCategory',
      name: 'BlogCategory',
      component: BlogCategory,
    },
    {
      path: '/BlogList/:keyword/:uid?',
      name: 'BlogList',
      component: BlogList,
      props: true
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
    },
    {
      path: '/userInfo',
      name: 'UserInfo',
      component: UserInfo,
    },
    {
      path: '/contentManagement/:isEdit?',
      name: 'ContentManagement',
      component: ContentManagement,
    },
    {
      path: '/subjectEdit',
      name: 'SubjectEdit',
      component: SubjectEdit,
    }, 
    {
      path: '/subjectBlogEdit',
      name: 'SubjectBlogEdit',
      component: SubjectBlogEdit,
    },
    {
      path: '/commentManagement',
      name: 'CommentManagement',
      component: CommentManagement,
    },
    {
      path: '/collectionManagement',
      name: 'CollectionManagement',
      component: CollectionManagement,
    },
    {
      path: '/authorInfo',
      name: 'AuthorInfo',
      component: AuthorInfo,
    },
    {
      path: '/authorDetail/:uid?',
      name: 'AuthorDetail',
      component: AuthorDetail,
      props: true
    },
    {
      path: '/followContent/:uid?',
      name: 'FollowContent',
      component: FollowContent,
      props: true
    },
    {
      path: '/followList/:uid?',
      name: 'FollowList',
      component: FollowList,
      props: true
    },
    
    
  ],
})

export default router
