// Composables
import { createRouter, createWebHistory } from "vue-router";
//路由表
const routes = [
  //当路由为空时，重定向到登录页面
  {
    path: "/",
    redirect: "/login",
  },
  //登录页面
  {
    path: "/login",
    name: "login",
    component: () => import("~/views/Login.vue"),
  },
  {
    path: ('/student'),
    component: () => import('../views/Layout.vue'),
    redirect: ('/student/homepage'),
    // 设置路由守卫进行登录判断及拦截
    beforeEnter: () => {
      //解决刷新退回登录页面问题
      const token = localStorage.getItem("token") ? localStorage.getItem("token") : ""
      if (!token) {
        // ElMessage({
        //   message: '请先登录!',
        //   type: 'warning'
        // })
        // return ('/login')
      }
    },
    children: [
      // 主页
      { 
        path: ('homepage'), 
        component: () => import('../views/student/Homepage.vue') 
      },
      { 
        path: ('info-edit'), 
        component: () => import('../views/student/InfoEdit.vue') },
      {
        path: ('password-edit'),
        component: () => import('../views/common/PasswordEdit.vue'),
      },
      { 
        path: ('course-select'),
        component: () => import('../views/student/CourseSelect.vue') 
      },
      { 
        path: ('course-view'), 
        component: () => import('../views/student/CourseView.vue') },
      { 
        path: ('score-view'), 
        component: () => import('../views/student/ScoreView.vue') 
      },
      { 
        path: ('social-prac'), 
        component: () => import('../views/student/SocialPrac.vue') 
      },
      { 
        path: ('activity'), 
        component: () => import('../views/student/Activity.vue') 
      },
      { 
        path: ('achievement'), 
        component: () => import('../views/student/Achievement.vue') 
      },
      { 
        path: ('peer-assess'), 
        component: () => import('../views/student/PeerAssess.vue') 
      },
    ]
  },
  {
    path: ('/teacher'),
    component: () => import('../views/Layout.vue'),
    redirect: ('/teacher/homepage'),
    // 设置路由守卫进行登录判断及拦截
    beforeEnter: () => {
      //解决刷新退回登录页面问题
      const token = localStorage.getItem("token") ? localStorage.getItem("token") : ""
      if (!token) {
        // ElMessage({
        //   message: '请先登录!',
        //   type: 'warning'
        // })
        // return ('/login')
      }
    },
    children: [
      // 主页
      { 
        path: ('homepage'), 
        component: () => import('../views/teacher/Homepage.vue') 
      },
      { 
        path: ('info-edit'), 
        component: () => import('../views/teacher/InfoEdit.vue') 
      },
      {
        path: ('password-edit'),
        component: () => import('../views/common/PasswordEdit.vue'),
      },
      { 
        path: ('course-view'), 
        component: () => import('../views/teacher/CourseView.vue') 
      },
      { 
        path: ('score-manage'),
        component: () => import('../views/teacher/ScoreManage.vue') 
      },
    ]
  },
  {
    path: ('/admin'),
    component: () => import('../views/Layout.vue'),
    redirect: ('/admin/course-manage'),
    // 设置路由守卫进行登录判断及拦截
    beforeEnter: () => {
      //解决刷新退回登录页面问题
      const token = localStorage.getItem("token") ? localStorage.getItem("token") : ""
      if (!token) {
        // ElMessage({
        //   message: '请先登录!',
        //   type: 'warning'
        // })
        // return ('/login')
      }
    },
    children: [
      { 
        path: ('course-manage'),
        component: () => import('../views/admin/CourseManage.vue') 
      },
    ]
  },
  { 
    path: '/:unknownPath(.*)*', 
    component: () => import('../views/common/NotFound.vue') 
  }
  
];
//路由创建
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});
//路由导出
export default router;
