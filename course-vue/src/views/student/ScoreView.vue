<!-- 查看成绩 -->
<template>
    <el-table :data="tableData" border>
        <el-table-column prop="courseNum" label="课序号" width="auto" />
        <el-table-column prop="courseName" label="课程名称" width="auto" />
        <el-table-column prop="credit" label="学分" width="auto" />
        <el-table-column prop="commonMark" label="过程评价" width="auto" />
        <el-table-column prop="finalMark" label="期末成绩" width="auto" />
        <el-table-column :formatter="markFormat" label="总成绩" width="auto" />
    </el-table>
</template>

<script setup>
// 在Menu.vue中点击获取成绩数据, 通过pinia实现组件间数据传送
import { useStudentStore, useCommonStore } from '~/stores/app'
import { storeToRefs } from "pinia";// 保证其响应性
import { onMounted, ref } from 'vue';
import request from '../../request/axios_config.js'

const studentStore=useStudentStore();
const commonStore=useCommonStore();
let tableData=ref([]);
const userInfo=commonStore.userInfo;
onMounted(async()=>{
    //📌获取不到数据
    commonStore.updateLoading(true);
    const res = await request.get('/score/getMyCourseScores')
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

function markFormat(row, column){
    return row.commonMark+row.finalMark;
}
</script>
