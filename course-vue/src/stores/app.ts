import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const userInfo={
    jwtToken:''
  }
  const tabPaneList = ref([
    {
      title:'个人主页',
      name:'homepage',
      close:false
    }
  ])
  //查看博客显示的信息
  const blogInfo = ref({
      createTime: "2023-12-17 01:38:51",
      num: "2022030003",
      BlogId: 5,
      name: "杨平",
      BlogTag: "Sample Tag",
      updateTime: null,
      personId: 2,
      BlogTitle: "Sample Title",
      content: "Sample Content"
  })
  const classmate = ref({
    clazzId:"",
  })
  return {
    tabPaneList,userInfo,blogInfo,classmate
  }
})

export const useStudentStore = defineStore('student', () => {
  let scoreList:object=[
    {
      "studentName":'',
      "gradeName":'',
      "courseNum":'000',
      "courseName":'高等数学',
      "credit":4,
      "commonMark":90,
      "finalMark":90,
      "isResult":1
    }
  ] 
  function updateScoreList(newValue:any){
    scoreList=newValue;
  }
  return {
    scoreList,updateScoreList
  }
})



// ☯️采用组合式写法
export const useCommonStore = defineStore('common', () => {
  // 用户信息
  let userInfo=ref({
    'accessToken': "",
    'id': 0,
    'roles': "",
    'tokenType': "",
    'username': ""
  });
  // 是否加载中
  const loading=ref(false);
  // 当前选中的标签页
  const selectedTab=ref('homepage')
  // 已打开的标签页列表, 默认是首页
  const tabPaneList = ref([
    {
      title:'个人主页',
      name:'homepage',
      close:false
    }
  ])
  //没有子菜单的菜单
  const itemList = ref([
    {
      name:'个人主页',
      path:'homepage',
      role:'ROLE_STUDENT'
    },
    {
      name:'个人主页',
      path:'homepage',
      role:'ROLE_TEACHER'
    },
    {
      name:'学科管理',
      path:'subject-manage',
      role:'ROLE_ADMIN'
    },
    {
      name:'课程管理',
      path:'course-manage',
      role:'ROLE_ADMIN'
    },
  ])
  const menuList = ref([
    {
      name:'课程中心',
      index:'0',
      role:'ROLE_STUDENT',
      item:[
        {
          name:'选课系统',
          path:'course-select',
        },
        {
          name:'查看课表',
          path:'course-view',
        },
        {
          name:'查看成绩',
          path:'score-view',
        },
      ]
    },
    {
      name:'学工管理',
      index:'1',
      role:'ROLE_STUDENT',
      item:[
        {
          name:'社会实践',
          path:'social-prac',
        },
        {
          name:'课外活动',
          path:'activity',
        },
        {
          name:'成果奖励',
          path:'achievement',
        },
        {
          name:'学生互评',
          path:'peer-class',
        },
      ]
    },
    {
      name:'博客系统',
      index:'2',
      role:'ROLE_STUDENT',
      item:[
        {
          name:'所有文章',
          path:'all-blog',
        },
        {
          name:'我的文章',
          path:'my-blog',
        },
        {
          name:'发布新文章',
          path:'new-blog',
        },
      ]
    },
    {
      name:'账号安全',
      index:'3',
      role:'ROLE_STUDENT',
      item:[
        {
          name:'修改个人信息',
          path:'info-edit',
        },
        {
          name:'修改密码',
          path:'password-edit',
        },
      ]
    },
    {
      name:'课程中心',
      index:'0',
      role:'ROLE_TEACHER',
      item:[
        {
          name:'查看课程',
          path:'course-view',
        },
        {
          name:'成绩管理',
          path:'score-manage',
        },
      ]
    }, 
    {
      name:'论文管理',
      index:'1',
      role:'ROLE_TEACHER',
      item:[
        {
          name:'我的论文',
          path:'dissertation',
        },
      ]
    }, 
    {
      name:'账号安全',
      index:'2',
      role:'ROLE_TEACHER',
      item:[
        {
          name:'修改个人信息',
          path:'info-edit',
        },
        {
          name:'修改密码',
          path:'password-edit',
        },
      ]
    },
    {
      name:'人员管理',
      index:'0',
      role:'ROLE_ADMIN',
      item:[
        {
          name:'学生管理',
          path:'student-manage',
        },
        {
          name:'教师管理',
          path:'teacher-manage',
        },
        {
          name:'班级管理',
          path:'class-manage',
        },
      ]
    },
    {
      name:'学工管理',
      index:'1',
      role:'ROLE_ADMIN',
      item:[
        {
          name:'社会实践',
          path:'social-audit',
        },
        {
          name:'成果奖励',
          path:'achievement-audit',
        },
        {
          name:'互评管理',
          path:'peer-audit',
        },
      ]
    },
    {
      name:'账号安全',
      index:'2',
      role:'ROLE_ADMIN',
      item:[
        {
          name:'修改密码',
          path:'password-edit',
        },
      ]
    },
  ])
  function addTab(item:any){//增加标签页方法
    //查找此界面是否已打开
    if(tabPaneList.value.findIndex(e=>e.name==item.path)===-1){
      tabPaneList.value.push({//添加当前标签页
        title: item.name,
        name: item.path,
        close:true//使除了首页标签页外其他都可关闭
      })
    }
    selectedTab.value=item.path;
  }
  function changeTab(item:any){//切换标签页方法
       //将需打开标签页路由赋予editableTabsvalue
       selectedTab.value=item.path;
  }
  function updateSelectedTab(name:string){//更新当前标签页
    selectedTab.value=name;
  }
  function updateTabList(name:string){//更新标签页列表
    tabPaneList.value=tabPaneList.value.filter(tab=>tab.name!==name);
  }
  function updateLoading(newValue:boolean){
    loading.value=newValue;
  }
  function setUserInfo(newValue:any){
    userInfo.value=newValue;
  }
  function adminInitTab(title:string,name:string){
    selectedTab.value=name;
    tabPaneList.value=[{
      'title':title,
      'name':name,
      'close':false
    }]
  }
  return {
    tabPaneList,itemList,menuList,selectedTab,addTab,changeTab,updateSelectedTab,updateTabList,
    loading,updateLoading,userInfo,setUserInfo,adminInitTab
  }
}, {
  persist: {
    key: id => 'KEY_' + id.toUpperCase(),
    storage: localStorage,
    // 设置持久化存储的白名单, 以下这些数据会被持久化存储到localStorage
    paths: ['userInfo']
  }
})
// // Utilities
// import { PiniaPluginContext, defineStore } from "pinia";
// import { userLoginReq } from "../services/userServ";
// import { type MenuInfo, type UserInfo, SystemConfig } from "../models/general";
// import { getMenuList } from "../services/mainServ";

// const defaultNaviList: MenuInfo[] = [];

// export const useAppStore = defineStore("app", {
//   state: () => ({
//     usernameSave: "ggg",
//     passwordSave: "",
//     remember: false,
//     userInfo: {
//       loggedIn: false,
//       username: "",
//       perName: "",
//       jwtToken: "",
//       id: 0,
//       roles: "",
//       password: "",
//     } as UserInfo,

//     systemConfig: {
//       naviList: defaultNaviList,
//       showLeftMeun: false,
//       leftList: defaultNaviList,
//       id: null,
//     } as SystemConfig,
//   }),
//   actions: {
//     async login(username: string, password: string): Promise<void> {
//       const res = await userLoginReq(username, password);
//       this.userInfo = {
//         loggedIn: true,
//         username: res.username,
//         perName: res.perName,
//         jwtToken: res.accessToken,
//         id: res.id,
//         roles: res.roles,
//         password: password,
//       };
//     },
//     logout() {
//       this.userInfo = {
//         loggedIn: false,
//         username: "",
//         perName: "",
//         jwtToken: "",
//         roles: "",
//         id: 0,
//         password: "",
//       };
//       this.systemConfig.naviList = [];
//     },
//     async setNavi() {
//       this.systemConfig.naviList = await getMenuList();
//       this.systemConfig.leftList = this.systemConfig.naviList[0].sList;
//       this.systemConfig.showLeftMeun = true;
//       this.systemConfig.id = this.systemConfig.naviList[0].id;
//     },
//     saveAccount(username: string, password: string) {
//       this.usernameSave = username;
//       this.passwordSave = password;
//       this.remember = true;
//     },
//     removeAccount() {
//       this.usernameSave = "";
//       this.passwordSave = "";
//       this.remember = false;
//     },
//     //选中模块菜单，设置左侧菜单数据
//     setLeftList(id: number) {
//       let i: number;
//       for (i = 0; i < this.systemConfig.naviList.length; i++) {
//         if (this.systemConfig.naviList[i].id === id) {
//           this.systemConfig.leftList = this.systemConfig.naviList[i].sList;
//           this.systemConfig.id = id;
//           break;
//         }
//       }
//     },
//   },
//   persist: {
//     storage: localStorage,
//     // debug: true,
//     // afterRestore: (context: PiniaPluginContext) => {
//     //   console.log(context);
//     // },
//   },
// });

// export default useAppStore