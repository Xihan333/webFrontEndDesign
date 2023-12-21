<!-- 选课系统 -->
<template>   
    <div class="search">
        <div class="select">
            <div>
                课程名/课序号
                <el-input
                    class="firstInput"
                    v-model="courseNameOrNum"
                    placeholder="请输入"
                />
            </div>
            <div>
                上课教师
                <el-input
                    class="secondInput"
                    v-model="teacherName"
                    placeholder="请输入"
                />
            </div>
            <div>
                课程类型
                <el-select class="thirdInput" v-model="type" placeholder="请选择">
                <el-option
                    v-for="(item,index) in types"
                    :key="item"
                    :label="item"
                    :value="index"
                />
                </el-select>
            </div>
            <div>
                节次
                <el-select class="forthInput" v-model="timeOrder" placeholder="请选择">
                <el-option
                    v-for="(item,index) in timeOrders"
                    :key="item"
                    :label="item"
                    :value="index"
                />
                </el-select>
            </div>
        </div>
        <div>
            <el-button type="primary" plain @click="reset">重置</el-button>
            <el-button type="primary" @click="search">查询</el-button>
        </div>
    </div>
    <el-table border :data="filterTableData">
        <el-table-column prop="campusName" label="开设单位" width="auto" />   
        <el-table-column prop="courseName" label="课程名称" width="auto" /> 
        <el-table-column prop="courseNum" label="课序号" width="auto" /> 
        <el-table-column prop="teacherName" label="上课教师" width="auto" />
        <el-table-column prop="hour" label="学时" width="auto" />  
        <el-table-column prop="credit" label="学分" width="auto" /> 
        <el-table-column :formatter="typeFormat" label="课程类型" width="auto" />
        <el-table-column :formatter="timeFormat" label="上课时间" width="auto" />
        <el-table-column prop="place" label="上课地点" width="auto" />  
        <el-table-column prop="courseCapacity" label="课容量" width="auto" />  
        <el-table-column prop="selectedCount" label="选课人数" width="auto" />  
        <el-table-column label="操作" width="auto" >
            <template #default="scope">
                <el-button v-if="isSelected(scope.row)" size="default" @click="select(scope.row)">
                    选课
                </el-button>
                <el-button v-else size="default" type="danger" plain @click="drop(scope.row)">
                    退课
                </el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-row class="pagination">
        <el-col>
        <el-pagination
            background
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[15, 25, 35, 50]"
            :pager-count="7"
            layout="total, sizes, prev, pager, next, jumper"
            :total="tableData.length"
            @size-change="handleSizeChange"
        />
        </el-col>
    </el-row>
</template>
    
<script setup>
import { ref,computed,onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../request/axios_config.js'

const courseNameOrNum=ref("");
const teacherName=ref("");
const timeOrder=ref(0);
let timeOrders=['全部','第一节','第二节','第三节','第四节','第五节']
const type=ref(3);
let types=['必修','限选','任选','全部']
let tableData = []
const filterTableData=ref([]);
let selectedCourses=ref([]);//获取我已选的课程
const days=['请选择','星期一','星期二','星期三','星期四','星期五','星期六','星期日']

onMounted(async() => {
    updateAllData();
})

async function updateAllData(){
    let res = await request.get('/course/getMyCourses')
    if(res.data.code==200){
        selectedCourses.value=res.data.data;
        res = await request.get('/course/getMyAccessCourses')
        if(res.data.code==200){
            tableData=res.data.data;
            filterTableData.value=tableData;
        }
        else{
            ElMessage({
                message: '加载失败，请重试！',
                type: 'error',
                offset: 150
            })
        }
    }
    else{
        ElMessage({
            message: '加载失败，请重试！',
            type: 'error',
            offset: 150
        })
    }
}

function reset(){
    courseNameOrNum.value="";
    teacherName.value="";
    type.value=3;
    timeOrder.value=0;
}

function search(){
    // 筛选, 非模糊匹配
    filterTableData.value = tableData.filter((item) => {
        return (courseNameOrNum.value==""||(item.courseName==courseNameOrNum.value||item.courseNum==courseNameOrNum.value))
                &&(teacherName.value==""||item.teacherName==teacherName.value)
                &&(timeOrder.value==0||item.timeOrder==timeOrder.value)
                &&(type.value==3||item.type==type.value);
    });
}

function typeFormat(row, column) {
    return types[row.type];
}

function timeFormat(row, column){
  return days[row.day]+timeOrders[row.timeOrder];
}

function isSelected(row){
    let x=true;
    for(let i=0;i<selectedCourses.value.length;i++){
        if(row.teacherCourseId==selectedCourses.value[i].teacherCourseId){
            x=false;
            break;
        }
    }
    return x;
}

async function select(row){
    const res = await request.post('/course/selectCourse',{
        data:{
            teacherId:row.teacherId,
            courseId:row.courseId
        }
    })
    if(res.data.code==200){
        ElMessage({
            message: '选课成功！',
            type: 'success',
            offset: 150
        })    
        updateAllData();
    }
    else{
        ElMessage({
            message: res.data.msg,
            type: 'error',
            offset: 150
        })
    }
}

async function drop(row){
    const res = await request.post('/course/cancelCourse',{
        data:{
            teacherId:row.teacherId,
            courseId:row.courseId
        }
    })
    if(res.data.code==200){
        ElMessage({
            message: '退课成功！',
            type: 'success',
            offset: 150
        })   
        updateAllData(); 
    }
    else{
        ElMessage({
            message: res.data.msg,
            type: 'error',
            offset: 150
        })
    }
}

// 分页相关,不需要修改,直接复制
// 当前页数,默认设置为1
const currentPage = ref(1)
const pageSize = ref(15)
const paginatedTableData = computed(() => searchedTableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const handleSizeChange = () => {
    currentPage.value = 1
}
</script>
    
<style lang="scss" scoped>
.search{
    display: flex;
    justify-content: space-between;
    margin-bottom: 6px;
    width: 100%;
    .select{
        display: flex;
        gap: 5px 20px;
        .firstInput{
            width: 150px;
        }
        .secondInput{
            width: 100px;
        }
        .thirdInput{
            width: 90px;
        }
        .forthInput{
            width: 100px;
        }
    }
}
.pagination{
    margin-top: 10px;
}
</style>