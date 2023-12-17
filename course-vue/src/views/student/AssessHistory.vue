<!-- 学生互评评价历史界面 -->
<template>
    <div class="assess-history">
        <div>
            <AssessHistoryDialog v-model:show="show" :rowData="currentRowData" :dialogMode="dialogMode" @updateTable="updateTableData" />
            <!-- 表格顶部按键 -->
            <div>
                <el-button class="seeBtn" color="#6FB6C1" @click="seePeer">查看我的同学</el-button>
                <el-button class="seeBtn" color="#6FB6C1" @click="seeMyAssess">查看我的评论</el-button>
                <!-- 查询部分 -->
                <div class="query">
                  <el-input
                      clearable
                      class="search"
                      v-model="inputSearch"
                      placeholder="搜索被评人学号和姓名..."
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
            <el-table-column prop="student.person.name" label="被评人姓名" width="150" />
            <el-table-column prop="student.person.num" label="被评人学号" width="200"/>
            <el-table-column prop="evalTime" label="评价时间" width="180" />
            <el-table-column prop="eval" label="评价内容" width="300"/>
            <el-table-column label="操作" width="180" >
            <!-- 操作部分，根据需要修改 -->
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
import router from "~/router";
import AssessHistoryDialog from '../../components/AssessHistoryDialog.vue'
import { ElMessage } from 'element-plus'
import request from '../../request/axios_config.js'

const tableData = ref([])
onMounted(() => {
  // 发起请求获取当前表格数据
  updateTableData()
})
const updateTableData = async () => {
  const res = await request.get('/evaluation/getMySendEvaluationList')
  tableData.value = res.data.data
}

const show = ref(false)

const search = ref('')
const inputSearch = ref('')
// 根据搜索框内容搜索, 空参搜索的话相当于显示所有
const searchedTableData = computed(() => tableData.value.filter(
  //这个item相当于给每一列一个命名，可以不改
  item =>
    //空参的情况
    !search.value ||
    // 按照主题theme和团队名称groupName搜索，忽略大小写
    item.student.person.name.toLowerCase().includes(search.value.toLowerCase()) ||
    item.student.person.num.toLowerCase().includes(search.value.toLowerCase())
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
//查看
const handleEdit = (rowData) => {
  console.log(rowData)
  currentRowData.value = rowData
  dialogMode.value = 'view'
  show.value = true
}
async function handleDel(rowData)  {
  console.log(rowData)
  const res = await request.post('/evaluation/evaluationDelete',{
    data:{
        evaluationId: rowData.evaluationId
    } 
  })
  console.log(res.data)
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

// 切换页面
const seePeer = () =>{
    router.push('peer-class')
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