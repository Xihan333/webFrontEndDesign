<!-- 查看课程 -->
<template>
    <el-table :data="tableData" border>
        <el-table-column prop="courseNum" label="课序号" width="auto" />
        <el-table-column prop="courseName" label="课程名称" width="auto" />
        <el-table-column prop="hour" label="学时" width="auto" />  
        <el-table-column prop="credit" label="学分" width="auto" />
        <el-table-column :formatter="typeFormat" label="课程类型" width="auto" />
        <el-table-column :formatter="timeFormat" label="上课时间" width="auto" />
        <el-table-column prop="place" label="上课地点" width="auto" />
        <el-table-column prop="courseCapacity" label="课容量" width="auto" />
        <el-table-column prop="selectedCount" label="选课人数" width="auto" />
    </el-table>
</template>

<script setup>
// 在Menu.vue中点击获取成绩数据, 通过pinia实现组件间数据传送
import { useCommonStore } from '~/stores/app'
import { storeToRefs } from "pinia";// 保证其响应性
import { onMounted, ref } from 'vue';
import request from '../../request/axios_config.js'

const commonStore=useCommonStore();
let tableData=ref([]);
const userInfo=commonStore.userInfo;
let types=['必修','限选','任选','']
let days=['','星期一','星期二','星期三','星期四','星期五','星期六','星期日']
let timeOrders=['','第一节','第二节','第三节','第四节','第五节']
onMounted(async()=>{
    const res = await request.get('/course/getMyTeacherCourses')
    console.log('请看请求',res)
    if(res.data.code==200){
        tableData.value=res.data.data;
    }
    else{
        ElMessage({
            message: '加载失败，请重试！',
            type: 'error',
            offset: 150
        })
    }
})

function typeFormat(row, column) {
    return types[row.type];
}

function timeFormat(row, column) {
    return days[row.day]+timeOrders[row.timeOrder];
}
</script>
