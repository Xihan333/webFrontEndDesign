<!-- 查看成绩 -->
<template>
    <el-table :data="tableData" border>
        <el-table-column prop="courseNum" label="课序号" width="auto" />
        <el-table-column prop="courseName" label="课程名称" width="auto" />
        <el-table-column prop="credit" label="学分" width="auto" />
        <el-table-column prop="commonMark" label="过程评价" width="auto" />
        <el-table-column prop="finalMark" label="期末成绩" width="auto" />
        <el-table-column :formatter="markFormat" label="总成绩" width="auto" />
        <el-table-column label="排名" width="auto" >
            <template #default="scope">
                <div class="rank"  v-if="scope.row.showRank">
                    <p id="rankText">{{ scope.row.rank }}</p>
                </div>
                <div class="rank" v-else>
                    <el-button size="default" @click="checkRank(scope.row)">
                        查看排名
                    </el-button>
                </div>
            </template>
        </el-table-column>
    </el-table>
</template>

<script setup>
// 在Menu.vue中点击获取成绩数据, 通过pinia实现组件间数据传送
import { useStudentStore, useCommonStore } from '~/stores/app'
import { storeToRefs } from "pinia";// 保证其响应性
import { onMounted, ref } from 'vue';
import request from '../../request/axios_config.js'
import { ElMessage } from 'element-plus';

const studentStore=useStudentStore();
const commonStore=useCommonStore();
let tableData=ref([]);
const userInfo=commonStore.userInfo;
onMounted(async()=>{
    const res = await request.get('/score/getMyCourseScores')
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

function markFormat(row){
    if(row.isResult==0)
        return '暂无成绩';
    return row.commonMark+row.finalMark;
}

//查看排名
function checkRank(row){
    row.showRank=true;
    row.rank=10;
}
</script>

<style lang="scss" scoped>
.rank{
    height: 34px;
    #rankText{
        box-sizing: border-box;
        margin: 0px;
    }
}
</style>
