<!-- 互评管理 -->
<template>
    <div class="peer-audit">
        <div>
        <PeerAduitDialog v-model:show="show" :rowData="currentRowData" :dialogMode="dialogMode" @updateTable="updateTableData" />
        <!-- 查询部分 -->
        <div class="query">
            <el-input
                clearable
                class="search"
                v-model="inputSearch"
                placeholder="搜索姓名或学号..."
                @keyup.enter="searchFn"
            />
        <el-button type="primary" @click="searchFn" color="#6FB6C1" class="searchBtn">查 询</el-button>
        </div>
    </div>
    <el-table
        :data="paginatedTableData"
        style="width: 100%">
            <el-table-column prop="evaluator.person.num" label="评价人学号" width="200" />
            <el-table-column prop="evaluator.person.name" label="评价人姓名" width="250" />
            <el-table-column prop="student.person.num" label="被评价人学号" width="200"/>
            <el-table-column prop="student.person.name" label="被评论者" width="250" />
            <el-table-column prop="eval" label="评论内容" width="300" />
            <el-table-column prop="evalTime" label="评论时间" width="250" />
            <el-table-column label="操作" width="200" >
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
import { ref,computed,onMounted } from 'vue'
//引入弹窗页面
import PeerAduitDialog from '../../components/PeerAduitDialog.vue'
import { ElMessage } from 'element-plus'
import request from '../../request/axios_config.js'

const tableData = ref([{}])
onMounted(() => {
  // 发起请求获取当前表格数据
  updateTableData()
})
const updateTableData = async () => {
  const res = await request.get('/evaluation/getEvaluationList')
  console.log(res.data.data)
  tableData.value = res.data.data
}

//弹窗
const show = ref(false)
//搜索内容
const search = ref('')
const inputSearch = ref('')
const searchedTableData = computed(() => tableData.value.filter(
    item =>
        !search.value ||
        item.student.person.num.toLowerCase().includes(search.value.toLowerCase()) ||
        item.student.person.name.toLowerCase().includes(search.value.toLowerCase()) ||
        item.evaluator.person.num.toLowerCase().includes(search.value.toLowerCase()) ||
        item.evaluator.person.name.toLowerCase().includes(search.value.toLowerCase())
))

const searchFn = () => {
  // 点击查询按钮后才开始查询
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

async function handleDel(rowData)  {
  const res = await request.post('/evaluation/evaluationDelete',{
    data:{
      evaluationId: rowData.evaluationId
    } 
  })
  console.log(res.code)
  console.log(res)
  updateTableData()
  if(res.data.code==200){
     ElMessage({
       message: '删除成功！',
       type: 'success',
       offset: 150
     })
  }
  else{
    ElMessage({
      message: '删除失败，请重试！',
      type: 'error',
      offset: 150
    })
  }
}

</script>

<style lang="scss" scoped>
el-table{
  text-align: center;
}

.query{
  float: right;
  border-right: 0px;
  margin-bottom: 10px;
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