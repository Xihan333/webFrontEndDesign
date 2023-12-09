<!-- 查看成绩 -->
<template>
    <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="courseNum" label="课序号" width="auto" />
        <el-table-column prop="courseName" label="课程名称" width="auto" />
        <el-table-column prop="credit" label="学分" width="auto" />
        <el-table-column prop="commonMark" label="过程评价" width="auto" />
        <el-table-column prop="finalMark" label="期末成绩" width="auto" />
        <el-table-column prop="totalMark" label="总成绩" width="auto" />
    </el-table>
</template>

<script setup>
// 在Menu.vue中点击获取成绩数据, 通过pinia实现组件间数据传送
import { useStudentStore, useCommonStore } from '~/stores/app'
import { storeToRefs } from "pinia";// 保证其响应性
const studentStore=useStudentStore();
const commonStore=useCommonStore();
const tableData=studentStore.scoreList;
console.log(tableData)
// async()=>{
//     // 📌获取成绩数据
//     commonStore.updateLoading(true);
//     const res = await request.post('/api/score/getScoreList',{
//         data:{
//             achievementId: rowData.achievementId
//         } 
//     })
//     console.log(res)
//     if(res.data.code==200){

//         commonStore.updateLoading(false);
//     }
//     else{
//         commonStore.updateLoading(false);
//         ElMessage({
//             message: '加载失败，请重试！',
//             type: 'error',
//             offset: 150
//         })
//     }
// }
</script>
