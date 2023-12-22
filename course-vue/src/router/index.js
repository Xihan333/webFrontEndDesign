// Composables
import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";

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
  //忘记密码
  {
    path: "/forget-password",
    name: "forget-password",
    component: () => import("~/views/forgetPassword.vue"),
  },
  //学生端
  {
    path: ('/student'),
    component: () => import('../views/Layout.vue'),
    redirect: ('/student/homepage'),
    children: [
      // 主页
      { 
        path: ('homepage'), 
        component: () => import('../views/student/Homepage.vue') 
      },
      { 
        path: ('other-homepage'), 
        name: 'other-homepage',
        component: () => import('../views/student/OtherHome.vue') 
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
        path: ('peer-class'), 
        component: () => import('../views/student/PeerClass.vue') 
      },
      { 
        path: ('my-assess'), 
        component: () => import('../views/student/MyAssess.vue') 
      },
      { 
        path: ('assess-history'), 
        component: () => import('../views/student/AssessHistory.vue') 
      },
      { 
        path: ('all-blog'), 
        component: () => import('../views/student/AllBlog.vue') 
      },
      { 
        path: ('my-blog'), 
        component: () => import('../views/student/MyBlog.vue') 
      },
      { 
        path: ('new-blog'), 
        component: () => import('../views/student/NewBlog.vue') 
      },
      { 
        path: ('blog-info'), 
        component: () => import('../views/student/BlogInfo.vue') 
      },
      { 
        path: ('blog-edit'), 
        component: () => import('../views/student/EditBlog.vue') 
      },
    ]
  },
  {
    path: ('/teacher'),
    component: () => import('../views/Layout.vue'),
    redirect: ('/teacher/homepage'),    
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
      { 
        path: ('dissertation'),
        component: () => import('../views/teacher/Dissertation.vue') 
      },
    ]
  },
  {
    path: ('/admin'),
    component: () => import('../views/Layout.vue'),
    redirect: ('/admin/subject-manage'),
    children: [
      { 
        path: ('subject-manage'),
        component: () => import('../views/admin/SubjectManage.vue') 
      },
      { 
        path: ('course-manage'),
        component: () => import('../views/admin/CourseManage.vue') 
      },
      { 
        path: ('student-manage'),
        component: () => import('../views/admin/StudentManage.vue') 
      },
      { 
        path: ('teacher-manage'),
        component: () => import('../views/admin/TeacherManage.vue') 
      },
      { 
        path: ('class-manage'),
        component: () => import('../views/admin/ClassManage.vue') 
      },
      { 
        path: ('social-audit'),
        component: () => import('../views/admin/SocialAudit.vue') 
      },
      { 
        path: ('achievement-audit'),
        component: () => import('../views/admin/AchievementAudit.vue') 
      },
      { 
        path: ('peer-audit'),
        component: () => import('../views/admin/PeerAudit.vue') 
      },
      { 
        path: ('classmate'),
        component: () => import('../views/admin/Classmate.vue') 
      },
      { 
        path: ('without-class'),
        component: () => import('../views/admin/WithoutClass.vue') 
      },
      { 
        path: ('password-edit'),
        component: () => import('../views/common/PasswordEdit.vue'),
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
//判断是否登录
router.beforeEach(async (to, from) => {
  if(localStorage.getItem('KEY_COMMON') == null && to.name != 'login'){
    ElMessage({
      message: '请先登录！',
      type: 'error',
      offset: 150
    })
    return { name: 'login' }
  }
})
//路由导出
export default router;
