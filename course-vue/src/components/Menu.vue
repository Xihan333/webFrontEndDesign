<template>
    <el-menu
        default-active="homepage"
        class="menu"
        router
        active-text-color="#6FB6C1"
        unique-opened="true"
    >
        <template v-for="item in itemList" :key="item.name">
            <el-menu-item v-if="item.role==role||item.role=='ALL'" :index="item.path" @click="openTab(item)">
                <el-icon><UserFilled /></el-icon>
                <span>{{item.name}}</span>
            </el-menu-item>
        </template>
        <template v-for="menu in menuList" :key="menu.name">
            <el-sub-menu v-if="menu.role==role||menu.role=='ALL'" :index="menu.index">
            <template #title>  
                <el-icon><Menu /></el-icon>
                <span>{{ menu.name }}</span>  
            </template>
            <el-menu-item 
                v-for="item in menu.item"
                :index="item.path"
                :key="item.name"  
                @click="openTab(item)"            
            >
                {{ item.name }}
            </el-menu-item>
        </el-sub-menu>
        </template>
    </el-menu>
</template>
  
<style lang="scss" scoped>
.menu{
    border-radius: 5px;
    height: 100%;
    overflow: auto;
}
.el-menu-item:hover{
    background-color: #6FB6C1;
    color: white;
}
</style>

<script setup>
import { useCommonStore } from "~/stores/app"

const userInfo=window.JSON.parse(localStorage.getItem('KEY_COMMON')).userInfo;
const role=userInfo.roles;
const store=useCommonStore();
const itemList=store.itemList;
const menuList=store.menuList;
const openTab=(item)=>{
    store.addTab(item);
}
const handleOpen = (key, keyPath) => {
    console.log(key, keyPath)
}
const handleClose = (key, keyPath) => {
    console.log(key, keyPath)
}
</script>

  