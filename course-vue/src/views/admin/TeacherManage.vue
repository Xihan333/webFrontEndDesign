<!-- 教师管理 -->
<template>
    <div class="teacher-manage">
        <div>
            <TeacherManageDialog v-model:show="show" :rowData="currentRowData" :dialogMode="dialogMode" @updateTable="updateTableData" />
            <el-button @click="handleAdd" color="#6FB6C1" class="add">新 增</el-button>
            <div class="query">
                <el-input
                    clearable
                    class="search"
                    v-model="inputSearch"
                    placeholder="搜索姓名或工号..."
                    @keyup.enter="searchFn"
                />
            <el-button type="primary" @click="searchFn" color="#6FB6C1" class="searchBtn">查 询</el-button>
            </div>
        </div>
        <el-table border
            :data="paginatedTableData"
            style="width: 100%">
            <el-table-column prop="name" label="姓名" width="180" />
            <el-table-column prop="num" label="工号" width="180" />
            <el-table-column prop="genderName" label="性别" width="150"/>
            <el-table-column prop="card" label="身份证号" width="200" />
            <el-table-column prop="degree" label="学位" width="100" />
            <el-table-column prop="birthday" label="出生日期" width="150"/>
            <el-table-column prop="phone" label="电话号码" width="120"/>
            <el-table-column prop="email" label="电子邮箱" width="210" />
            <el-table-column label="操作" width="200" >
                <template #default="scope">
                    <el-button size="default" @click="handleEdit(scope.row)">
                        编辑
                    </el-button>
                    <el-button size="default" @click="handleDel(scope.row)">
                    删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-row class="pagination">
            <el-col>
                <el-pagination
                background
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[15, 25,35, 50]"
                :pager-count="7"
                layout="total, sizes, prev, pager, next, jumper"
                :total="searchedTableData.length"
                @size-change="handleSizeChange"
                />
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'

import TeacherManageDialog from '../../components/TeacherManageDialog.vue'
import request from '../../request/axios_config.js'
import {ElMessage} from 'element-plus'

const tableData = ref([])
onMounted(() => {
    updateTableData()
})
const updateTableData = async () => {
    const res = await request.post('/teacher/getTeacherList',{
        data:{
            numName:''
        }
    } 
    )
    console.log(res.data)
    tableData.value = res.data.data
}

const show = ref(false)
const search = ref('')
const inputSearch = ref('')
const searchedTableData = computed(() => tableData.value.filter(
    item =>
    !search.value ||
    item.num.toLowerCase().includes(search.value.toLowerCase())||
    item.name.toLowerCase().includes(search.value.toLowerCase())
))
const searchFn = () => {
    search.value = inputSearch.value
}

//分页相关
const currentPage = ref(1)
const pageSize = ref(15)
const paginatedTableData = computed(() => searchedTableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const handleSizeChange = () => {
  //当前页面默认为1
  currentPage.value = 1
}
//操作
const currentRowData = ref({})
const dialogMode = ref('')
const handleAdd = () => {
    currentRowData.value = {}
    dialogMode.value = 'add'
    show.value = true
} 
//编辑
const handleEdit = (rowData) => {
    console.log(rowData)
    currentRowData.value = rowData
    console.log(currentRowData.value)
    dialogMode.value = 'view'
    show.value = true
}

async function handleDel(rowData) {
    const res = await request.post('/teacher/teacherDelete',{
        data:{
            teacherId :rowData.teacherId
        }
    })
    console.log(res.code)
    console.log(res)
    updateTableData()
    if(res.data.code==200){
        ElMessage({
            message:'删除成功！',
            type:'success',
            offset:150
        })
    }
    else{
        ElMessage({
            message:'删除失败，请重试！',
            type:'error',
            offset:150
        })        
    }
}
</script>

<style lang="scss" scoped>
el-table{
    text-align: center;
}
.add{
    width: 70px;
    height: 35px;
    margin-bottom: 5px;
    color: white;
    font-weight: bold;
}

.query{
    float: right;
    border-right: 0px;
    .search{
        border-color: #6FB6C1;
        margin-left: auto;
        margin-right: 10px;
        width: 200px;
    }
    .searchBtn{
        width: 70px;
        margin-right: 10px;
        color: white;
    }
}
.pagination{
    margin-top: 10px;
}
</style>