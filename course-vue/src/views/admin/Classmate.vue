<template>
    <div class="classmate">
        <div>
          <div class="className">{{ className }}</div>
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
        <el-table border
          :data="paginatedTableData"
          style="width: 100%"
        >
            <el-table-column prop="num" label="学号" width="auto" align="center"/>
            <el-table-column prop="name" label="姓名" width="auto" align="center"/>
            <el-table-column prop="genderName" label="性别" width="auto" align="center"/>
            <el-table-column prop="card" label="身份证号" width="auto" align="center"/>
            <el-table-column prop="borthday" label="出生日期" width="auto" align="center"/>
            <el-table-column prop="phone" label="电话号码" width="auto" align="center"/>            
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
import { ElMessage } from 'element-plus'
import request from '../../request/axios_config.js'

import { useAppStore } from '../../stores/app.ts'
import { storeToRefs } from 'pinia'

const store = useAppStore()

const { classmate } = storeToRefs(store);
const className = classmate.value.clazzName
const tableData = ref([])
onMounted(() => {
  // 发起请求获取当前表格数据
  updateTableData()
})

const updateTableData = async () => {
  console.log(classmate.value.clazzName)
  const res = await request.post('/student/getStudentOptionItemListByClazzId',{
    data:{
        clazzId:classmate.value.clazzId
    }
  })
  console.log(res.data.data)
  tableData.value = res.data.data
}

const search = ref('')
const inputSearch = ref('')

const searchedTableData = computed(() => tableData.value.filter(
  //这个item相当于给每一列一个命名，可以不改
  item =>
    //空参的情况
    !search.value ||
    // 按照主题theme和团队名称groupName搜索，忽略大小写
    item.num.toLowerCase().includes(search.value.toLowerCase()) ||
    item.name.toLowerCase().includes(search.value.toLowerCase())
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
async function handleDel(rowData)  {
  const res = await request.post('/clazz/studentDelete',{
    data:{
      studentId: rowData.studentId
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
.className{
  margin-top: 10px;
  margin-left: 20px;
  font-size: 18px;
  font-weight: bold;
  float: left;
  color: rgb(68, 68, 68);
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