<!-- 班级管理 -->
<template>
    <div class="clas-manage">
        <div>
        <ClassManageDialog v-model:show="show" :rowData="currentRowData" :dialogMode="dialogMode" @updateTable="updateTableData" />
        <el-button @click="handleAdd" color="#6FB6C1" class="add">新 增</el-button>
        <div class="select">
            <el-select v-model="campus" value-key="id"
                       placeholder="请选择学院" clearable @change="changeSelect">
                <el-option
                   v-for="item in campusType"
                   :key="item.id"
                   :label="item.label"
                   :value="item.value"
                   />
            </el-select>
        </div>
        <div class="select">
            <el-select v-model="grade" value-key="id"
                       placeholder="请选择年级" clearable @change="changeSelect">
                <el-option
                   v-for="item in gradeType"
                   :key="item.id"
                   :label="item.label"
                   :value="item.value"
                   />
            </el-select>
        </div>
        <el-button class="query" type="primary" size="default" @click="fetchDate(campus,grade)">查 询</el-button>

        <div class="query">
            <el-input
                clearable
                class="search"
                v-model="inputSearch"
                placeholder="搜索主题和团队名称..."
                @keyup.enter="searchFn"
            />
            <el-button type="primary" @click="searchFn" color="#6FB6C1" class="searchBtn">查 询</el-button>
        </div>
    </div>
    <el-table
     :data="paginatedTableData"
     style="width: 100%">
        <el-table-column prop="clazzName" label="班级名称" width="200" />
        <el-table-column prop="grade" label="年级" width="300" />
        <el-table-column prop="campus" label="学院" width="250" />
        <el-table-column label="操作" width="180" >
        <!-- 操作部分，根据需要修改 -->
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
import SocialDialog from '../../components/ClassManageDialog.vue'
import { ElMessage } from 'element-plus'
import request from '../../request/axios_config.js'

import { filterOptions } from '@/assets/config.js'

const campus = ref('')
const grade = ref('')

const campusType = computed(() => filterOptions.allCampuses)
const gradeType = computed(() => filterOptions.allGrades) 

const tableData = ref([])
onMounted(async ()=> {
    updateTableData()
})

const updateTableData = async () => {
  const res = await request.post('/clazz/getClazzOptionItemListByGradeId',{
    data:{}
  })
  tableData.value = res.data.data
}

const show = ref(false)

const search = ref('')
const inputSearch = ref('')

const searchedTableData = computed(() => tableData.value.filter(
    item =>
    //空参的情况
    !search.value ||
    // 按照主题theme和团队名称groupName搜索，忽略大小写
    item.clazzName.toLowerCase().includes(search.value.toLowerCase())
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
  const res = await request.post('/clazz/clazzDelete',{
    data:{
      clazzId: rowData.clazzId
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

const filiterTableData = tableData.value
const fetchDate = (campus, grade) => {
    filiterTableData.value = filiterTableData.filter(
        person =>
            person.campus.toLowerCase().includes(campus) && 
            person.grade.toLowerCase().includes(grade)
    )
    console.log(filiterTableData.value) 
}

const table = computed(() => filiterTableData.value)

export const filterOptions = {
    allCampuses: [
        {id: 1, label: '软件学院', value : 1}
        {id: 2, label: '集成电路学院', value : 2}
        {id: 3, label: '计算机科学与技术学院', value : 3}
        {id: 4, label: '基础医学院', value : 4}
        {id: 5, label: '电气与自动化学院', value : 5}
        {id: 6, label: '外语学院', value : 6}
        {id: 7, label: '药学院', value : 7}
        {id: 8, label: '物理学院', value : 8}
    ]
    allGrades: [
        {id: 1, label: '大一', value : 1}
        {id: 2, label: '大二', value : 2}
        {id: 3, label: '大三', value : 3}
        {id: 4, label: '大四', value : 4}
    ]
}


</script>