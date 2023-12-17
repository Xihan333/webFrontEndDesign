<!-- 学生互评我的评价界面 -->
<template>
    <div class="my-assess">
        <div>
        <MyAssessDialog v-model:show="show" :rowData="currentRowData" :dialogMode="dialogMode" @updateTable="updateTableData" />
        <!-- 表格顶部按键 -->
        <div>
            <el-button class="seeBtn" color="#6FB6C1" @click="seePeer">查看我的同学</el-button>
            <el-button class="seeBtn" color="#6FB6C1" @click="seeHistory">查看历史评论</el-button>
        </div>
        </div>
        <!-- 表格部分 -->
        <el-table
            :data="paginatedTableData"
            style="width: 100%;">
            <el-table-column prop="evalTime" label="评价时间" width="200"/>
            <el-table-column prop="eval" label="评价内容" width="300"/>
            <el-table-column label="操作" width="150">
                <template #default="scope">
                    <el-button size="default" @click="handleEdit(scope.row)">
                        查看 </el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-row class="pagination">
            <el-col>
                <el-pagination
                    background
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[15, 25, 35, 50]"
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
import {ref, computed,onMounted} from 'vue'
import router from "~/router";
import MyAssessDialog from '../../components/MyAssessDialog.vue'
import {ElMessage} from 'element-plus'
import request from '../../request/axios_config.js'

const tableData = ref([])
onMounted(() => {
    updateTableData()
})

const updateTableData = async () => {
    const res = await request.get('/evaluation/getMyEvaluationList')
    console.log(res.data.data)
    tableData.value = res.data.data 
}

const show = ref(false)

const search = ref('')
const inputSearch = ref('')

const searchedTableData = computed(() => tableData.value.filter(
    item => !search.value ||
    item.evalTime.toLowerCase().includes(search.value.toLowerCase())
))

const searchFn = () => {
    search.value = inputSearch.value
}


const currentPage = ref(1)
const pageSize = ref(15)
const paginatedTableData = computed(() => searchedTableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const handleSizeChange = () => {
  currentPage.value = 1
}

const currentRowData = ref({})
const dialogMode = ref('')

const handleEdit = (rowData) => {
  console.log(rowData)
  currentRowData.value = rowData
  dialogMode.value = 'view'
  show.value = true
}

// 切换页面
const seePeer = () =>{
    router.push('peer-class')
}
const seeHistory = () =>{
    router.push('assess-history')
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