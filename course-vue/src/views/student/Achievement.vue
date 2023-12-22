<!-- 成果奖励 -->
<template>
    <div class="achievement-prac">
      <div>
        <!-- 新增，Dialog是弹窗，需要弹窗的页面都要在components文件夹下新增一个xxDialog.vue -->
        <AchievementDialog v-model:show="show" :rowData="currentRowData" :dialogMode="dialogMode" @updateTable="updateTableData" />
        <el-button @click="handleAdd" color="#6FB6C1" class="add">新 增</el-button>
        <!-- 查询部分 -->
        <div class="query">
          <el-input
              clearable
              class="search"
              v-model="inputSearch"
              placeholder="搜索名称..."
              @keyup.enter="searchFn"
            />
        <el-button type="primary" @click="searchFn" color="#6FB6C1" class="searchBtn">查 询</el-button>
        </div>
      </div>
      <!-- 表格部分 -->
      <el-table border
        :data="paginatedTableData" 
        style="width: 100%">
  
          <el-table-column prop="achievementName" label="成就名称" width="auto" align="center" />   
          <el-table-column prop="type" label="成就类别" width="auto" align="center"/> 
          <el-table-column prop="level" label="成就级别" width="auto" align="center"/> 
          <!-- 时间 -->
          <el-table-column sortable prop="time" label="时间" width="auto" align="center"/>   
          <el-table-column prop="content" label="内容" width="auto" align="center"/>   
          <!-- 审核状态，做了筛选 -->
          <el-table-column
            prop="statusName"
            label="审核状态"
            width="auto"
            align="center"
            :filters="[
              { text: '已通过', value: '已通过' },
              { text: '待审核', value: '待审核' },
              { text: '未通过', value: '未通过' },
            ]"
            :filter-method="filterStatus"
          />
          <el-table-column label="操作" width="auto" align="center">
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
      <!-- 分页相关，可以直接复制 -->
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
    import AchievementDialog from '../../components/AchievementDialog.vue'
    import { ElMessage } from 'element-plus'
    import request from '../../request/axios_config.js'
    
    const tableData = ref([])
    onMounted(() => {
      // 发起请求获取当前表格数据
      updateTableData()
    })
    const updateTableData = async () => {
      const res = await request.get('/achievement/getStudentAchievement')
      tableData.value = res.data.data
    }
    // 弹窗的显示
    const show = ref(false)
    
    // 右上角搜索框查询后的数据内容
    const search = ref('')
    const inputSearch = ref('')
    // 根据搜索框内容搜索, 空参搜索的话相当于显示所有
    const searchedTableData = computed(() => tableData.value.filter(
      //这个item相当于给每一列一个命名，可以不改
      item =>
        //空参的情况
        !search.value ||
        // 按照名称搜索，忽略大小写
        item.achievementName.toLowerCase().includes(search.value.toLowerCase())
    ))
    const searchFn = () => {
      // 点击查询按钮后才开始查询
      search.value = inputSearch.value
    }
    
    //筛选审核状态
    const filterStatus = (value,row) => {
      return row.statusName === value
    }
    
    // 分页相关,不需要修改，直接复制
    // 当前页数, 默认设置为1
    const currentPage = ref(1)
    const pageSize = ref(15)
    const paginatedTableData = computed(() => searchedTableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
    const handleSizeChange = () => {
      currentPage.value = 1
    }
    
    
    // 操作相关
    const currentRowData = ref({})
    const dialogMode = ref('') // 'add' 或 'view'
    const handleAdd = () => {
      currentRowData.value = {}  // 清空数据
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
    async function handleDel(rowData)  {
      const res = await request.post('/achievement/achievementDelete',{
        data:{
          achievementId: rowData.achievementId
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