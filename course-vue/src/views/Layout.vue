<template>
    <el-container>
        <el-header class="header">
            <img src="../assets/img/logo.png" alt="校徽">
            <div class="logout">
                <p>欢迎回来，黄婉姗</p>
                <div title="退出">
                    <el-icon @click="logout" size="20px" id="logout"><Expand /></el-icon>
                </div>
            </div>
        </el-header>
        <el-container class="middle">
            <el-aside class="side">
                <div class="sideMenu">
                    <Menu/>
                </div>
                <div class="copyright">
                    <p>Copyright©</p>
                    <p>2023-1220 捍卫小狗</p> 
                </div>
            </el-aside>
            <el-main class="main">
                <el-tabs
                    class="tab"
                    type="card"
                    v-model="tabValue"
                    @tab-remove="removeTab"
                    @tab-click="clickTab"
                >
                <el-tab-pane class="tabPane"
                                v-for="item in tabPaneList" 						      
                                :closable="item.close" 
                                :key="item.name" 
                                :label="item.title" 
                                :name="item.name"
                >
                    <el-scrollbar v-loading="loading">
                        <router-view/>  
                    </el-scrollbar>
                </el-tab-pane>
                </el-tabs>
            </el-main>
        </el-container>
    </el-container>
</template>
<style lang="scss" scoped>
.header{
    position: absolute;
    top: 0px;
    width: 100%;
    height: 40px;
    background-color: white;
    box-shadow: 0px 2px 6px rgb(213, 213, 213);
    display: flex;
    justify-content: space-between;
    align-items: center;
    img{
        height: 94%;
    }
    .logout{
        display: flex;
        p{
            margin: 0px 10px 0px 0px;
            font-size: 13px;
            line-height: 20px;
        }
    }
    #logout:hover{
        color: #6FB6C1;
    }
}
.middle{
    position: absolute;
    top: 48px;
    bottom: 10px;
    width: 100%;
    border-radius: 5px;
    .side{
        position: absolute;
        width:220px;
        top: 0px;
        bottom: 0px;
        left: 6px;
        .sideMenu{
            position: absolute;
            top: 0px;
            bottom: 75px;
            width: 220px;
        }
        .copyright{
            padding: 15px;
            text-align: center;
            border-radius: 5px;
            position: absolute;
            bottom: 0px;
            width: 220px;
            margin-top: 10px;
            height: 65px;
            background: linear-gradient(#afe5e8,#6FB6C1,#3999a8);
            p{
                margin: 2px;
                width: 100%;
                color: white;
                font-size: 13px;
            }
        }
    }
    .main {
        position: absolute;
        top: 0px;
        bottom: 0px;
        left: 246px;
        right: 8px;
        :deep .base_form{
            margin-left: 0px;
        }
    } 
}
</style>
<script setup>
import router from "~/router";
import { useCommonStore } from "~/stores/app";
import Menu from '../components/Menu.vue'
import { ref } from "vue";
import { storeToRefs } from "pinia";// 保证其响应性

const userInfo=window.JSON.parse(localStorage.getItem('KEY_COMMON')).userInfo;
const role=userInfo.roles;
const store=useCommonStore();
const {selectedTab:tabValue,tabPaneList,loading} = storeToRefs(store);
function clickTab(tab){
    var name=JSON.stringify(tab.paneName).replace('"','').replace('"','')//对tab参数处理，以获得当前点击的标签页的路由
    store.changeTab(name);
    if(role=='ROLE_ADMIN'){
        name="/admin/"+name;
    }
    else if(role=='ROLE_STUDENT'){
        name="/student/"+name;
    }
    else if(role=='ROLE_TEACHER'){
        name="/teacher/"+name;
    }
    router.push(name)//路由跳转以实现切换界面
}
function removeTab(name) {
    console.log(name)
    if (tabValue.value === name) {
        tabPaneList.value.forEach((tab, index) => {
            if (tab.name === name) {
                let nextTab = tabPaneList.value[index + 1] || tabPaneList.value[index - 1];
                if (nextTab) {
                    store.updateSelectedTab(nextTab.name);
                }
            }
        });
    }
    store.updateTabList(name);
    if(role=='ROLE_ADMIN'){
        name="/admin/"+tabValue.value;
    }
    else if(role=='ROLE_STUDENT'){
        name="/student/"+tabValue.value;
    }
    else if(role=='ROLE_TEACHER'){
        name="/teacher/"+tabValue.value;
    }
    router.push(name)//路由跳转以实现切换界面
}
function logout(){

}
</script>

