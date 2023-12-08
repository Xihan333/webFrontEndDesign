<template>
    <el-menu
        default-active="homepage"
        class="menu"
        router
        active-text-color="#6FB6C1"
        unique-opened="true"
    >
        <template v-for="item in itemList" :key="item.name">
            <el-menu-item v-if="item.role==role||item.role==3" :index="item.path">
                <el-icon><UserFilled /></el-icon>
                <span>{{item.name}}</span>
            </el-menu-item>
        </template>
        <template v-for="menu in menuList" :key="menu.name">
            <el-sub-menu v-if="menu.role==role||menu.role==3" :index="menu.index">
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
}
// .el-menu-item :focus{
//     background-color: #6FB6C1;
//     color: white;
// }
.el-menu-item:hover{
    background-color: #6FB6C1;
    color: white;
}
</style>

<script lang="ts" setup>
import { useCommonStore } from "~/stores/app"

const role=1;
const store=useCommonStore();
const itemList=store.itemList;
const menuList=store.menuList;
const openTab=(item:object)=>{
    store.addTab(item);
}
const handleOpen = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}
</script>

  