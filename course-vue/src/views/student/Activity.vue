<!-- 课外活动 -->
<template>
<div class = "activity">
    <div>
        <ActivityDialog v-model:show="show" :rowData="currentRowData" :dialogMode="dialogMode" @updateTable="updateTableData" />
        <el-button @click="handleAdd" color="#6FB6C1" class="add">新 增</el-button>
        <div class="query">
            <el-input
                clearable
                class="search"
                v-model="inputSearch"
                placeholder="搜索活动日期或主题..."
                @keyup.enter="searchFn"
            />
        <el-button type="primary" @click="searchFn" color="#6FB6C1" class="searchBtn">查 询</el-button>
        </div>
    </div>
    <el-table
       :data="paginatedTableData"
       style="width: 100%">
       <el-table-column prop="title" label="活动主题" width="180" />
       <el-table-column prop="day" label="活动日期" width="120" />
       <el-table-column prop="location" label="活动地点" width="150"/>
       <el-table-column prop="introduction" label="活动介绍" width="180" />
       <el-table-column label="操作" width="180" >
            <template #default="scope">
                <el-button size="default" @click="handleEdit(scope.row)">
                 查看
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
import {ref,computed,onMounted} from 'vue'
//引入弹窗界面
import ActivityDialog from '../../components/ActivityDialog.vue'
import{ElMessage} from 'element-plus'
import request from '../../request/axios_config.ts'

const tableData = ref([])
onMounted(() => {
    updateTableData()
})
const updateTableData = async () => {
    //这里有点迷糊，不太确定接口
    const res = await request.post('/api/activity/getStudentActivity',{
        data:{}
    })
    console.log(res.data)
    tableData.value = res.data.data
}
//显示弹窗
const show = ref(false)
//搜索框查询后的内容
const search = ref('')
const inputSearch = ref('')
//根据搜索框内容进行搜索，空参搜索显示所有
const searchedTableData = computed(() => tableData.value.filter(
    item =>
    //空参
    !search.value ||
    //按要求搜索
    item.title.toLowerCase().includes(search.value.toLowerCase()) ||
    //有疑问，日期搜索和文字一样吗
    item.day.toLowerCase().includes(search.value.toLowerCase())
))
const searchFn = () => {
    //点击按钮进行查询
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
//查看
const handleEdit = (rowData) => {
    console.log(rowData)
    currentRowData.value = rowData
    dialogMode.value = 'view'
    show.value = true
}

async function handleDel(rowData) {
    const res = await request.post('/api/activity/ActivityDelete',{
        data:{
            activityId :rowData.activityId
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

<!-- 看看这一块是干啥的呀 -->
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