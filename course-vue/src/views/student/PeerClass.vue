<!-- 学生互评同一班级界面 -->
<template>
    <div class="peer-class">
        <div>
            <PeerClassDialog v-model:show="show" :rowData="currentRowData"  @updateTable="updateTableData" />            
            <!-- 表格顶部按键 -->
            <div>
                <el-button class="seeBtn" color="#6FB6C1" @click="seeHistory">查看历史评论</el-button>
                <el-button class="seeBtn" color="#6FB6C1" @click="seeMyAssess">查看我的评论</el-button>
                <!-- 查询部分 -->
                <div class="query">
                    <el-input
                        clearable
                        class="search"
                        v-model="inputSearch"
                        placeholder="搜索学号或姓名..."
                        @keyup.enter="searchFn"
                    />
                    <el-button type="primary" @click="searchFn" color="#6FB6C1" class="searchBtn">查 询</el-button>
                </div>
            </div>
        </div>
        <!-- 表格部分 -->
        <el-table
            :data="paginatedTableData"
            style="width: 100%">
            <el-table-column prop="name" label="姓名" width="280" />
            <el-table-column prop="num" label="学号" width="320" />
            <el-table-column prop="genderName" label="性别" width="200"/>
            <el-table-column prop="phone" label="电话号码" width="370" />
            <el-table-column label="操作" width="300" >
            <template #default="scope">
                <el-button size="default" @click="handleEdit(scope.row)">
                评价
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
import{ref, computed, onMounted} from 'vue'
import router from "~/router";
import PeerClassDialog from '../../components/PeerClassDialog.vue'
import request from '../../request/axios_config.js'
import {ElMessage} from 'element-plus'

const tableData = ref([])
onMounted(() => {
  // 发起请求获取当前表格数据
  updateTableData()
})

const updateTableData = async () => {
    const res = await request.post('/student/getMyClassStudents',{})
    console.log(res.data)
    tableData.value = res.data.data
}

//弹窗
const show = ref(false)

//搜索
const search = ref('')
const inputSearch = ref('')

const searchedTableData = computed(() => tableData.value.filter(
    item =>
        !search.value ||
        item.num.toLowerCase().includes(search.value.toLowerCase())||
        item.name.toLowerCase().includes(search.value.toLowerCase())
))

const searchFn = () =>{
    search.value = inputSearch.value
}
//分页相关
const currentPage = ref(1)
const pageSize = ref(15)
const paginatedTableData = computed(() => searchedTableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const handleSizeChange = () => {
  currentPage.value = 1
}

// 操作相关
const currentRowData = ref({})
const handleEdit = (rowData) => {
console.log(rowData)
  currentRowData.value = rowData
  show.value = true
}

// 切换页面
const seeHistory = () =>{
    router.push('assess-history')
}
const seeMyAssess = () =>{
    router.push('my-assess')
}


</script>

<style lang="scss" scoped>
el-table{
    text-align: center;
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

.seeBtn{
    width: 150px;
    color: white;
    margin-bottom: 10px;
}
</style>