<template>
    <div class="layout">
        <el-container>
            <el-header class="header">

            </el-header>
            <el-container class="middle">
                <el-aside class="side">
                    <Menu/>
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
                        <el-scrollbar>
                            <router-view/>  
                        </el-scrollbar>
                    </el-tab-pane>
                    </el-tabs>
                </el-main>
            </el-container>
            <el-footer @click="kk" class="footer">
                FOOT
            </el-footer>
        </el-container>
    </div>
</template>
<style lang="scss" scoped>
.header{
    position: absolute;
    top: 0px;
    width: 100%;
    height: 60px;
    background-color: white;
    box-shadow: 0px 2px 6px rgb(213, 213, 213);
}
.side{
    margin: 6px 0px 5px 6px;
    width:220px;
}
.main {
    ::v-deep .el-tabs__header{
        margin: 0 0 10px;
    }
    ::v-deep .base_form{
        margin-left: 0px;
    }
} 
.tapPane{

}
.middle{
    position: absolute;
    top: 60px;
    bottom: 50px;
    width: 100%;
}
.footer{
    position: absolute;
    bottom: 0px;
    width: 100%;
    height: 50px;
    background-color: rgb(255, 255, 255);
}
</style>
<script setup>
import router from "~/router";
import { useCommonStore } from "~/stores/app";
import Menu from '../components/Menu.vue'
import { ref } from "vue";
import { storeToRefs } from "pinia";// 保证其响应性

const role=1;
const store=useCommonStore();
const {selectedTab:tabValue,tabPaneList} = storeToRefs(store);
function clickTab(tab){
    var name=JSON.stringify(tab.paneName).replace('"','').replace('"','')//对tab参数处理，以获得当前点击的标签页的路由
    store.changeTab(name);
    if(role==0){
        name="/admin/"+name;
    }
    else if(role==1){
        name="/student/"+name;
    }
    else if(role==2){
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
    if(role==0){
        name="/admin/"+tabValue.value;
    }
    else if(role==1){
        name="/student/"+tabValue.value;
    }
    else if(role==2){
        name="/teacher/"+tabValue.value;
    }
    router.push(name)//路由跳转以实现切换界面
}

</script>

